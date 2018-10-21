package scientifik.kmath.linear

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.GenericNDField
import scientifik.kmath.structures.NDArray
import scientifik.kmath.structures.NDField
import scientifik.kmath.structures.get

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MatrixSpace<*>) return false

        if (rows != other.rows) return false
        if (columns != other.columns) return false
        if (field != other.field) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        result = 31 * result + field.hashCode()
        return result
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

        /**
         * Create [ArrayMatrix] with custom field
         */
        fun <T : Any> of(rows: Int, columns: Int, field: Field<T>, initializer: (Int, Int) -> T) =
                ArrayMatrix(ArrayMatrixSpace(rows, columns, field), initializer)

        /**
         * Create [ArrayMatrix] of doubles. The implementation in general should be faster than generic one due to boxing.
         */
        fun ofReal(rows: Int, columns: Int, initializer: (Int, Int) -> Double) =
                ArrayMatrix(ArrayMatrixSpace(rows, columns, DoubleField, realNDFieldFactory), initializer)

        /**
         * Create a diagonal value matrix. By default value equals [Field.one].
         */
        fun <T : Any> diagonal(rows: Int, columns: Int, field: Field<T>, values: (Int) -> T = { field.one }): Matrix<T> {
            return of(rows, columns, field) { i, j -> if (i == j) values(i) else field.zero }
        }

        /**
         * Equality check on two generic matrices
         */
        fun equals(mat1: Matrix<*>, mat2: Matrix<*>): Boolean {
            if (mat1 === mat2) return true
            if (mat1.context != mat2.context) return false
            for (i in 0 until mat1.rows) {
                for (j in 0 until mat2.columns) {
                    if (mat1[i, j] != mat2[i, j]) return false
                }
            }
            return true
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

    companion object {
        /**
         * Create vector with custom field
         */
        fun <T : Any> of(size: Int, field: Field<T>, initializer: (Int) -> T) =
                ArrayVector(ArrayVectorSpace(size, field), initializer)

        /**
         * Create vector of [Double]
         */
        fun ofReal(size: Int, initializer: (Int) -> Double) =
                ArrayVector(ArrayVectorSpace(size, DoubleField, realNDFieldFactory), initializer)


        fun equals(v1: Vector<*>, v2: Vector<*>): Boolean {
            if (v1 === v2) return true
            if (v1.context != v2.context) return false
            for (i in 0 until v2.size) {
                if (v1[i] != v2[i]) return false
            }
            return true
        }
    }
}

typealias NDFieldFactory<T> = (IntArray) -> NDField<T>

internal fun <T : Any> genericNDFieldFactory(field: Field<T>): NDFieldFactory<T> = { index -> GenericNDField(index, field) }
internal val realNDFieldFactory: NDFieldFactory<Double> = { index -> GenericNDField(index, DoubleField) }


/**
 * NDArray-based implementation of vector space. By default uses slow [SimpleNDField], but could be overridden with custom [NDField] factory.
 */
class ArrayMatrixSpace<T : Any>(
        rows: Int,
        columns: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = genericNDFieldFactory(field)
) : MatrixSpace<T>(rows, columns, field) {

    val ndField by lazy {
        ndFactory(intArrayOf(rows, columns))
    }

    override fun produce(initializer: (Int, Int) -> T): Matrix<T> = ArrayMatrix(this, initializer)

    override fun produceSpace(rows: Int, columns: Int): ArrayMatrixSpace<T> {
        return ArrayMatrixSpace(rows, columns, field, ndFactory)
    }
}

class ArrayVectorSpace<T : Any>(
        size: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = genericNDFieldFactory(field)
) : VectorSpace<T>(size, field) {
    val ndField by lazy {
        ndFactory(intArrayOf(size))
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
    fun inverse(a: Matrix<T>): Matrix<T> = solve(a, Matrix.diagonal(a.rows, a.columns, a.context.field))
}

/**
 * Convert vector to array (copying content of array)
 */
fun <T : Any> Array<T>.toVector(field: Field<T>) = Vector.of(size, field) { this[it] }

fun DoubleArray.toVector() = Vector.ofReal(this.size) { this[it] }

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
            Vector.of(rows, context.field) { get(it, 0) }
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
    return Matrix.of(size, 1, context.field) { i, _ -> get(i) }
}

