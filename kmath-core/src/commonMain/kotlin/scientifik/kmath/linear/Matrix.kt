package scientifik.kmath.linear

import scientifik.kmath.operations.*
import scientifik.kmath.structures.*

/**
 * The space for linear elements. Supports scalar product alongside with standard linear operations.
 * @param T type of individual element of the vector or matrix
 * @param V the type of vector space element
 */
abstract class MatrixSpace<T : Any, F : Ring<T>>(val rows: Int, val columns: Int, val field: F) : Space<Matrix<T, F>> {

    /**
     * Produce the element of this space
     */
    abstract fun produce(initializer: (Int, Int) -> T): Matrix<T, F>

    /**
     * Produce new matrix space with given dimensions. The space produced could be raised from cache since [MatrixSpace] does not have mutable elements
     */
    abstract fun produceSpace(rows: Int, columns: Int): MatrixSpace<T, F>

    override val zero: Matrix<T, F> by lazy {
        produce { _, _ -> field.zero }
    }

//    val one: Matrix<T> by lazy {
//        produce { i, j -> if (i == j) field.one else field.zero }
//    }

    override fun add(a: Matrix<T, F>, b: Matrix<T, F>): Matrix<T, F> {
        return produce { i, j -> with(field) { a[i, j] + b[i, j] } }
    }

    override fun multiply(a: Matrix<T, F>, k: Double): Matrix<T, F> {
        //TODO it is possible to implement scalable linear elements which normed values and adjustable scale to save memory and processing poser
        return produce { i, j -> with(field) { a[i, j] * k } }
    }

    /**
     * Dot product. Throws exception on dimension mismatch
     */
    fun multiply(a: Matrix<T, F>, b: Matrix<T, F>): Matrix<T, F> {
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
        if (other !is MatrixSpace<*,*>) return false

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

infix fun <T : Any, F : Field<T>> Matrix<T, F>.dot(b: Matrix<T, F>): Matrix<T, F> = this.context.multiply(this, b)

/**
 * A matrix-like structure
 */
interface Matrix<T : Any, F: Ring<T>> : SpaceElement<Matrix<T, F>, MatrixSpace<T, F>> {
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

    override val self: Matrix<T, F>
        get() = this

    fun transpose(): Matrix<T, F> {
        return object : Matrix<T, F> {
            override val context: MatrixSpace<T, F> = this@Matrix.context
            override val rows: Int = this@Matrix.columns
            override val columns: Int = this@Matrix.rows
            override fun get(i: Int, j: Int): T = this@Matrix[j, i]
        }
    }

    companion object {

        /**
         * Create [ArrayMatrix] with custom field
         */
        fun <T : Any, F: Field<T>> of(rows: Int, columns: Int, field: F, initializer: (Int, Int) -> T) =
                ArrayMatrix(ArrayMatrixSpace(rows, columns, field), initializer)

        /**
         * Create [ArrayMatrix] of doubles. The implementation in general should be faster than generic one due to boxing.
         */
        fun ofReal(rows: Int, columns: Int, initializer: (Int, Int) -> Double) =
                ArrayMatrix(ArrayMatrixSpace(rows, columns, DoubleField, realNDFieldFactory), initializer)

        /**
         * Create a diagonal value matrix. By default value equals [Field.one].
         */
        fun <T : Any, F: Field<T>> diagonal(rows: Int, columns: Int, field: F, values: (Int) -> T = { field.one }): Matrix<T, F> {
            return of(rows, columns, field) { i, j -> if (i == j) values(i) else field.zero }
        }

        /**
         * Equality check on two generic matrices
         */
        fun equals(mat1: Matrix<*, *>, mat2: Matrix<*, *>): Boolean {
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




typealias NDFieldFactory<T, F> = (IntArray) -> NDField<T, F>

internal fun <T : Any, F : Field<T>> genericNDFieldFactory(field: F): NDFieldFactory<T, F> = { index -> GenericNDField(index, field) }
internal val realNDFieldFactory: NDFieldFactory<Double, DoubleField> = { index -> ExtendedNDField(index, DoubleField) }


/**
 * NDArray-based implementation of vector space. By default uses slow [GenericNDField], but could be overridden with custom [NDField] factory.
 */
class ArrayMatrixSpace<T : Any, F : Field<T>>(
        rows: Int,
        columns: Int,
        field: F,
        val ndFactory: NDFieldFactory<T, F> = genericNDFieldFactory(field)
) : MatrixSpace<T, F>(rows, columns, field) {

    val ndField by lazy {
        ndFactory(intArrayOf(rows, columns))
    }

    override fun produce(initializer: (Int, Int) -> T): Matrix<T, F> = ArrayMatrix(this, initializer)

    override fun produceSpace(rows: Int, columns: Int): ArrayMatrixSpace<T, F> {
        return ArrayMatrixSpace(rows, columns, field, ndFactory)
    }
}

/**
 * Member of [ArrayMatrixSpace] which wraps 2-D array
 */
class ArrayMatrix<T : Any, F : Field<T>> internal constructor(override val context: ArrayMatrixSpace<T, F>, val element: NDElement<T, F>) : Matrix<T, F> {

    constructor(context: ArrayMatrixSpace<T, F>, initializer: (Int, Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0], list[1]) })

    override val rows: Int get() = context.rows

    override val columns: Int get() = context.columns

    override fun get(i: Int, j: Int): T {
        return element[i, j]
    }

    override val self: ArrayMatrix<T, F> get() = this
}
