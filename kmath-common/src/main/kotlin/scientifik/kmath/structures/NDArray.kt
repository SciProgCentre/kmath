package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement

/**
 * An exception is thrown when the expected ans actual shape of NDArray differs
 */
class ShapeMismatchException(val expected: List<Int>, val actual: List<Int>) : RuntimeException()

/**
 * Field for n-dimensional arrays.
 * @param shape - the list of dimensions of the array
 * @param field - operations field defined on individual array element
 * @param T the type of the element contained in NDArray
 */
abstract class NDField<T>(val shape: List<Int>, val field: Field<T>) : Field<NDArray<T>> {

    /**
     * Create new instance of NDArray using field shape and given initializer
     * The producer takes list of indices as argument and returns contained value
     */
    abstract fun produce(initializer: (List<Int>) -> T): NDArray<T>

    override val zero: NDArray<T> by lazy {
        produce { this.field.zero }
    }

    /**
     * Check the shape of given NDArray and throw exception if it does not coincide with shape of the field
     */
    private fun checkShape(vararg arrays: NDArray<T>) {
        arrays.forEach {
            if (shape != it.shape) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }

    /**
     * Element-by-element addition
     */
    override fun add(a: NDArray<T>, b: NDArray<T>): NDArray<T> {
        checkShape(a, b)
        return produce { with(field) { a[it] + b[it] } }
    }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: NDArray<T>, k: Double): NDArray<T> {
        checkShape(a)
        return produce { with(field) { a[it] * k } }
    }

    override val one: NDArray<T>
        get() = produce { this.field.one }

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: NDArray<T>, b: NDArray<T>): NDArray<T> {
        checkShape(a)
        return produce { with(field) { a[it] * b[it] } }
    }

    /**
     * Element-by-element division
     */
    override fun divide(a: NDArray<T>, b: NDArray<T>): NDArray<T> {
        checkShape(a)
        return produce { with(field) { a[it] / b[it] } }
    }
}


interface NDArray<T> : FieldElement<NDArray<T>, NDField<T>> {

    /**
     * The list of dimensions of this NDArray
     */
    val shape: List<Int>
        get() = context.shape

    /**
     * The number of dimentsions for this array
     */
    val dimension: Int
        get() = shape.size

    /**
     * Get the element with given indexes. If number of indexes is different from {@link dimension}, throws exception.
     */
    operator fun get(vararg index: Int): T

    operator fun get(index: List<Int>): T {
        return get(*index.toIntArray())
    }

    operator fun iterator(): Iterator<Pair<List<Int>, T>> {
        return iterateIndexes(shape).map { Pair(it, this[it]) }.iterator()
    }

    /**
     * Generate new NDArray, using given transformation for each element
     */
    fun transform(action: (List<Int>, T) -> T): NDArray<T> = context.produce { action(it, this[it]) }

    companion object {
        /**
         * Iterate over all indexes in the nd-shape
         */
        fun iterateIndexes(shape: List<Int>): Sequence<List<Int>> {
            return if (shape.size == 1) {
                (0 until shape[0]).asSequence().map { listOf(it) }
            } else {
                val tailShape = ArrayList(shape).apply { removeAt(0) }
                val tailSequence: List<List<Int>> = iterateIndexes(tailShape).toList()
                (0 until shape[0]).asSequence().map { firstIndex ->
                    //adding first element to each of provided index lists
                    tailSequence.map { listOf(firstIndex) + it }.asSequence()
                }.flatten()
            }
        }
    }
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T> Function1<T, T>.invoke(ndArray: NDArray<T>): NDArray<T> = ndArray.transform { _, value -> this(value) }

/* plus and minus */

/**
 * Summation operation for [NDArray] and single element
 */
operator fun <T> NDArray<T>.plus(arg: T): NDArray<T> = transform { _, value ->
    with(context.field){
        arg + value
    }
}

/**
 * Reverse sum operation
 */
operator fun <T> T.plus(arg: NDArray<T>): NDArray<T> = arg + this

/**
 * Subtraction operation between [NDArray] and single element
 */
operator fun <T> NDArray<T>.minus(arg: T): NDArray<T> = transform  { _, value ->
    with(context.field){
        arg - value
    }
}

/**
 * Reverse minus operation
 */
operator fun <T> T.minus(arg: NDArray<T>): NDArray<T> = arg.transform { _, value ->
    with(arg.context.field){
        this@minus - value
    }
}

/* prod and div */

/**
 * Product operation for [NDArray] and single element
 */
operator fun <T> NDArray<T>.times(arg: T): NDArray<T> = transform { _, value ->
    with(context.field){
        arg * value
    }
}

/**
 * Reverse product operation
 */
operator fun <T> T.times(arg: NDArray<T>): NDArray<T> = arg * this

/**
 * Division operation between [NDArray] and single element
 */
operator fun <T> NDArray<T>.div(arg: T): NDArray<T> = transform  { _, value ->
    with(context.field){
        arg / value
    }
}

/**
 * Reverse division operation
 */
operator fun <T> T.div(arg: NDArray<T>): NDArray<T> = arg.transform { _, value ->
    with(arg.context.field){
        this@div/ value
    }
}

