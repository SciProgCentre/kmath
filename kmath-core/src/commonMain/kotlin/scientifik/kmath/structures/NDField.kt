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
 * @param shape - the list of dimensions of the array
 * @param field - operations field defined on individual array element
 * @param T the type of the element contained in NDArray
 */
abstract class NDField<T, F : Field<T>>(val shape: IntArray, val field: F) : Field<NDElement<T, F>> {

    abstract fun produceStructure(initializer: F.(IntArray) -> T): NDStructure<T>

    /**
     * Create new instance of NDArray using field shape and given initializer
     * The producer takes list of indices as argument and returns contained value
     */
    fun produce(initializer: F.(IntArray) -> T): NDElement<T, F> = NDStructureElement(this, produceStructure(initializer))

    override val zero: NDElement<T, F> by lazy {
        produce { zero }
    }

    /**
     * Check the shape of given NDArray and throw exception if it does not coincide with shape of the field
     */
    private fun checkShape(vararg elements: NDElement<T, F>) {
        elements.forEach {
            if (!shape.contentEquals(it.shape)) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }

    /**
     * Element-by-element addition
     */
    override fun add(a: NDElement<T, F>, b: NDElement<T, F>): NDElement<T, F> {
        checkShape(a, b)
        return produce { with(field) { a[it] + b[it] } }
    }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: NDElement<T, F>, k: Double): NDElement<T, F> {
        checkShape(a)
        return produce { with(field) { a[it] * k } }
    }

    override val one: NDElement<T, F>
        get() = produce { one }

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: NDElement<T, F>, b: NDElement<T, F>): NDElement<T, F> {
        checkShape(a)
        return produce { with(field) { a[it] * b[it] } }
    }

    /**
     * Element-by-element division
     */
    override fun divide(a: NDElement<T, F>, b: NDElement<T, F>): NDElement<T, F> {
        checkShape(a)
        return produce { with(field) { a[it] / b[it] } }
    }

//    /**
//     * Reverse sum operation
//     */
//    operator fun T.plus(arg: NDElement<T, F>): NDElement<T, F> = arg + this
//
//    /**
//     * Reverse minus operation
//     */
//    operator fun T.minus(arg: NDElement<T, F>): NDElement<T, F> = arg.transformIndexed { _, value ->
//        with(arg.context.field) {
//            this@minus - value
//        }
//    }
//
//    /**
//     * Reverse product operation
//     */
//    operator fun T.times(arg: NDElement<T, F>): NDElement<T, F> = arg * this
//
//    /**
//     * Reverse division operation
//     */
//    operator fun T.div(arg: NDElement<T, F>): NDElement<T, F> = arg.transformIndexed { _, value ->
//        with(arg.context.field) {
//            this@div / value
//        }
//    }
}


interface NDElement<T, F : Field<T>>: FieldElement<NDElement<T, F>, NDField<T, F>>, NDStructure<T>

inline fun <T, F : Field<T>> NDElement<T, F>.transformIndexed(crossinline action: F.(IntArray, T) -> T): NDElement<T, F> = context.produce { action(it, get(*it)) }
inline fun <T, F : Field<T>> NDElement<T, F>.transform(crossinline action: F.(T) -> T): NDElement<T, F> = context.produce { action(get(*it)) }


/**
 *  Read-only [NDStructure] coupled to the context.
 */
class NDStructureElement<T, F : Field<T>>(override val context: NDField<T, F>, private val structure: NDStructure<T>) : NDElement<T,F>, NDStructure<T> by structure {

    //TODO ensure structure is immutable

    override val self: NDElement<T, F>  get() = this
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T, F : Field<T>> Function1<T, T>.invoke(ndElement: NDElement<T, F>): NDElement<T, F> = ndElement.transform {value -> this@invoke(value) }

/* plus and minus */

/**
 * Summation operation for [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.plus(arg: T): NDElement<T, F> = transform {value ->
    with(context.field) {
        arg + value
    }
}

/**
 * Subtraction operation between [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.minus(arg: T): NDElement<T, F> = transform {value ->
    with(context.field) {
        arg - value
    }
}

/* prod and div */

/**
 * Product operation for [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.times(arg: T): NDElement<T, F> = transform { value ->
    with(context.field) {
        arg * value
    }
}

/**
 * Division operation between [NDElement] and single element
 */
operator fun <T, F : Field<T>> NDElement<T, F>.div(arg: T): NDElement<T, F> = transform { value ->
    with(context.field) {
        arg / value
    }
}

class GenericNDField<T : Any, F : Field<T>>(shape: IntArray, field: F) : NDField<T, F>(shape, field) {
    override fun produceStructure(initializer: F.(IntArray) -> T): NDStructure<T> = ndStructure(shape) { field.initializer(it) }
}

//typealias NDFieldFactory<T> = (IntArray)->NDField<T>

object NDArrays {
    /**
     * Create a platform-optimized NDArray of doubles
     */
    fun realNDArray(shape: IntArray, initializer: DoubleField.(IntArray) -> Double = { 0.0 }): NDElement<Double, DoubleField> {
        return ExtendedNDField(shape, DoubleField).produce(initializer)
    }

    fun real1DArray(dim: Int, initializer: (Int) -> Double = { _ -> 0.0 }): NDElement<Double, DoubleField> {
        return realNDArray(intArrayOf(dim)) { initializer(it[0]) }
    }

    fun real2DArray(dim1: Int, dim2: Int, initializer: (Int, Int) -> Double = { _, _ -> 0.0 }): NDElement<Double, DoubleField> {
        return realNDArray(intArrayOf(dim1, dim2)) { initializer(it[0], it[1]) }
    }

    fun real3DArray(dim1: Int, dim2: Int, dim3: Int, initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }): NDElement<Double, DoubleField> {
        return realNDArray(intArrayOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }
    }

    inline fun produceReal(shape: IntArray, block: ExtendedNDField<Double, DoubleField>.() -> NDElement<Double, DoubleField>) =
            ExtendedNDField(shape, DoubleField).run(block)

    /**
     * Simple boxing NDArray
     */
    fun <T : Any, F : Field<T>> create(field: F, shape: IntArray, initializer: (IntArray) -> T): NDElement<T, F> {
        return GenericNDField(shape, field).produce { initializer(it) }
    }
}
