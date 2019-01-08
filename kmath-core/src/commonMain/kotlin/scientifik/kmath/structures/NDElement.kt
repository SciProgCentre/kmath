package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Space


interface NDElement<T, S : Space<T>> : NDStructure<T> {
    val elementField: S

    fun mapIndexed(transform: S.(index: IntArray, T) -> T): NDElement<T, S>
    fun map(action: S.(T) -> T) = mapIndexed { _, value -> action(value) }

    companion object {
        /**
         * Create a optimized NDArray of doubles
         */
        fun real(shape: IntArray, initializer: RealField.(IntArray) -> Double = { 0.0 }) =
            NDField.real(shape).produce(initializer)


        fun real1D(dim: Int, initializer: (Int) -> Double = { _ -> 0.0 }) =
            real(intArrayOf(dim)) { initializer(it[0]) }


        fun real2D(dim1: Int, dim2: Int, initializer: (Int, Int) -> Double = { _, _ -> 0.0 }) =
            real(intArrayOf(dim1, dim2)) { initializer(it[0], it[1]) }

        fun real3D(dim1: Int, dim2: Int, dim3: Int, initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }) =
            real(intArrayOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }


        /**
         * Simple boxing NDArray
         */
        fun <T : Any, F : Field<T>> buffered(
            shape: IntArray,
            field: F,
            initializer: F.(IntArray) -> T
        ): StridedNDFieldElement<T, F> {
            val ndField = BufferNDField(shape, field, Buffer.Companion::boxing)
            return ndField.produce(initializer)
        }

        inline fun <reified T : Any, F : Field<T>> auto(
            shape: IntArray,
            field: F,
            noinline initializer: F.(IntArray) -> T
        ): StridedNDFieldElement<T, F> {
            val ndField = NDField.auto(shape, field)
            return StridedNDFieldElement(ndField, ndField.produce(initializer).buffer)
        }
    }
}


/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T, F : Field<T>> Function1<T, T>.invoke(ndElement: NDElement<T, F>) =
    ndElement.map { value -> this@invoke(value) }

/* plus and minus */

/**
 * Summation operation for [NDElements] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.plus(arg: T): NDElement<T, F> =
    this.map { value -> elementField.run { arg + value } }

/**
 * Subtraction operation between [NDElements] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.minus(arg: T): NDElement<T, F> =
    this.map { value -> elementField.run { arg - value } }

/* prod and div */

/**
 * Product operation for [NDElements] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.times(arg: T): NDElement<T, F> =
    this.map { value -> elementField.run { arg * value } }

/**
 * Division operation between [NDElements] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.div(arg: T): NDElement<T, F> =
    this.map { value -> elementField.run { arg / value } }


//    /**
//     * Reverse sum operation
//     */
//    operator fun T.plus(arg: NDStructure<T>): NDElement<T, F> = produce { index ->
//        field.run { this@plus + arg[index] }
//    }
//
//    /**
//     * Reverse minus operation
//     */
//    operator fun T.minus(arg: NDStructure<T>): NDElement<T, F> = produce { index ->
//        field.run { this@minus - arg[index] }
//    }
//
//    /**
//     * Reverse product operation
//     */
//    operator fun T.times(arg: NDStructure<T>): NDElement<T, F> = produce { index ->
//        field.run { this@times * arg[index] }
//    }
//
//    /**
//     * Reverse division operation
//     */
//    operator fun T.div(arg: NDStructure<T>): NDElement<T, F> = produce { index ->
//        field.run { this@div / arg[index] }
//    }


/**
 *  Read-only [NDStructure] coupled to the context.
 */
class GenericNDFieldElement<T, F : Field<T>>(
    override val context: NDField<T, F, NDStructure<T>>,
    private val structure: NDStructure<T>
) :
    NDStructure<T> by structure,
    NDElement<T, F>,
    FieldElement<NDStructure<T>, GenericNDFieldElement<T, F>, NDField<T, F, NDStructure<T>>> {
    override val elementField: F get() = context.elementContext

    override fun unwrap(): NDStructure<T> = structure

    override fun NDStructure<T>.wrap() = GenericNDFieldElement(context, this)

    override fun mapIndexed(transform: F.(index: IntArray, T) -> T) =
        ndStructure(context.shape) { index: IntArray -> context.elementContext.transform(index, get(index)) }.wrap()
}
