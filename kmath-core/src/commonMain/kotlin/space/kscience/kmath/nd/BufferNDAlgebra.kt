package space.kscience.kmath.nd

import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.Space
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface BufferNDAlgebra<T, C> : NDAlgebra<T, C> {
    public val strides: Strides
    public val bufferFactory: BufferFactory<T>

    override fun produce(initializer: C.(IntArray) -> T): NDBuffer<T> = NDBuffer(
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

    override fun NDStructure<T>.map(transform: C.(T) -> T): NDBuffer<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(buffer[offset])
        }
        return NDBuffer(strides, buffer)
    }

    override fun NDStructure<T>.mapIndexed(transform: C.(index: IntArray, T) -> T): NDBuffer<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(
                strides.index(offset),
                buffer[offset]
            )
        }
        return NDBuffer(strides, buffer)
    }

    override fun combine(a: NDStructure<T>, b: NDStructure<T>, transform: C.(T, T) -> T): NDBuffer<T> {
        val buffer = bufferFactory(strides.linearSize) { offset ->
            elementContext.transform(a.buffer[offset], b.buffer[offset])
        }
        return NDBuffer(strides, buffer)
    }
}

public open class BufferedNDSpace<T, R : Space<T>>(
    final override val shape: IntArray,
    final override val elementContext: R,
    final override val bufferFactory: BufferFactory<T>,
) : NDSpace<T, R>, BufferNDAlgebra<T, R> {
    override val strides: Strides = DefaultStrides(shape)
    override val zero: NDBuffer<T> by lazy { produce { zero } }
}

public open class BufferedNDRing<T, R : Ring<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedNDSpace<T, R>(shape, elementContext, bufferFactory), NDRing<T, R> {
    override val one: NDBuffer<T> by lazy { produce { one } }
}

public open class BufferedNDField<T, R : Field<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedNDRing<T, R>(shape, elementContext, bufferFactory), NDField<T, R>

// space factories
public fun <T, A : Space<T>> NDAlgebra.Companion.space(
    space: A,
    bufferFactory: BufferFactory<T>,
    vararg shape: Int,
): BufferedNDSpace<T, A> = BufferedNDSpace(shape, space, bufferFactory)

public inline fun <T, A : Space<T>, R> A.ndSpace(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: BufferedNDSpace<T, A>.() -> R,
): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return NDAlgebra.space(this, bufferFactory, *shape).run(action)
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