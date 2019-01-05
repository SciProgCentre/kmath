package scientifik.kmath.structures

import scientifik.kmath.operations.AbstractField
import scientifik.kmath.operations.Field

/**
 * An exception is thrown when the expected ans actual shape of NDArray differs
 */
class ShapeMismatchException(val expected: IntArray, val actual: IntArray) : RuntimeException()

/**
 * Field for n-dimensional arrays.
 * @param shape - the list of dimensions of the array
 * @param elementField - operations field defined on individual array element
 * @param T - the type of the element contained in ND structure
 * @param F - field over structure elements
 * @param R - actual nd-element type of this field
 */
interface NDField<T, F : Field<T>, N : NDStructure<T>> : Field<N> {

    val shape: IntArray
    val elementField: F

    fun produce(initializer: F.(IntArray) -> T): N

    fun map(arg: N, transform: F.(T) -> T): N

    fun mapIndexed(arg: N, transform: F.(index: IntArray, T) -> T): N

    fun combine(a: N, b: N, transform: F.(T, T) -> T): N

    /**
     * Element by element application of any operation on elements to the whole array. Just like in numpy
     */
    operator fun Function1<T, T>.invoke(structure: N): N

    /**
     * Summation operation for [NDElements] and single element
     */
    operator fun N.plus(arg: T): N

    /**
     * Subtraction operation between [NDElements] and single element
     */
    operator fun N.minus(arg: T): N

    /**
     * Product operation for [NDElements] and single element
     */
    operator fun N.times(arg: T): N

    /**
     * Division operation between [NDElements] and single element
     */
    operator fun N.div(arg: T): N

    operator fun T.plus(arg: N): N
    operator fun T.minus(arg: N): N
    operator fun T.times(arg: N): N
    operator fun T.div(arg: N): N

    companion object {
        /**
         * Create a nd-field for [Double] values
         */
        fun real(shape: IntArray) = RealNDField(shape)

        /**
         * Create a nd-field with boxing generic buffer
         */
        fun <T : Any, F : Field<T>> generic(shape: IntArray, field: F) = GenericNDField(shape, field)

        /**
         * Create a most suitable implementation for nd-field using reified class.
         */
        inline fun <reified T : Any, F : Field<T>> auto(shape: IntArray, field: F): BufferNDField<T, F> {
            return BufferNDField(shape, field, ::autoBuffer)
        }
    }
}


abstract class AbstractNDField<T, F : Field<T>, N : NDStructure<T>>(
    override val shape: IntArray,
    override val elementField: F
) : AbstractField<N>(), NDField<T, F, N> {
    override val zero: N  by lazy { produce { zero } }

    override val one: N by lazy { produce { one } }

    final override operator fun Function1<T, T>.invoke(structure: N) = map(structure) { value -> this@invoke(value) }
    final override operator fun N.plus(arg: T) = map(this) { value -> elementField.run { arg + value } }
    final override operator fun N.minus(arg: T) = map(this) { value -> elementField.run { arg - value } }
    final override operator fun N.times(arg: T) = map(this) { value -> elementField.run { arg * value } }
    final override operator fun N.div(arg: T) = map(this) { value -> elementField.run { arg / value } }

    final override operator fun T.plus(arg: N) = arg + this
    final override operator fun T.minus(arg: N) = arg - this
    final override operator fun T.times(arg: N) = arg * this
    final override operator fun T.div(arg: N) = arg / this


    /**
     * Element-by-element addition
     */
    override fun add(a: N, b: N): N =
        combine(a, b) { aValue, bValue -> aValue + bValue }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: N, k: Double): N =
        map(a) { it * k }


    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: N, b: N): N =
        combine(a, b) { aValue, bValue -> aValue * bValue }

    /**
     * Element-by-element division
     */
    override fun divide(a: N, b: N): N =
        combine(a, b) { aValue, bValue -> aValue / bValue }

    /**
     * Check if given objects are compatible with this context. Throw exception if they are not
     */
    open fun check(vararg elements: N) {
        elements.forEach {
            if (!shape.contentEquals(it.shape)) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }
}

class GenericNDField<T : Any, F : Field<T>>(
    shape: IntArray,
    elementField: F,
    val bufferFactory: BufferFactory<T> = ::boxingBuffer
) : AbstractNDField<T, F, NDStructure<T>>(shape, elementField) {

    override fun produce(initializer: F.(IntArray) -> T): NDStructure<T> =
        ndStructure(shape, bufferFactory) { elementField.initializer(it) }

    override fun map(arg: NDStructure<T>, transform: F.(T) -> T): NDStructure<T> =
        produce { index -> transform(arg.get(index)) }

    override fun mapIndexed(arg: NDStructure<T>, transform: F.(index: IntArray, T) -> T): NDStructure<T> =
        produce { index -> transform(index, arg.get(index)) }

    override fun combine(a: NDStructure<T>, b: NDStructure<T>, transform: F.(T, T) -> T): NDStructure<T> =
        produce { index -> transform(a[index], b[index]) }
}