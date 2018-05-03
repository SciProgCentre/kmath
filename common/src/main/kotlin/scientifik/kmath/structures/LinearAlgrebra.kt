package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement

abstract class LinearSpace<T, E : LinearObject<T>>(val rows: Int, val columns: Int, val field: Field<T>) : Space<E> {

    abstract fun produce(initializer: (Int, Int) -> T): E

    override val zero: E by lazy {
        produce { _, _ -> field.zero }
    }

    override fun add(a: E, b: E): E {
        return produce { i, j -> with(field) { a[i, j] + b[i, j] } }
    }

    override fun multiply(a: E, k: Double): E {
        //TODO it is possible to implement scalable linear elements which normed values and adjustable scale to save memory and processing poser
        return produce { i, j -> with(field) { a[i, j] * k } }
    }
}

/**
 * An element of linear algebra with fixed dimension. The linear space allows linear operations on objects of the same dimensions.
 * Scalar product operations are performed outside space.
 *
 * @param T the type of linear object element type.
 */
interface LinearObject<T> : SpaceElement<LinearObject<T>> {
    val rows: Int
    val columns: Int
    operator fun get(i: Int, j: Int): T

    /**
     * Get a transposed object with switched dimensions
     */
    fun transpose(): LinearObject<T>

    /**
     * Perform scalar multiplication (dot) operation, checking dimensions. The argument object and result both could be outside initial space.
     */
    operator fun times(other: LinearObject<T>): LinearObject<T>
}
