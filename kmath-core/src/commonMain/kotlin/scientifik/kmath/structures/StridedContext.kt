package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement

abstract class StridedNDField<T, F : Field<T>>(final override val shape: IntArray) : NDField<T, F, NDBuffer<T>> {
    val strides = DefaultStrides(shape)

    abstract fun buildBuffer(size: Int, initializer: (Int) -> T): Buffer<T>

    /**
     * Convert any [NDStructure] to buffered structure using strides from this context.
     * If the structure is already [NDBuffer], conversion is free. If not, it could be expensive because iteration over indexes
     *
     * If the argument is [NDBuffer] with different strides structure, the new element will be produced.
     */
    fun NDStructure<T>.toBuffer(): NDBuffer<T> {
        return if (this is NDBuffer<T> && this.strides == this@StridedNDField.strides) {
            this
        } else {
            produce { index -> get(index) }
        }
    }

    fun NDBuffer<T>.toElement(): StridedNDFieldElement<T, F> =
        StridedNDFieldElement(this@StridedNDField, buffer)
}

class StridedNDFieldElement<T, F : Field<T>>(
    override val context: StridedNDField<T, F>,
    override val buffer: Buffer<T>
) :
    NDBuffer<T>,
    FieldElement<NDBuffer<T>, StridedNDFieldElement<T, F>, StridedNDField<T, F>>,
    NDElement<T, F> {

    override val elementField: F
        get() = context.elementContext

    override fun unwrap(): NDBuffer<T> =
        this

    override fun NDBuffer<T>.wrap(): StridedNDFieldElement<T, F> =
        StridedNDFieldElement(context, this.buffer)

    override val strides
        get() = context.strides

    override val shape: IntArray
        get() = context.shape

    override fun get(index: IntArray): T =
        buffer[strides.offset(index)]

    override fun elements(): Sequence<Pair<IntArray, T>> =
        strides.indices().map { it to get(it) }

    override fun map(action: F.(T) -> T) =
        context.run { map(this@StridedNDFieldElement, action) }.wrap()

    override fun mapIndexed(transform: F.(index: IntArray, T) -> T) =
        context.run { mapIndexed(this@StridedNDFieldElement, transform) }.wrap()
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T : Any, F : Field<T>> Function1<T, T>.invoke(ndElement: StridedNDFieldElement<T, F>) =
    ndElement.context.run { ndElement.map { invoke(it) } }

/* plus and minus */

/**
 * Summation operation for [StridedNDFieldElement] and single element
 */
operator fun <T : Any, F : Field<T>> StridedNDFieldElement<T, F>.plus(arg: T) =
    context.run { map { it + arg } }

/**
 * Subtraction operation between [StridedNDFieldElement] and single element
 */
operator fun <T : Any, F : Field<T>> StridedNDFieldElement<T, F>.minus(arg: T) =
    context.run { map { it - arg } }

/* prod and div */

/**
 * Product operation for [StridedNDFieldElement] and single element
 */
operator fun <T : Any, F : Field<T>> StridedNDFieldElement<T, F>.times(arg: T) =
    context.run { map { it * arg } }

/**
 * Division operation between [StridedNDFieldElement] and single element
 */
operator fun <T : Any, F : Field<T>> StridedNDFieldElement<T, F>.div(arg: T) =
    context.run { map { it / arg } }