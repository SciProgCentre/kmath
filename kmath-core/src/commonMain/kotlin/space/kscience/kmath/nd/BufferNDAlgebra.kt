/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface BufferNDAlgebra<T, A : Algebra<T>> : NDAlgebra<T, A> {
    public val strides: Strides
    public val bufferFactory: BufferFactory<T>

    override fun produce(initializer: A.(IntArray) -> T): NDBuffer<T> = NDBuffer(
        strides,
        bufferFactory(strides.linearSize) { offset ->
            elementContext.initializer(strides.index(offset))
        }
    )

    public val NDStructure<T>.buffer: Buffer<T>
        get() = when {
            !shape.contentEquals(this@BufferNDAlgebra.shape) -> throw ShapeMismatchException(
                this@BufferNDAlgebra.shape,
                shape
            )
            this is NDBuffer && this.strides == this@BufferNDAlgebra.strides -> this.buffer
            else -> bufferFactory(strides.linearSize) { offset -> get(strides.index(offset)) }
        }

    override fun NDStructure<T>.map(transform: A.(T) -> T): NDBuffer<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(buffer[offset])
        }
        return NDBuffer(strides, buffer)
    }

    override fun NDStructure<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): NDBuffer<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(
                strides.index(offset),
                buffer[offset]
            )
        }
        return NDBuffer(strides, buffer)
    }

    override fun combine(a: NDStructure<T>, b: NDStructure<T>, transform: A.(T, T) -> T): NDBuffer<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(a.buffer[offset], b.buffer[offset])
        }
        return NDBuffer(strides, buffer)
    }
}

public open class BufferedNDGroup<T, A : Group<T>>(
    final override val shape: IntArray,
    final override val elementContext: A,
    final override val bufferFactory: BufferFactory<T>,
) : NDGroup<T, A>, BufferNDAlgebra<T, A> {
    override val strides: Strides = DefaultStrides(shape)
    override val zero: NDBuffer<T> by lazy { produce { zero } }
    override fun NDStructure<T>.unaryMinus(): NDStructure<T> = produce { -get(it) }
}

public open class BufferedNDRing<T, R : Ring<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedNDGroup<T, R>(shape, elementContext, bufferFactory), NDRing<T, R> {
    override val one: NDBuffer<T> by lazy { produce { one } }
}

public open class BufferedNDField<T, R : Field<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedNDRing<T, R>(shape, elementContext, bufferFactory), NDField<T, R> {

    override fun scale(a: NDStructure<T>, value: Double): NDStructure<T> = a.map { it * value }
}

// group factories
public fun <T, A : Group<T>> NDAlgebra.Companion.group(
    space: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedNDGroup<T, A> = BufferedNDGroup(shape, space, bufferFactory)

public inline fun <T, A : Group<T>, R> A.ndGroup(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedNDGroup<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return NDAlgebra.group(this, bufferFactory, *shape).run(action)
}

//ring factories
public fun <T, A : Ring<T>> NDAlgebra.Companion.ring(
    ring: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedNDRing<T, A> = BufferedNDRing(shape, ring, bufferFactory)

public inline fun <T, A : Ring<T>, R> A.ndRing(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedNDRing<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return NDAlgebra.ring(this, bufferFactory, *shape).run(action)
}

//field factories
public fun <T, A : Field<T>> NDAlgebra.Companion.field(
    field: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedNDField<T, A> = BufferedNDField(shape, field, bufferFactory)

@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any, A : Field<T>> NDAlgebra.Companion.auto(
    field: A,
    vararg shape: Int,
): NDField<T, A> = when (field) {
    RealField -> RealNDField(shape) as NDField<T, A>
    else -> BufferedNDField(shape, field, Buffer.Companion::auto)
}

public inline fun <T, A : Field<T>, R> A.ndField(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedNDField<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return NDAlgebra.field(this, bufferFactory, *shape).run(action)
}