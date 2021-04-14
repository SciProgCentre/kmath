package space.kscience.kmath.nd

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface BufferAlgebraND<T, A : Algebra<T>> : AlgebraND<T, A> {
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

public open class BufferedGroupND<T, A : Group<T>>(
    final override val shape: IntArray,
    final override val elementContext: A,
    final override val bufferFactory: BufferFactory<T>,
) : GroupND<T, A>, BufferAlgebraND<T, A> {
    override val strides: Strides = DefaultStrides(shape)
    override val zero: BufferND<T> by lazy { produce { zero } }
    override fun StructureND<T>.unaryMinus(): StructureND<T> = produce { -get(it) }
}

public open class BufferedRingND<T, R : Ring<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedGroupND<T, R>(shape, elementContext, bufferFactory), RingND<T, R> {
    override val one: BufferND<T> by lazy { produce { one } }
}

public open class BufferedFieldND<T, R : Field<T>>(
    shape: IntArray,
    elementContext: R,
    bufferFactory: BufferFactory<T>,
) : BufferedRingND<T, R>(shape, elementContext, bufferFactory), FieldND<T, R> {

    override fun scale(a: StructureND<T>, value: Double): StructureND<T> = a.map { it * value }
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