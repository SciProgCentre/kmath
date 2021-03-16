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

public interface BufferAlgebraND<T, out A : Algebra<T>> : AlgebraND<T, A> {
    public val strides: Strides
    public val bufferFactory: BufferFactory<T>

    override fun produce(initializer: A.(IntArray) -> T): BufferND<T> = BufferND(
        strides,
        bufferFactory(strides.linearSize) { offset ->
            elementContext.initializer(strides.index(offset))
        }
    )

    public val StructureND<T>.buffer: Buffer<T>
        get() = when {
            !shape.contentEquals(this@BufferAlgebraND.shape) -> throw ShapeMismatchException(
                this@BufferAlgebraND.shape,
                shape
            )
            this is BufferND && this.strides == this@BufferAlgebraND.strides -> this.buffer
            else -> bufferFactory(strides.linearSize) { offset -> get(strides.index(offset)) }
        }

    override fun StructureND<T>.map(transform: A.(T) -> T): BufferND<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(buffer[offset])
        }
        return BufferND(strides, buffer)
    }

    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): BufferND<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(
                strides.index(offset),
                buffer[offset]
            )
        }
        return BufferND(strides, buffer)
    }

    override fun combine(a: StructureND<T>, b: StructureND<T>, transform: A.(T, T) -> T): BufferND<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(a.buffer[offset], b.buffer[offset])
        }
        return BufferND(strides, buffer)
    }
}

public open class BufferedGroupND<T, out G : Group<T>>(
    final override val shape: IntArray,
    final override val elementContext: G,
    final override val bufferFactory: BufferFactory<T>,
) : GroupND<T, G>, BufferAlgebraND<T, G> {
    override val strides: Strides = DefaultStrides(shape)
    override val zero: BufferND<T> by lazy { produce { zero } }
    override fun StructureND<T>.unaryMinus(): StructureND<T> = produce { -get(it) }
}

public open class BufferedRingND<T, out R : Ring<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedGroupND<T, R>(shape, elementContext, bufferFactory), RingND<T, R> {
    override val one: BufferND<T> by lazy { produce { one } }
}

public open class BufferedFieldND<T, out F : Field<T>>(
    shape: IntArray,
    elementContext: F,
    bufferFactory: BufferFactory<T>,
) : BufferedRingND<T, F>(shape, elementContext, bufferFactory), FieldND<T, F> {

    override fun scale(a: StructureND<T>, value: Double): StructureND<T> = a.map { it * value }
}

public open class BufferedExtendedFieldND<T, out F : ExtendedField<T>>(
    shape: IntArray,
    elementContext: F,
    bufferFactory: BufferFactory<T>,
) : BufferedFieldND<T, F>(shape, elementContext, bufferFactory), ExtendedFieldND<T, F> {
    public override fun sin(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.sin(it) }
    public override fun cos(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.cos(it) }
    public override fun asin(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.asin(it) }
    public override fun acos(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.acos(it) }
    public override fun atan(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.atan(it) }
    public override fun power(arg: StructureND<T>, pow: Number): StructureND<T> =
        arg.map { elementContext.power(it, pow) }

    public override fun exp(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.exp(it) }
    public override fun ln(arg: StructureND<T>): StructureND<T> = arg.map { elementContext.ln(it) }
}

// group factories
public fun <T, A : Ring<T>> AlgebraND.Companion.group(
    space: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedGroupND<T, A> = BufferedGroupND(shape, space, bufferFactory)

public inline fun <T, A : Ring<T>, R> A.ndGroup(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedGroupND<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return AlgebraND.group(this, bufferFactory, *shape).run(action)
}

//ring factories
public fun <T, A : Ring<T>> AlgebraND.Companion.ring(
    ring: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedRingND<T, A> = BufferedRingND(shape, ring, bufferFactory)

public inline fun <T, A : Ring<T>, R> A.ndRing(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedRingND<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return AlgebraND.ring(this, bufferFactory, *shape).run(action)
}

//field factories
public fun <T, A : Field<T>> AlgebraND.Companion.field(
    field: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedFieldND<T, A> = BufferedFieldND(shape, field, bufferFactory)

@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any, A : Field<T>> AlgebraND.Companion.auto(
    field: A,
    vararg shape: Int,
): FieldND<T, A> = when (field) {
    DoubleField -> DoubleFieldND(shape) as FieldND<T, A>
    else -> BufferedFieldND(shape, field, Buffer.Companion::auto)
}

public inline fun <T, A : Field<T>, R> A.ndField(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedFieldND<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return AlgebraND.field(this, bufferFactory, *shape).run(action)
}

public fun <T, A : ExtendedField<T>> AlgebraND.Companion.extendedField(
    field: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedExtendedFieldND<T, A> = BufferedExtendedFieldND(shape, field, bufferFactory)

@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any, A : ExtendedField<T>> AlgebraND.Companion.auto(
    field: A,
    vararg shape: Int,
): FieldND<T, A> = when (field) {
    DoubleField -> DoubleFieldND(shape) as FieldND<T, A>
    else -> BufferedFieldND(shape, field, Buffer.Companion::auto)
}

public inline fun <T, A : ExtendedField<T>, R> A.ndExtendedField(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedExtendedFieldND<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return AlgebraND.extendedField(this, bufferFactory, *shape).run(action)
}
