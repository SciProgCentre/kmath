package kscience.kmath.linear

import kscience.kmath.operations.Ring
import kscience.kmath.operations.SpaceOperations
import kscience.kmath.operations.invoke
import kscience.kmath.operations.sum
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.BufferFactory
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.asSequence

/**
 * Basic operations on matrices. Operates on [Matrix]
 */
public interface MatrixContext<T : Any> : SpaceOperations<Matrix<T>> {
    /**
     * Produce a matrix with this context and given dimensions
     */
    public fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): Matrix<T>

    public override fun binaryOperation(operation: String, left: Matrix<T>, right: Matrix<T>): Matrix<T> = when (operation) {
        "dot" -> left dot right
        else -> super.binaryOperation(operation, left, right)
    }

    /**
     * Computes the dot product of this matrix and another one.
     *
     * @receiver the multiplicand.
     * @param other the multiplier.
     * @return the dot product.
     */
    public infix fun Matrix<T>.dot(other: Matrix<T>): Matrix<T>

    /**
     * Computes the dot product of this matrix and a vector.
     *
     * @receiver the multiplicand.
     * @param vector the multiplier.
     * @return the dot product.
     */
    public infix fun Matrix<T>.dot(vector: Point<T>): Point<T>

    /**
     * Multiplies a matrix by its element.
     *
     * @receiver the multiplicand.
     * @param value the multiplier.
     * @receiver the product.
     */
    public operator fun Matrix<T>.times(value: T): Matrix<T>

    /**
     * Multiplies an element by a matrix of it.
     *
     * @receiver the multiplicand.
     * @param value the multiplier.
     * @receiver the product.
     */
    public operator fun T.times(m: Matrix<T>): Matrix<T> = m * this

    public companion object {
        /**
         * Non-boxing double matrix
         */
        public val real: RealMatrixContext = RealMatrixContext

        /**
         * A structured matrix with custom buffer
         */
        public fun <T : Any, R : Ring<T>> buffered(
            ring: R,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ): GenericMatrixContext<T, R> = BufferMatrixContext(ring, bufferFactory)

        /**
         * Automatic buffered matrix, unboxed if it is possible
         */
        public inline fun <reified T : Any, R : Ring<T>> auto(ring: R): GenericMatrixContext<T, R> =
            buffered(ring, Buffer.Companion::auto)
    }
}

public interface GenericMatrixContext<T : Any, R : Ring<T>> : MatrixContext<T> {
    /**
     * The ring context for matrix elements
     */
    public val elementContext: R

    /**
     * Produce a point compatible with matrix space
     */
    public fun point(size: Int, initializer: (Int) -> T): Point<T>

    public override infix fun Matrix<T>.dot(other: Matrix<T>): Matrix<T> {
        //TODO add typed error
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }

        return produce(rowNum, other.colNum) { i, j ->
            val row = rows[i]
            val column = other.columns[j]
            elementContext { sum(row.asSequence().zip(column.asSequence(), ::multiply)) }
        }
    }

    public override infix fun Matrix<T>.dot(vector: Point<T>): Point<T> {
        //TODO add typed error
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }

        return point(rowNum) { i ->
            val row = rows[i]
            elementContext { sum(row.asSequence().zip(vector.asSequence(), ::multiply)) }
        }
    }

    public override operator fun Matrix<T>.unaryMinus(): Matrix<T> =
        produce(rowNum, colNum) { i, j -> elementContext { -get(i, j) } }

    public override fun add(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        require(a.rowNum == b.rowNum && a.colNum == b.colNum) {
            "Matrix operation dimension mismatch. [${a.rowNum},${a.colNum}] + [${b.rowNum},${b.colNum}]"
        }

        return produce(a.rowNum, a.colNum) { i, j -> elementContext { a[i, j] + b[i, j] } }
    }

    public override operator fun Matrix<T>.minus(b: Matrix<T>): Matrix<T> {
        require(rowNum == b.rowNum && colNum == b.colNum) {
            "Matrix operation dimension mismatch. [$rowNum,$colNum] - [${b.rowNum},${b.colNum}]"
        }

        return produce(rowNum, colNum) { i, j -> elementContext { get(i, j) + b[i, j] } }
    }

    public override fun multiply(a: Matrix<T>, k: Number): Matrix<T> =
        produce(a.rowNum, a.colNum) { i, j -> elementContext { a[i, j] * k } }

    public operator fun Number.times(matrix: FeaturedMatrix<T>): Matrix<T> = matrix * this

    public override operator fun Matrix<T>.times(value: T): Matrix<T> =
        produce(rowNum, colNum) { i, j -> elementContext { get(i, j) * value } }
}
