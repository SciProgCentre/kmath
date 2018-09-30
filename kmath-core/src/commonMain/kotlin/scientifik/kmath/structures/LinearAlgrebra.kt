package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.NDArrays.createSimpleNDFieldFactory

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
     * Produce new linear space with given dimensions. The space produced could be raised from cache since [LinearSpace] does not have mutable elements
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
     * Dot product. Throws exception on dimension mismatch
     */
    fun multiply(a: V, b: V): V {
        if (a.rows != b.columns) {
            //TODO replace by specific exception
            error("Dimension mismatch in linear structure dot product: [${a.rows},${a.columns}]*[${b.rows},${b.columns}]")
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
    /**
     * Number of rows
     */
    val rows: Int
    /**
     * Number of columns
     */
    val columns: Int

    /**
     * Get element in row [i] and column [j]. Throws error in case of call ounside structure dimensions
     */
    operator fun get(i: Int, j: Int): T

    fun transpose(): LinearStructure<T> {
        return object : LinearStructure<T> {
            override val rows: Int = this@LinearStructure.columns
            override val columns: Int = this@LinearStructure.rows
            override fun get(i: Int, j: Int): T = this@LinearStructure[j, i]
        }
    }
}

interface Vector<T : Any> : LinearStructure<T> {
    override val columns: Int
        get() = 1

    operator fun get(i: Int) = get(i, 0)
}


/**
 * NDArray-based implementation of vector space. By default uses slow [SimpleNDField], but could be overridden with custom [NDField] factory.
 */
class ArraySpace<T : Any>(
        rows: Int,
        columns: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = createSimpleNDFieldFactory(field)
) : LinearSpace<T, LinearStructure<out T>>(rows, columns, field) {

    val ndField by lazy {
        ndFactory(listOf(rows, columns))
    }

    override fun produce(initializer: (Int, Int) -> T): LinearStructure<T> = ArrayMatrix<T>(this, initializer)

    override fun produceSpace(rows: Int, columns: Int): LinearSpace<T, LinearStructure<out T>> {
        return ArraySpace(rows, columns, field, ndFactory)
    }
}

/**
 * Member of [ArraySpace] which wraps 2-D array
 */
class ArrayMatrix<T : Any>(override val context: ArraySpace<T>, initializer: (Int, Int) -> T) : LinearStructure<T>, SpaceElement<LinearStructure<out T>, ArraySpace<T>> {

    private val array = context.ndField.produce { list -> initializer(list[0], list[1]) }

    //val list: List<List<T>> = (0 until rows).map { i -> (0 until columns).map { j -> initializer(i, j) } }

    override val rows: Int get() = context.rows

    override val columns: Int get() = context.columns

    override fun get(i: Int, j: Int): T {
        return array[i, j]
    }

    override val self: ArrayMatrix<T> get() = this
}


class ArrayVector<T : Any>(override val context: ArraySpace<T>, initializer: (Int) -> T) : Vector<T>, SpaceElement<LinearStructure<out T>, ArraySpace<T>> {

    init {
        if (context.columns != 1) {
            error("Vector must have single column")
        }
    }

    private val array = context.ndField.produce { list -> initializer(list[0]) }


    override val rows: Int get() = context.rows

    override val columns: Int = 1

    override fun get(i: Int, j: Int): T {
        return array[i]
    }

    override val self: ArrayVector<T> get() = this

}

fun <T : Any> vector(size: Int, field: Field<T>, initializer: (Int) -> T) =
        ArrayVector(ArraySpace(size, 1, field), initializer)

fun realVector(size: Int, initializer: (Int) -> Double) =
        ArrayVector(ArraySpace(size, 1, DoubleField, realNDFieldFactory), initializer)

fun <T : Any> Array<T>.asVector(field: Field<T>) = vector(size, field) { this[it] }

fun DoubleArray.asVector() = realVector(this.size) { this[it] }

fun <T : Any> matrix(rows: Int, columns: Int, field: Field<T>, initializer: (Int, Int) -> T) =
        ArrayMatrix(ArraySpace(rows, columns, field), initializer)

fun realMatrix(rows: Int, columns: Int, initializer: (Int, Int) -> Double) =
        ArrayMatrix(ArraySpace(rows, columns, DoubleField, realNDFieldFactory), initializer)
