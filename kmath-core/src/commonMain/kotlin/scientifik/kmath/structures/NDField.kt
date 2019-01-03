package scientifik.kmath.structures

import scientifik.kmath.operations.Field

/**
 * An exception is thrown when the expected ans actual shape of NDArray differs
 */
class ShapeMismatchException(val expected: IntArray, val actual: IntArray) : RuntimeException()

/**
 * Field for n-dimensional arrays.
 * @param shape - the list of dimensions of the array
 * @param field - operations field defined on individual array element
 * @param T - the type of the element contained in ND structure
 * @param F - field over structure elements
 * @param R - actual nd-element type of this field
 */
interface NDField<T, F : Field<T>, N : NDStructure<out T>> : Field<N> {

    val shape: IntArray
    val field: F

    /**
     * Check the shape of given NDArray and throw exception if it does not coincide with shape of the field
     */
    fun checkShape(vararg elements: N) {
        elements.forEach {
            if (!shape.contentEquals(it.shape)) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }

    fun produce(initializer: F.(IntArray) -> T): N


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
         * Create a most suitable implementation for nd-field using reified class
         */
        inline fun <reified T : Any, F : Field<T>> inline(shape: IntArray, field: F) = BufferNDField(shape, field, ::inlineBuffer)
    }
}


class GenericNDField<T : Any, F : Field<T>>(override val shape: IntArray, override val field: F, val bufferFactory: BufferFactory<T> = ::boxingBuffer) : NDField<T, F, NDStructure<T>> {
    override fun produce(initializer: F.(IntArray) -> T): NDStructure<T> = ndStructure(shape, bufferFactory) { field.initializer(it) }

    override val zero: NDStructure<T>  by lazy { produce { zero } }

    override val one: NDStructure<T> by lazy { produce { one } }


    /**
     * Element-by-element addition
     */
    override fun add(a: NDStructure<T>, b: NDStructure<T>): NDStructure<T> {
        checkShape(a, b)
        return produce { field.run { a[it] + b[it] } }
    }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: NDStructure<T>, k: Double): NDStructure<T> {
        checkShape(a)
        return produce { field.run { a[it] * k } }
    }

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: NDStructure<T>, b: NDStructure<T>): NDStructure<T> {
        checkShape(a)
        return produce { field.run { a[it] * b[it] } }
    }

    /**
     * Element-by-element division
     */
    override fun divide(a: NDStructure<T>, b: NDStructure<T>): NDStructure<T> {
        checkShape(a)
        return produce { field.run { a[it] / b[it] } }
    }
}