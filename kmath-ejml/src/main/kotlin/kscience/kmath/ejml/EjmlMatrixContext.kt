package kscience.kmath.ejml

import kscience.kmath.linear.MatrixContext
import kscience.kmath.linear.Point
import kscience.kmath.structures.Matrix
import org.ejml.simple.SimpleMatrix

/**
 * Converts this matrix to EJML one.
 */
public fun Matrix<Double>.toEjml(): EjmlMatrix =
    if (this is EjmlMatrix) this else EjmlMatrixContext.produce(rowNum, colNum) { i, j -> get(i, j) }

/**
 * Represents context of basic operations operating with [EjmlMatrix].
 *
 * @author Iaroslav Postovalov
 */
public object EjmlMatrixContext : MatrixContext<Double, EjmlMatrix> {

    /**
     * Converts this vector to EJML one.
     */
    public fun Point<Double>.toEjml(): EjmlVector =
        if (this is EjmlVector) this else EjmlVector(SimpleMatrix(size, 1).also {
            (0 until it.numRows()).forEach { row -> it[row, 0] = get(row) }
        })

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> Double): EjmlMatrix =
        EjmlMatrix(SimpleMatrix(rows, columns).also {
            (0 until it.numRows()).forEach { row ->
                (0 until it.numCols()).forEach { col -> it[row, col] = initializer(row, col) }
            }
        })

    public override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(toEjml().origin.mult(other.toEjml().origin))

    public override fun Matrix<Double>.dot(vector: Point<Double>): EjmlVector =
        EjmlVector(toEjml().origin.mult(vector.toEjml().origin))

    public override fun add(a: Matrix<Double>, b: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(a.toEjml().origin + b.toEjml().origin)

    public override operator fun Matrix<Double>.minus(b: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(toEjml().origin - b.toEjml().origin)

    public override fun multiply(a: Matrix<Double>, k: Number): EjmlMatrix =
        produce(a.rowNum, a.colNum) { i, j -> a[i, j] * k.toDouble() }

    public override operator fun Matrix<Double>.times(value: Double): EjmlMatrix =
        EjmlMatrix(toEjml().origin.scale(value))
}

/**
 * Solves for X in the following equation: x = a^-1*b, where 'a' is base matrix and 'b' is an n by p matrix.
 *
 * @param a the base matrix.
 * @param b n by p matrix.
 * @return the solution for 'x' that is n by p.
 * @author Iaroslav Postovalov
 */
public fun EjmlMatrixContext.solve(a: Matrix<Double>, b: Matrix<Double>): EjmlMatrix =
    EjmlMatrix(a.toEjml().origin.solve(b.toEjml().origin))

/**
 * Solves for X in the following equation: x = a^(-1)*b, where 'a' is base matrix and 'b' is an n by p matrix.
 *
 * @param a the base matrix.
 * @param b n by p vector.
 * @return the solution for 'x' that is n by p.
 * @author Iaroslav Postovalov
 */
public fun EjmlMatrixContext.solve(a: Matrix<Double>, b: Point<Double>): EjmlVector =
    EjmlVector(a.toEjml().origin.solve(b.toEjml().origin))

/**
 * Returns the inverse of given matrix: b = a^(-1).
 *
 * @param a the matrix.
 * @return the inverse of this matrix.
 * @author Iaroslav Postovalov
 */
public fun EjmlMatrixContext.inverse(a: Matrix<Double>): EjmlMatrix = EjmlMatrix(a.toEjml().origin.invert())
