package scientifik.kmath.linear

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.NDArray
import scientifik.kmath.structures.NDArrays.createFactory
import scientifik.kmath.structures.NDFieldFactory
import scientifik.kmath.structures.realNDFieldFactory

/**
 * The space for linear elements. Supports scalar product alongside with standard linear operations.
 * @param T type of individual element of the vector or matrix
 * @param V the type of vector space element
 */
abstract class LinearSpace<T : Any, V : Matrix<T>>(val rows: Int, val columns: Int, val field: Field<T>) : Space<V> {

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

    val one: V by lazy {
        produce { i, j -> if (i == j) field.one else field.zero }
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
 * A specialized [LinearSpace] which works with vectors
 */
abstract class VectorSpace<T : Any, V : Vector<T>>(size: Int, field: Field<T>) : LinearSpace<T, V>(size, 1, field)

/**
 * A matrix-like structure
 */
interface Matrix<T : Any> {
    val context: LinearSpace<T, out Matrix<T>>
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

    fun transpose(): Matrix<T> {
        return object : Matrix<T> {
            override val context: LinearSpace<T, out Matrix<T>> = this@Matrix.context
            override val rows: Int = this@Matrix.columns
            override val columns: Int = this@Matrix.rows
            override fun get(i: Int, j: Int): T = this@Matrix[j, i]
        }
    }
}

interface Vector<T : Any> : Matrix<T> {
    override val context: VectorSpace<T, Vector<T>>
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
        val ndFactory: NDFieldFactory<T> = createFactory(field)
) : LinearSpace<T, Matrix<T>>(rows, columns, field) {

    val ndField by lazy {
        ndFactory(listOf(rows, columns))
    }

    override fun produce(initializer: (Int, Int) -> T): Matrix<T> = ArrayMatrix(this, initializer)

    override fun produceSpace(rows: Int, columns: Int): ArraySpace<T> {
        return ArraySpace(rows, columns, field, ndFactory)
    }
}

class ArrayVectorSpace<T : Any>(
        size: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = createFactory(field)
) : VectorSpace<T, Vector<T>>(size, field) {
    val ndField by lazy {
        ndFactory(listOf(size))
    }

    override fun produce(initializer: (Int, Int) -> T): Vector<T> = produceVector { i -> initializer(i, 0) }

    fun produceVector(initializer: (Int) -> T): Vector<T> = ArrayVector(this, initializer)

    override fun produceSpace(rows: Int, columns: Int): LinearSpace<T, Vector<T>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * Member of [ArraySpace] which wraps 2-D array
 */
class ArrayMatrix<T : Any> internal constructor(override val context: ArraySpace<T>, val array: NDArray<T>) : Matrix<T>, SpaceElement<Matrix<T>, ArraySpace<T>> {

    constructor(context: ArraySpace<T>, initializer: (Int, Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0], list[1]) })

    override val rows: Int get() = context.rows

    override val columns: Int get() = context.columns

    override fun get(i: Int, j: Int): T {
        return array[i, j]
    }

    override val self: ArrayMatrix<T> get() = this
}


class ArrayVector<T : Any> internal constructor(override val context: ArrayVectorSpace<T>, val array: NDArray<T>) : Vector<T>, SpaceElement<Vector<T>, ArrayVectorSpace<T>> {

    constructor(context: ArrayVectorSpace<T>, initializer: (Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0]) })

    init {
        if (context.columns != 1) {
            error("Vector must have single column")
        }
        if (context.rows != array.shape[0]) {
            error("Array dimension mismatch")
        }
    }

    //private val array = context.ndField.produce { list -> initializer(list[0]) }


    override val rows: Int get() = context.rows

    override val columns: Int = 1

    override fun get(i: Int, j: Int): T {
        return array[i]
    }

    override val self: ArrayVector<T> get() = this

}

/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any> {
    fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>
    fun solve(a: Matrix<T>, b: Vector<T>): Vector<T> = solve(a, b as Matrix<T>).toVector()
    fun inverse(a: Matrix<T>): Matrix<T> = solve(a, a.context.one)
}

/**
 * Create vector with custom field
 */
fun <T : Any> vector(size: Int, field: Field<T>, initializer: (Int) -> T) =
        ArrayVector(ArrayVectorSpace(size, field), initializer)

/**
 * Create vector of [Double]
 */
fun realVector(size: Int, initializer: (Int) -> Double) =
        ArrayVector(ArrayVectorSpace(size, DoubleField, realNDFieldFactory), initializer)

/**
 * Convert vector to array (copying content of array)
 */
fun <T : Any> Array<T>.asVector(field: Field<T>) = vector(size, field) { this[it] }

fun DoubleArray.asVector() = realVector(this.size) { this[it] }

/**
 * Create [ArrayMatrix] with custom field
 */
fun <T : Any> matrix(rows: Int, columns: Int, field: Field<T>, initializer: (Int, Int) -> T) =
        ArrayMatrix(ArraySpace(rows, columns, field), initializer)

/**
 * Create [ArrayMatrix] of doubles.
 */
fun realMatrix(rows: Int, columns: Int, initializer: (Int, Int) -> Double) =
        ArrayMatrix(ArraySpace(rows, columns, DoubleField, realNDFieldFactory), initializer)


/**
 * Convert matrix to vector if it is possible
 */
fun <T : Any> Matrix<T>.toVector(): Vector<T> {
    return when {
        this is Vector -> return this
        this.columns == 1 -> {
            if (this is ArrayMatrix) {
                //Reuse existing underlying array
                ArrayVector(ArrayVectorSpace(rows, context.field, context.ndFactory), array)
            } else {
                //Generic vector
                vector(rows, context.field) { get(it, 0) }
            }
        }
        else -> error("Can't convert matrix with more than one column to vector")
    }
}

