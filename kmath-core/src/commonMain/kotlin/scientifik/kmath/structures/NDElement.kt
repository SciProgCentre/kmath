package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space


interface NDElement<T, C, N : NDStructure<T>> : NDStructure<T> {

    val context: NDAlgebra<T, C, N>

    fun unwrap(): N

    fun N.wrap(): NDElement<T, C, N>

    fun mapIndexed(transform: C.(index: IntArray, T) -> T) = context.mapIndexed(unwrap(), transform).wrap()
    fun map(transform: C.(T) -> T) = context.map(unwrap(), transform).wrap()

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
        ): BufferedNDElement<T, F> {
            val ndField = BoxingNDField(shape, field, Buffer.Companion::boxing)
            return ndField.produce(initializer)
        }

        inline fun <reified T : Any, F : Field<T>> auto(
            shape: IntArray,
            field: F,
            noinline initializer: F.(IntArray) -> T
        ): BufferedNDFieldElement<T, F> {
            val ndField = NDField.auto(shape, field)
            return BufferedNDFieldElement(ndField, ndField.produce(initializer).buffer)
        }
    }
}

/**
 * Element by element application of any operation on elements to the whole [NDElement]
 */
operator fun <T, C> Function1<T, T>.invoke(ndElement: NDElement<T, C, *>) =
    ndElement.map { value -> this@invoke(value) }

/* plus and minus */

/**
 * Summation operation for [NDElement] and single element
 */
operator fun <T, S : Space<T>> NDElement<T, S, *>.plus(arg: T) =
    map { value -> arg + value }

/**
 * Subtraction operation between [NDElement] and single element
 */
operator fun <T, S : Space<T>> NDElement<T, S, *>.minus(arg: T) =
    map { value -> arg - value }

/* prod and div */

/**
 * Product operation for [NDElement] and single element
 */
operator fun <T, R : Ring<T>> NDElement<T, R, *>.times(arg: T) =
    map { value -> arg * value }

/**
 * Division operation between [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F, *>.div(arg: T) =
    map { value -> arg / value }


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
