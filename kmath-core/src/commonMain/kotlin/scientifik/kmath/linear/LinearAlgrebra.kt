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
abstract class MatrixSpace<T : Any>(val rows: Int, val columns: Int, val field: Field<T>) : Space<Matrix<T>> {

    /**
     * Produce the element of this space
     */
    abstract fun produce(initializer: (Int, Int) -> T): Matrix<T>

    /**
     * Produce new matrix space with given dimensions. The space produced could be raised from cache since [MatrixSpace] does not have mutable elements
     */
    abstract fun produceSpace(rows: Int, columns: Int): MatrixSpace<T>

    override val zero: Matrix<T> by lazy {
        produce { _, _ -> field.zero }
    }

//    val one: Matrix<T> by lazy {
//        produce { i, j -> if (i == j) field.one else field.zero }
//    }

    override fun add(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        return produce { i, j -> with(field) { a[i, j] + b[i, j] } }
    }

    override fun multiply(a: Matrix<T>, k: Double): Matrix<T> {
        //TODO it is possible to implement scalable linear elements which normed values and adjustable scale to save memory and processing poser
        return produce { i, j -> with(field) { a[i, j] * k } }
    }

    /**
     * Dot product. Throws exception on dimension mismatch
     */
    fun multiply(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        if (a.rows != b.columns) {
            //TODO replace by specific exception
            error("Dimension mismatch in linear structure dot product: [${a.rows},${a.columns}]*[${b.rows},${b.columns}]")
        }
        return produceSpace(a.rows, b.columns).produce { i, j ->
            (0 until a.columns).asSequence().map { k -> field.multiply(a[i, k], b[k, j]) }.reduce { first, second -> field.add(first, second) }
        }
    }
}

infix fun <T : Any> Matrix<T>.dot(b: Matrix<T>): Matrix<T> = this.context.multiply(this, b)

/**
 * A matrix-like structure
 */
interface Matrix<T : Any> : SpaceElement<Matrix<T>, MatrixSpace<T>> {
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

    override val self: Matrix<T>
        get() = this

    fun transpose(): Matrix<T> {
        return object : Matrix<T> {
            override val context: MatrixSpace<T> = this@Matrix.context
            override val rows: Int = this@Matrix.columns
            override val columns: Int = this@Matrix.rows
            override fun get(i: Int, j: Int): T = this@Matrix[j, i]
        }
    }

    companion object {
        fun <T : Any> one(rows: Int, columns: Int, field: Field<T>): Matrix<T> {
            return matrix(rows, columns, field) { i, j -> if (i == j) field.one else field.zero }
        }
    }
}


/**
 * A linear space for vectors
 */
abstract class VectorSpace<T : Any>(val size: Int, val field: Field<T>) : Space<Vector<T>> {

    abstract fun produce(initializer: (Int) -> T): Vector<T>

    override val zero: Vector<T> by lazy { produce { field.zero } }

    override fun add(a: Vector<T>, b: Vector<T>): Vector<T> = produce { with(field) { a[it] + b[it] } }

    override fun multiply(a: Vector<T>, k: Double): Vector<T> = produce { with(field) { a[it] * k } }
}


interface Vector<T : Any> : SpaceElement<Vector<T>, VectorSpace<T>> {
    val size: Int
        get() = context.size

    operator fun get(i: Int): T
}


/**
 * NDArray-based implementation of vector space. By default uses slow [SimpleNDField], but could be overridden with custom [NDField] factory.
 */
class ArrayMatrixSpace<T : Any>(
        rows: Int,
        columns: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = createFactory(field)
) : MatrixSpace<T>(rows, columns, field) {

    val ndField by lazy {
        ndFactory(listOf(rows, columns))
    }

    override fun produce(initializer: (Int, Int) -> T): Matrix<T> = ArrayMatrix(this, initializer)

    override fun produceSpace(rows: Int, columns: Int): ArrayMatrixSpace<T> {
        return ArrayMatrixSpace(rows, columns, field, ndFactory)
    }
}

class ArrayVectorSpace<T : Any>(
        size: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = createFactory(field)
) : VectorSpace<T>(size, field) {
    val ndField by lazy {
        ndFactory(listOf(size))
    }

    override fun produce(initializer: (Int) -> T): Vector<T> = ArrayVector(this, initializer)
}

/**
 * Member of [ArrayMatrixSpace] which wraps 2-D array
 */
class ArrayMatrix<T : Any> internal constructor(override val context: ArrayMatrixSpace<T>, val array: NDArray<T>) : Matrix<T> {

    constructor(context: ArrayMatrixSpace<T>, initializer: (Int, Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0], list[1]) })

    override val rows: Int get() = context.rows

    override val columns: Int get() = context.columns

    override fun get(i: Int, j: Int): T {
        return array[i, j]
    }

    override val self: ArrayMatrix<T> get() = this
}


class ArrayVector<T : Any> internal constructor(override val context: ArrayVectorSpace<T>, val array: NDArray<T>) : Vector<T> {

    constructor(context: ArrayVectorSpace<T>, initializer: (Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0]) })

    init {
        if (context.size != array.shape[0]) {
            error("Array dimension mismatch")
        }
    }

    override fun get(i: Int): T {
        return array[i]
    }

    override val self: ArrayVector<T> get() = this
}

/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any> {
    fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>
    fun solve(a: Matrix<T>, b: Vector<T>): Vector<T> = solve(a, b.toMatrix()).toVector()
    fun inverse(a: Matrix<T>): Matrix<T> = solve(a, Matrix.one(a.rows, a.columns, a.context.field))
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
        ArrayMatrix(ArrayMatrixSpace(rows, columns, field), initializer)

/**
 * Create [ArrayMatrix] of doubles.
 */
fun realMatrix(rows: Int, columns: Int, initializer: (Int, Int) -> Double) =
        ArrayMatrix(ArrayMatrixSpace(rows, columns, DoubleField, realNDFieldFactory), initializer)


/**
 * Convert matrix to vector if it is possible
 */
fun <T : Any> Matrix<T>.toVector(): Vector<T> {
    return when {
        this.columns == 1 -> {
//            if (this is ArrayMatrix) {
//                //Reuse existing underlying array
//                ArrayVector(ArrayVectorSpace(rows, context.field, context.ndFactory), array)
//            } else {
//                //Generic vector
//                vector(rows, context.field) { get(it, 0) }
//            }
            vector(rows, context.field) { get(it, 0) }
        }
        else -> error("Can't convert matrix with more than one column to vector")
    }
}

fun <T : Any> Vector<T>.toMatrix(): Matrix<T> {
//    return if (this is ArrayVector) {
//        //Reuse existing underlying array
//        ArrayMatrix(ArrayMatrixSpace(size, 1, context.field, context.ndFactory), array)
//    } else {
//        //Generic vector
//        matrix(size, 1, context.field) { i, j -> get(i) }
//    }
    return matrix(size, 1, context.field) { i, j -> get(i) }
}

