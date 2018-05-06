package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement

/**
 * The space for linear elements. Supports scalar product alongside with standard linear operations.
 */
abstract class LinearSpace<T, V: LinearStructure<out T>>(val rows: Int, val columns: Int, val field: Field<T>) : Space<V> {

    /**
     * Produce the element of this space
     */
    abstract fun produce(initializer: (Int, Int) -> T): V

    /**
     * Produce new linear space with given dimensions
     */
    abstract fun produceSpace(rows: Int, columns: Int): LinearSpace<T,V>

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
        if (a.columns != b.rows) {
            //TODO replace by specific exception
            throw RuntimeException("Dimension mismatch in vector dot product")
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
interface LinearStructure<T> {
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

class RealArraySpace(rows: Int, columns: Int) : LinearSpace<Double, RealArray>(rows, columns, DoubleField) {
    override fun produce(initializer: (Int, Int) -> Double): RealArray {
        return RealArray(this, initializer)
    }

    override fun produceSpace(rows: Int, columns: Int): LinearSpace<Double, RealArray> {
        return RealArraySpace(rows, columns)
    }
}

class RealArray(override val context: RealArraySpace, initializer: (Int, Int) -> Double): LinearStructure<Double>, SpaceElement<RealArray> {

    val array: Array<Array<Double>> = Array(context.rows) { i -> Array(context.columns) { j -> initializer(i, j) } }

    override val rows: Int = context.rows

    override val columns: Int = context.columns

    override fun get(i: Int, j: Int): Double {
        return array[i][j]
    }

    override val self: RealArray = this
}


///**
// * An element of linear algebra with fixed dimension. The linear space allows linear operations on objects of the same dimensions.
// * Scalar product operations are performed outside space.
// *
// * @param E the type of linear object element type.
// */
//interface LinearObject<E> : SpaceElement<LinearObject<E>> {
//    override val context: LinearSpace<>
//
//    val rows: Int
//    val columns: Int
//    operator fun get(i: Int, j: Int): E
//
//    /**
//     * Get a transposed object with switched dimensions
//     */
//    fun transpose(): LinearObject<E>
//
//    /**
//     * Perform scalar multiplication (dot) operation, checking dimensions. The argument object and result both could be outside initial space.
//     */
//    operator fun times(other: LinearObject<E>): LinearObject<E>
//}
