package kscience.kmath.ejml

import kscience.kmath.linear.InverseMatrixFeature
import kscience.kmath.linear.MatrixContext
import kscience.kmath.linear.Point
import kscience.kmath.linear.origin
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.getFeature
import org.ejml.simple.SimpleMatrix

/**
 * Represents context of basic operations operating with [EjmlMatrix].
 *
 * @author Iaroslav Postovalov
 */
public object EjmlMatrixContext : MatrixContext<Double, EjmlMatrix> {

    /**
     * Converts this matrix to EJML one.
     */
    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<Double>.toEjml(): EjmlMatrix = when (val matrix = origin) {
        is EjmlMatrix -> matrix
        else -> produce(rowNum, colNum) { i, j -> get(i, j) }
    }

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

    override fun point(size: Int, initializer: (Int) -> Double): Point<Double> =
        EjmlVector(SimpleMatrix(size, 1).also {
            (0 until it.numRows()).forEach { row -> it[row, 0] = initializer(row) }
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

@OptIn(UnstableKMathAPI::class)
public fun EjmlMatrix.inverted(): EjmlMatrix = getFeature<InverseMatrixFeature<Double>>()!!.inverse as EjmlMatrix

public fun EjmlMatrixContext.inverse(matrix: Matrix<Double>): Matrix<Double> = matrix.toEjml().inverted()