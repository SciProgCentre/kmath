package scientifik.kmath.ejml

import org.ejml.simple.SimpleMatrix
import scientifik.kmath.linear.MatrixContext
import scientifik.kmath.linear.Point
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.Matrix

/**
 * Represents context of basic operations operating with [EjmlMatrix].
 */
class EjmlMatrixContext(private val space: Space<Double>) : MatrixContext<Double> {
    /**
     * Solves for X in the following equation: x = a^-1*b, where 'a' is base matrix and 'b' is an n by p matrix.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for 'x' that is n by p.
     */
    fun solve(a: Matrix<Double>, b: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(a.toEjml().origin.solve(b.toEjml().origin))

    /**
     * Solves for X in the following equation: x = a^(-1)*b, where 'a' is base matrix and 'b' is an n by p matrix.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for 'x' that is n by p.
     */
    fun solve(a: Matrix<Double>, b: Point<Double>): EjmlVector =
        EjmlVector(a.toEjml().origin.solve(b.toEjml().origin))

    /**
     * Returns the inverse of given matrix: b = a^(-1).
     *
     * @param a the matrix.
     * @return the inverse of this matrix.
     */
    fun inverse(a: Matrix<Double>): EjmlMatrix = EjmlMatrix(a.toEjml().origin.invert())

    /**
     * Converts this matrix to EJML one.
     */
    fun Matrix<Double>.toEjml(): EjmlMatrix =
        if (this is EjmlMatrix) this else produce(rowNum, colNum) { i, j -> get(i, j) }

    /**
     * Converts this vector to EJML one.
     */
    fun Point<Double>.toEjml(): EjmlVector =
        if (this is EjmlVector) this else EjmlVector(SimpleMatrix(size, 1).also {
            (0 until it.numRows()).forEach { row -> it[row, 0] = get(row) }
        })

    override fun unaryOperation(operation: String, arg: Matrix<Double>): Matrix<Double> = when (operation) {
        "inverse" -> inverse(arg)
        else -> super.unaryOperation(operation, arg)
    }

    override fun binaryOperation(operation: String, left: Matrix<Double>, right: Matrix<Double>): Matrix<Double> =
        when (operation) {
            "solve" -> solve(left, right)
            else -> super.binaryOperation(operation, left, right)
        }

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> Double): EjmlMatrix =
        EjmlMatrix(SimpleMatrix(rows, columns).also {
            (0 until it.numRows()).forEach { row ->
                (0 until it.numCols()).forEach { col -> it[row, col] = initializer(row, col) }
            }
        })

    override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(toEjml().origin.mult(other.toEjml().origin))

    override fun Matrix<Double>.dot(vector: Point<Double>): EjmlVector =
        EjmlVector(toEjml().origin.mult(vector.toEjml().origin))

    override fun add(a: Matrix<Double>, b: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(a.toEjml().origin + b.toEjml().origin)

    override operator fun Matrix<Double>.minus(b: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(toEjml().origin - b.toEjml().origin)

    override fun multiply(a: Matrix<Double>, k: Number): EjmlMatrix =
        produce(a.rowNum, a.colNum) { i, j -> space { a[i, j] * k } }

    override operator fun Matrix<Double>.times(value: Double): EjmlMatrix = EjmlMatrix(toEjml().origin.scale(value))

    companion object
}
