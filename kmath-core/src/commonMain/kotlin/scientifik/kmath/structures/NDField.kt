package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement

/**
 * An exception is thrown when the expected ans actual shape of NDArray differs
 */
class ShapeMismatchException(val expected: IntArray, val actual: IntArray) : RuntimeException()

/**
 * Field for n-dimensional arrays.
 * @property shape - the list of dimensions of the array
 * @property field - operations field defined on individual array element
 * @param T the type of the element contained in NDArray
 */
interface NDField<T, F : Field<T>> : Field<NDStructure<T>> {

    val shape: IntArray
    val field: F

    /**
     * Create new instance of NDArray using field shape and given initializer
     * The producer takes list of indices as argument and returns contained value
     */
    fun produce(initializer: F.(IntArray) -> T): NDElement<T, F>

    override val zero: NDElement<T, F> get() = produce { zero }

    override val one: NDElement<T, F> get() = produce { one }

    /**
     * Check the shape of given NDArray and throw exception if it does not coincide with shape of the field
     */
    fun checkShape(vararg elements: NDStructure<T>) =
        elements.forEach { if (!shape.contentEquals(it.shape)) throw ShapeMismatchException(shape, it.shape) }

    /**
     * Element-by-element addition
     */
    override fun add(a: NDStructure<T>, b: NDStructure<T>): NDElement<T, F> {
        checkShape(a, b)
        return produce { field.run { a[it] + b[it] } }
    }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: NDStructure<T>, k: Double): NDElement<T, F> {
        checkShape(a)
        return produce { field.run { a[it] * k } }
    }

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: NDStructure<T>, b: NDStructure<T>): NDElement<T, F> {
        checkShape(a)
        return produce { field.run { a[it] * b[it] } }
    }

    /**
     * Element-by-element division
     */
    override fun divide(a: NDStructure<T>, b: NDStructure<T>): NDElement<T, F> {
        checkShape(a)
        return produce { field.run { a[it] / b[it] } }
    }

    companion object {
        /**
         * Create a nd-field for [Double] values
         */
        fun real(shape: IntArray) = RealNDField(shape)

        /**
         * Create a nd-field with boxing generic buffer
         */
        fun <T : Any, F : Field<T>> generic(shape: IntArray, field: F) = BufferNDField(shape, field, ::boxingBuffer)

        /**
         * Create a most suitable implementation for nd-field using reified class
         */
        inline fun <reified T : Any, F : Field<T>> inline(shape: IntArray, field: F) = BufferNDField(shape, field, ::inlineBuffer)
    }
}


interface NDElement<T, F : Field<T>> : FieldElement<NDStructure<T>, NDField<T, F>>, NDStructure<T> {
    companion object {
        /**
         * Create a platform-optimized NDArray of doubles
         */
        fun real(shape: IntArray, initializer: DoubleField.(IntArray) -> Double = { 0.0 }): NDElement<Double, DoubleField> =
                NDField.real(shape).produce(initializer)

        fun real1D(dim: Int, initializer: (Int) -> Double = { _ -> 0.0 }): NDElement<Double, DoubleField> =
                real(intArrayOf(dim)) { initializer(it[0]) }

        fun real2D(dim1: Int, dim2: Int, initializer: (Int, Int) -> Double = { _, _ -> 0.0 }): NDElement<Double, DoubleField> =
                real(intArrayOf(dim1, dim2)) { initializer(it[0], it[1]) }

        fun real3D(dim1: Int, dim2: Int, dim3: Int, initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }): NDElement<Double, DoubleField> =
                real(intArrayOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }

//        inline fun real(shape: IntArray, block: ExtendedNDField<Double, DoubleField>.() -> NDStructure<Double>): NDElement<Double, DoubleField> {
//            val field = NDField.real(shape)
//            return GenericNDElement(field, field.run(block))
//        }

        /**
         * Simple boxing NDArray
         */
        fun <T : Any, F : Field<T>> generic(shape: IntArray, field: F, initializer: F.(IntArray) -> T): NDElement<T, F> =
                NDField.generic(shape, field).produce(initializer)

        inline fun <reified T : Any, F : Field<T>> inline(shape: IntArray, field: F, noinline initializer: F.(IntArray) -> T): NDElement<T, F> =
                NDField.inline(shape, field).produce(initializer)
    }
}

inline fun <T, F : Field<T>> NDElement<T, F>.transformIndexed(crossinline action: F.(IntArray, T) -> T): NDElement<T, F> = context.produce { action(it, get(*it)) }
inline fun <T, F : Field<T>> NDElement<T, F>.transform(crossinline action: F.(T) -> T): NDElement<T, F> = context.produce { action(get(*it)) }


/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T, F : Field<T>> Function1<T, T>.invoke(ndElement: NDElement<T, F>): NDElement<T, F> = ndElement.transform { value -> this@invoke(value) }

/* plus and minus */

/**
 * Summation operation for [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.plus(arg: T): NDElement<T, F> = transform { value ->
    context.field.run { arg + value }
}

/**
 * Subtraction operation between [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.minus(arg: T): NDElement<T, F> = transform { value ->
    context.field.run { arg - value }
}

/* prod and div */

/**
 * Product operation for [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.times(arg: T): NDElement<T, F> = transform { value ->
    context.field.run { arg * value }
}

/**
 * Division operation between [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.div(arg: T): NDElement<T, F> = transform { value ->
    context.field.run { arg / value }
}


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

class GenericNDField<T : Any, F : Field<T>>(override val shape: IntArray, override val field: F) : NDField<T, F> {
    override fun produce(initializer: F.(IntArray) -> T): NDElement<T, F> = GenericNDElement(this, produceStructure(initializer))
    private inline fun produceStructure(crossinline initializer: F.(IntArray) -> T): NDStructure<T> = NdStructure(shape, ::boxingBuffer) { field.initializer(it) }
}

/**
 *  Read-only [NDStructure] coupled to the context.
 */
class GenericNDElement<T, F : Field<T>>(override val context: NDField<T, F>, private val structure: NDStructure<T>) : NDElement<T, F>, NDStructure<T> by structure {
    override val self: NDElement<T, F> get() = this
}
