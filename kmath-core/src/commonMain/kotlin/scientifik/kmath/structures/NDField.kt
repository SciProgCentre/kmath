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
abstract class NDField<T>(val shape: IntArray, open val field: Field<T>) : Field<NDElement<T>> {

    abstract fun produceStructure(initializer: (IntArray) -> T): NDStructure<T>

    /**
     * Create new instance of NDArray using field shape and given initializer
     * The producer takes list of indices as argument and returns contained value
     */
    fun produce(initializer: (IntArray) -> T): NDElement<T> = NDElement(this, produceStructure(initializer))

    override val zero: NDElement<T> by lazy {
        produce { this.field.zero }
    }

    /**
     * Check the shape of given NDArray and throw exception if it does not coincide with shape of the field
     */
    private fun checkShape(vararg elements: NDElement<T>) {
        elements.forEach {
            if (!shape.contentEquals(it.shape)) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }

    /**
     * Element-by-element addition
     */
    override fun add(a: NDElement<T>, b: NDElement<T>): NDElement<T> {
        checkShape(a, b)
        return produce { with(field) { a[it] + b[it] } }
    }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: NDElement<T>, k: Double): NDElement<T> {
        checkShape(a)
        return produce { with(field) { a[it] * k } }
    }

    override val one: NDElement<T>
        get() = produce { this.field.one }

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: NDElement<T>, b: NDElement<T>): NDElement<T> {
        checkShape(a)
        return produce { with(field) { a[it] * b[it] } }
    }

    /**
     * Element-by-element division
     */
    override fun divide(a: NDElement<T>, b: NDElement<T>): NDElement<T> {
        checkShape(a)
        return produce { with(field) { a[it] / b[it] } }
    }

    /**
     * Reverse sum operation
     */
    operator fun <T> T.plus(arg: NDElement<T>): NDElement<T> = arg + this

    /**
     * Reverse minus operation
     */
    operator fun <T> T.minus(arg: NDElement<T>): NDElement<T> = arg.transform { _, value ->
        with(arg.context.field) {
            this@minus - value
        }
    }

    /**
     * Reverse product operation
     */
    operator fun <T> T.times(arg: NDElement<T>): NDElement<T> = arg * this

    /**
     * Reverse division operation
     */
    operator fun <T> T.div(arg: NDElement<T>): NDElement<T> = arg.transform { _, value ->
        with(arg.context.field) {
            this@div / value
        }
    }
}

/**
 *  Immutable [NDStructure] coupled to the context. Emulates Python ndarray
 */
class NDElement<T>(override val context: NDField<T>, private val structure: NDStructure<T>) : FieldElement<NDElement<T>, NDField<T>>, NDStructure<T> by structure {

    //TODO ensure structure is immutable

    override val self: NDElement<T>
        get() = this

    inline fun transform(crossinline action: (IntArray, T) -> T): NDElement<T> = context.produce { action(it, get(*it)) }
    inline fun transform(crossinline action: (T) -> T): NDElement<T> = context.produce { action(get(*it)) }
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T> Function1<T, T>.invoke(ndElement: NDElement<T>): NDElement<T> = ndElement.transform { _, value -> this(value) }

/* plus and minus */

/**
 * Summation operation for [NDElement] and single element
 */
operator fun <T> NDElement<T>.plus(arg: T): NDElement<T> = transform { _, value ->
    with(context.field) {
        arg + value
    }
}

/**
 * Subtraction operation between [NDElement] and single element
 */
operator fun <T> NDElement<T>.minus(arg: T): NDElement<T> = transform { _, value ->
    with(context.field) {
        arg - value
    }
}

/* prod and div */

/**
 * Product operation for [NDElement] and single element
 */
operator fun <T> NDElement<T>.times(arg: T): NDElement<T> = transform { _, value ->
    with(context.field) {
        arg * value
    }
}

/**
 * Division operation between [NDElement] and single element
 */
operator fun <T> NDElement<T>.div(arg: T): NDElement<T> = transform { _, value ->
    with(context.field) {
        arg / value
    }
}

class GenericNDField<T : Any>(shape: IntArray, field: Field<T>) : NDField<T>(shape, field) {
    override fun produceStructure(initializer: (IntArray) -> T): NDStructure<T> = genericNdStructure(shape, initializer)
}

//typealias NDFieldFactory<T> = (IntArray)->NDField<T>

object NDArrays {
    /**
     * Create a platform-optimized NDArray of doubles
     */
    fun realNDArray(shape: IntArray, initializer: (IntArray) -> Double = { 0.0 }): NDElement<Double> {
        return ExtendedNDField(shape, DoubleField).produce(initializer)
    }

    fun real1DArray(dim: Int, initializer: (Int) -> Double = { _ -> 0.0 }): NDElement<Double> {
        return realNDArray(intArrayOf(dim)) { initializer(it[0]) }
    }

    fun real2DArray(dim1: Int, dim2: Int, initializer: (Int, Int) -> Double = { _, _ -> 0.0 }): NDElement<Double> {
        return realNDArray(intArrayOf(dim1, dim2)) { initializer(it[0], it[1]) }
    }

    fun real3DArray(dim1: Int, dim2: Int, dim3: Int, initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }): NDElement<Double> {
        return realNDArray(intArrayOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }
    }

    inline fun produceReal(shape: IntArray, block: ExtendedNDField<Double>.() -> NDElement<Double>) = ExtendedNDField(shape, DoubleField).run(block)

//    /**
//     * Simple boxing NDField
//     */
//    fun <T : Any> fieldFactory(field: Field<T>): NDFieldFactory<T> = { shape -> GenericNDField(shape, field) }

    /**
     * Simple boxing NDArray
     */
    fun <T : Any> create(field: Field<T>, shape: IntArray, initializer: (IntArray) -> T): NDElement<T> {
        return GenericNDField(shape, field).produce { initializer(it) }
    }
}
