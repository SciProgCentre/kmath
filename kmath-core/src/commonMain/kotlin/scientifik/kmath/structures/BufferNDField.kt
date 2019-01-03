package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement

open class BufferNDField<T, F : Field<T>>(final override val shape: IntArray, final override val field: F, val bufferFactory: BufferFactory<T>) : NDField<T, F, NDBuffer<T>> {
    val strides = DefaultStrides(shape)

    override fun produce(initializer: F.(IntArray) -> T) =
            BufferNDElement(this, bufferFactory(strides.linearSize) { offset -> field.initializer(strides.index(offset)) })

    open fun NDBuffer<T>.map(transform: F.(T) -> T) =
            BufferNDElement(this@BufferNDField, bufferFactory(strides.linearSize) { offset -> field.transform(buffer[offset]) })

    open fun NDBuffer<T>.mapIndexed(transform: F.(index: IntArray, T) -> T) =
            BufferNDElement(this@BufferNDField, bufferFactory(strides.linearSize) { offset -> field.transform(strides.index(offset), buffer[offset]) })

    open fun combine(a: NDBuffer<T>, b: NDBuffer<T>, transform: F.(T, T) -> T) =
            BufferNDElement(this, bufferFactory(strides.linearSize) { offset -> field.transform(a[offset], b[offset]) })

    /**
     * Convert any [NDStructure] to buffered structure using strides from this context.
     * If the structure is already [NDBuffer], conversion is free. If not, it could be expensive because iteration over indexes
     */
    fun NDStructure<T>.toBuffer(): NDBuffer<T> =
            this as? NDBuffer<T> ?: produce { index -> get(index) }

    override val zero: NDBuffer<T> by lazy { produce { field.zero } }

    override fun add(a: NDBuffer<T>, b: NDBuffer<T>): NDBuffer<T> = combine(a, b) { aValue, bValue -> add(aValue, bValue) }

    override fun multiply(a: NDBuffer<T>, k: Double): NDBuffer<T> = a.map { it * k }

    override val one: NDBuffer<T> by lazy { produce { field.one } }

    override fun multiply(a: NDBuffer<T>, b: NDBuffer<T>): NDBuffer<T> = combine(a, b) { aValue, bValue -> multiply(aValue, bValue) }

    override fun divide(a: NDBuffer<T>, b: NDBuffer<T>): NDBuffer<T> = combine(a, b) { aValue, bValue -> divide(aValue, bValue) }
}

class BufferNDElement<T, F : Field<T>>(override val context: BufferNDField<T, F>, override val buffer: Buffer<T>) :
        NDBuffer<T>,
        FieldElement<NDBuffer<T>, BufferNDElement<T, F>, BufferNDField<T, F>>,
        NDElement<T, F> {

    override val elementField: F get() = context.field

    override fun unwrap(): NDBuffer<T> = this

    override fun NDBuffer<T>.wrap(): BufferNDElement<T, F> = BufferNDElement(context, this.buffer)

    override val strides get() = context.strides

    override val shape: IntArray get() = context.shape

    override fun get(index: IntArray): T = buffer[strides.offset(index)]

    override fun elements(): Sequence<Pair<IntArray, T>> = strides.indices().map { it to get(it) }

    override fun map(action: F.(T) -> T): BufferNDElement<T, F> = context.run { map(action) }

    override fun mapIndexed(transform: F.(index: IntArray, T) -> T): BufferNDElement<T, F> = context.run { mapIndexed(transform) }
}


/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T : Any, F : Field<T>> Function1<T, T>.invoke(ndElement: BufferNDElement<T, F>) =
        ndElement.context.run { ndElement.map { invoke(it) } }

/* plus and minus */

/**
 * Summation operation for [BufferNDElement] and single element
 */
operator fun <T : Any, F : Field<T>> BufferNDElement<T, F>.plus(arg: T) =
        context.run { map { it + arg } }

/**
 * Subtraction operation between [BufferNDElement] and single element
 */
operator fun <T : Any, F : Field<T>> BufferNDElement<T, F>.minus(arg: T) =
        context.run { map { it - arg } }

/* prod and div */

/**
 * Product operation for [BufferNDElement] and single element
 */
operator fun <T : Any, F : Field<T>> BufferNDElement<T, F>.times(arg: T) =
        context.run { map { it * arg } }

/**
 * Division operation between [BufferNDElement] and single element
 */
operator fun <T : Any, F : Field<T>> BufferNDElement<T, F>.div(arg: T) =
        context.run { map { it / arg } }