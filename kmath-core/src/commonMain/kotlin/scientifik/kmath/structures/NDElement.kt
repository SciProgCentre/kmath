package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

/**
 * The root for all [NDStructure] based algebra elements. Does not implement algebra element root because of problems with recursive self-types
 * @param T  the type of the element of the structure
 * @param C the type of the context for the element
 * @param N the type of the underlying [NDStructure]
 */
interface NDElement<T, C, N : NDStructure<T>> : NDStructure<T> {

    val context: NDAlgebra<T, C, N>

    fun unwrap(): N

    fun N.wrap(): NDElement<T, C, N>

    companion object {
        /**
         * Create a optimized NDArray of doubles
         */
        fun real(shape: IntArray, initializer: RealField.(IntArray) -> Double = { 0.0 }): RealNDElement =
            NDField.real(*shape).produce(initializer)

        inline fun real1D(dim: Int, crossinline initializer: (Int) -> Double = { _ -> 0.0 }): RealNDElement =
            real(intArrayOf(dim)) { initializer(it[0]) }

        inline fun real2D(
            dim1: Int,
            dim2: Int,
            crossinline initializer: (Int, Int) -> Double = { _, _ -> 0.0 }
        ): RealNDElement = real(intArrayOf(dim1, dim2)) { initializer(it[0], it[1]) }

        inline fun real3D(
            dim1: Int,
            dim2: Int,
            dim3: Int,
            crossinline initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }
        ): RealNDElement = real(intArrayOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }


        /**
         * Simple boxing NDArray
         */
        fun <T : Any, F : Field<T>> boxing(
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
            val ndField = NDField.auto(field, *shape)
            return BufferedNDFieldElement(ndField, ndField.produce(initializer).buffer)
        }
    }
}


fun <T, C, N : NDStructure<T>> NDElement<T, C, N>.mapIndexed(transform: C.(index: IntArray, T) -> T): NDElement<T, C, N> =
    context.mapIndexed(unwrap(), transform).wrap()

fun <T, C, N : NDStructure<T>> NDElement<T, C, N>.map(transform: C.(T) -> T): NDElement<T, C, N> =
    context.map(unwrap(), transform).wrap()

/**
 * Element by element application of any operation on elements to the whole [NDElement]
 */
operator fun <T, C, N : NDStructure<T>> Function1<T, T>.invoke(ndElement: NDElement<T, C, N>): NDElement<T, C, N> =
    ndElement.map { value -> this@invoke(value) }

/* plus and minus */

/**
 * Summation operation for [NDElement] and single element
 */
operator fun <T, S : Space<T>, N : NDStructure<T>> NDElement<T, S, N>.plus(arg: T): NDElement<T, S, N> =
    map { value -> arg + value }

/**
 * Subtraction operation between [NDElement] and single element
 */
operator fun <T, S : Space<T>, N : NDStructure<T>> NDElement<T, S, N>.minus(arg: T): NDElement<T, S, N> =
    map { value -> arg - value }

/* prod and div */

/**
 * Product operation for [NDElement] and single element
 */
operator fun <T, R : Ring<T>, N : NDStructure<T>> NDElement<T, R, N>.times(arg: T): NDElement<T, R, N> =
    map { value -> arg * value }

/**
 * Division operation between [NDElement] and single element
 */
operator fun <T, F : Field<T>, N : NDStructure<T>> NDElement<T, F, N>.div(arg: T): NDElement<T, F, N> =
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
