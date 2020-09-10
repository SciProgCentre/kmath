package scientifik.kmath.linear

import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.SpaceOperations
import scientifik.kmath.operations.invoke
import scientifik.kmath.operations.sum
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.asSequence

/**
 * Basic operations on matrices. Operates on [Matrix]
 */
interface MatrixContext<T : Any> : SpaceOperations<Matrix<T>> {
    /**
     * Produce a matrix with this context and given dimensions
     */
    fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): Matrix<T>

    infix fun Matrix<T>.dot(other: Matrix<T>): Matrix<T>

    infix fun Matrix<T>.dot(vector: Point<T>): Point<T>

    operator fun Matrix<T>.times(value: T): Matrix<T>

    operator fun T.times(m: Matrix<T>): Matrix<T> = m * this

    companion object {
        /**
         * Non-boxing double matrix
         */
        val real: RealMatrixContext = RealMatrixContext

        /**
         * A structured matrix with custom buffer
         */
        fun <T : Any, R : Ring<T>> buffered(
            ring: R,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ): GenericMatrixContext<T, R> = BufferMatrixContext(ring, bufferFactory)

        /**
         * Automatic buffered matrix, unboxed if it is possible
         */
        inline fun <reified T : Any, R : Ring<T>> auto(ring: R): GenericMatrixContext<T, R> =
            buffered(ring, Buffer.Companion::auto)
    }
}

interface GenericMatrixContext<T : Any, R : Ring<T>> : MatrixContext<T> {
    /**
     * The ring context for matrix elements
     */
    val elementContext: R

    /**
     * Produce a point compatible with matrix space
     */
    fun point(size: Int, initializer: (Int) -> T): Point<T>

    override infix fun Matrix<T>.dot(other: Matrix<T>): Matrix<T> {
        //TODO add typed error
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }

        return produce(rowNum, other.colNum) { i, j ->
            val row = rows[i]
            val column = other.columns[j]
            elementContext { sum(row.asSequence().zip(column.asSequence(), ::multiply)) }
        }
    }

    override infix fun Matrix<T>.dot(vector: Point<T>): Point<T> {
        //TODO add typed error
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }

        return point(rowNum) { i ->
            val row = rows[i]
            elementContext { sum(row.asSequence().zip(vector.asSequence(), ::multiply)) }
        }
    }

    override operator fun Matrix<T>.unaryMinus(): Matrix<T> =
        produce(rowNum, colNum) { i, j -> elementContext { -get(i, j) } }

    override fun add(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        require(a.rowNum == b.rowNum && a.colNum == b.colNum) {
            "Matrix operation dimension mismatch. [${a.rowNum},${a.colNum}] + [${b.rowNum},${b.colNum}]"
        }

        return produce(a.rowNum, a.colNum) { i, j -> elementContext { a[i, j] + b[i, j] } }
    }

    override operator fun Matrix<T>.minus(b: Matrix<T>): Matrix<T> {
        require(rowNum == b.rowNum && colNum == b.colNum) {
            "Matrix operation dimension mismatch. [$rowNum,$colNum] - [${b.rowNum},${b.colNum}]"
        }

        return produce(rowNum, colNum) { i, j -> elementContext { get(i, j) + b[i, j] } }
    }

    override fun multiply(a: Matrix<T>, k: Number): Matrix<T> =
        produce(a.rowNum, a.colNum) { i, j -> elementContext { a[i, j] * k } }

    operator fun Number.times(matrix: FeaturedMatrix<T>): Matrix<T> = matrix * this

    override operator fun Matrix<T>.times(value: T): Matrix<T> =
        produce(rowNum, colNum) { i, j -> elementContext { get(i, j) * value } }
}
