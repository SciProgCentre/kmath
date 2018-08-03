package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement

/**
 * The space for linear elements. Supports scalar product alongside with standard linear operations.
 * @param T type of individual element of the vector or matrix
 * @param V the type of vector space element
 */
abstract class LinearSpace<T : Any, V : LinearStructure<out T>>(val rows: Int, val columns: Int, val field: Field<T>) : Space<V> {

    /**
     * Produce the element of this space
     */
    abstract fun produce(initializer: (Int, Int) -> T): V

    /**
     * Produce new linear space with given dimensions
     */
    abstract fun produceSpace(rows: Int, columns: Int): LinearSpace<T, V>

    override val zero: V by lazy {
        produce { _, _ -> field.zero }
    }

    override fun add(a: V, b: V): V {
        return produce { i, j -> with(field) { a[i, j] + b[i, j] } }
    }

    override fun multiply(a: V, k: Double): V {
        //TODO it is possible to implement scalable linear elements which normed values and adjustable scale to save memory and processing poser
        return produce { i, j -> with(field) { a[i, j] * k } }
    }

    /**
     * Dot product
     */
    fun multiply(a: V, b: V): V {
        if (a.rows != b.columns) {
            //TODO replace by specific exception
            error("Dimension mismatch in vector dot product")
        }
        return produceSpace(a.rows, b.columns).produce { i, j ->
            (0..a.columns).asSequence().map { k -> field.multiply(a[i, k], b[k, j]) }.reduce { first, second -> field.add(first, second) }
        }
    }

    infix fun V.dot(b: V): V = multiply(this, b)
}

/**
 * A matrix-like structure that is not dependent on specific space implementation
 */
interface LinearStructure<T : Any> {
    val rows: Int
    val columns: Int

    operator fun get(i: Int, j: Int): T

    fun transpose(): LinearStructure<T> {
        return object : LinearStructure<T> {
            override val rows: Int = this@LinearStructure.columns
            override val columns: Int = this@LinearStructure.rows
            override fun get(i: Int, j: Int): T = this@LinearStructure.get(j, i)
        }
    }
}

interface Vector<T : Any> : LinearStructure<T> {
    override val columns: Int
        get() = 1

    operator fun get(i: Int) = get(i, 0)
}


/**
 * DoubleArray-based implementation of vector space
 */
class ArraySpace<T : Any>(rows: Int, columns: Int, field: Field<T>) : LinearSpace<T, LinearStructure<out T>>(rows, columns, field) {

    override fun produce(initializer: (Int, Int) -> T): LinearStructure<T> = ArrayMatrix<T>(this, initializer)


    override fun produceSpace(rows: Int, columns: Int): LinearSpace<T, LinearStructure<out T>> {
        return ArraySpace(rows, columns, field)
    }
}

/**
 * Member of [ArraySpace] which wraps 2-D array
 */
class ArrayMatrix<T : Any>(override val context: ArraySpace<T>, initializer: (Int, Int) -> T) : LinearStructure<T>, SpaceElement<LinearStructure<out T>, ArraySpace<T>> {

    val list: List<List<T>> = (0 until rows).map { i -> (0 until columns).map { j -> initializer(i, j) } }

    override val rows: Int get() = context.rows

    override val columns: Int get() = context.columns

    override fun get(i: Int, j: Int): T {
        return list[i][j]
    }

    override val self: ArrayMatrix<T> get() = this
}


class ArrayVector<T : Any>(override val context: ArraySpace<T>, initializer: (Int) -> T) : Vector<T>, SpaceElement<LinearStructure<out T>, ArraySpace<T>> {

    init {
        if (context.columns != 1) {
            error("Vector must have single column")
        }
    }

    val list: List<T> = (0 until context.rows).map(initializer)


    override val rows: Int get() = context.rows

    override val columns: Int = 1

    override fun get(i: Int, j: Int): T {
        return list[i]
    }

    override val self: ArrayVector<T> get() = this

}

fun <T : Any> vector(size: Int, field: Field<T>, initializer: (Int) -> T) = ArrayVector(ArraySpace(size, 1, field), initializer)
//TODO replace by primitive array version
fun realVector(size: Int, initializer: (Int) -> Double) = vector(size, DoubleField, initializer)

fun <T : Any> Array<T>.asVector(field: Field<T>) = vector(size, field) { this[it] }
//TODO add inferred field from field element
fun DoubleArray.asVector() = realVector(this.size) { this[it] }

fun <T : Any> matrix(rows: Int, columns: Int, field: Field<T>, initializer: (Int, Int) -> T) = ArrayMatrix<T>(ArraySpace(rows, columns, field), initializer)
fun realMatrix(rows: Int, columns: Int, initializer: (Int, Int) -> Double) = matrix(rows, columns, DoubleField, initializer)