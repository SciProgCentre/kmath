package space.kscience.kmath.ejml

import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.getFeature
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.ScaleOperations

/**
 * Represents context of basic operations operating with [EjmlMatrix].
 *
 * @author Iaroslav Postovalov
 */
public object EjmlLinearSpace : LinearSpace<Double, RealField>, ScaleOperations<Matrix<Double>> {

    override val elementAlgebra: RealField get() = RealField

    /**
     * Converts this matrix to EJML one.
     */
    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<Double>.toEjml(): EjmlMatrix = when (val matrix = origin) {
        is EjmlMatrix -> matrix
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    /**
     * Converts this vector to EJML one.
     */
    public fun Vector<Double>.toEjml(): EjmlVector = when (this) {
        is EjmlVector -> this
        else -> EjmlVector(SimpleMatrix(size, 1).also {
            (0 until it.numRows()).forEach { row -> it[row, 0] = get(row) }
        })
    }

    override fun buildMatrix(rows: Int, columns: Int, initializer: RealField.(i: Int, j: Int) -> Double): EjmlMatrix =
        EjmlMatrix(SimpleMatrix(rows, columns).also {
            (0 until rows).forEach { row ->
                (0 until columns).forEach { col -> it[row, col] = RealField.initializer(row, col) }
            }
        })

    override fun buildVector(size: Int, initializer: RealField.(Int) -> Double): Vector<Double> =
        EjmlVector(SimpleMatrix(size, 1).also {
            (0 until it.numRows()).forEach { row -> it[row, 0] = RealField.initializer(row) }
        })

    private fun SimpleMatrix.wrapMatrix() = EjmlMatrix(this)
    private fun SimpleMatrix.wrapVector() = EjmlVector(this)

    override fun Matrix<Double>.unaryMinus(): Matrix<Double> = this * (-1)

    public override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(toEjml().origin.mult(other.toEjml().origin))

    public override fun Matrix<Double>.dot(vector: Vector<Double>): EjmlVector =
        EjmlVector(toEjml().origin.mult(vector.toEjml().origin))

    public override operator fun Matrix<Double>.minus(other: Matrix<Double>): EjmlMatrix =
        (toEjml().origin - other.toEjml().origin).wrapMatrix()

    public override fun scale(a: Matrix<Double>, value: Double): EjmlMatrix =
        a.toEjml().origin.scale(value).wrapMatrix()

    public override operator fun Matrix<Double>.times(value: Double): EjmlMatrix =
        toEjml().origin.scale(value).wrapMatrix()

    override fun Vector<Double>.unaryMinus(): EjmlVector =
        toEjml().origin.negative().wrapVector()

    override fun Matrix<Double>.plus(other: Matrix<Double>): EjmlMatrix =
        (toEjml().origin + other.toEjml().origin).wrapMatrix()

    override fun Vector<Double>.plus(other: Vector<Double>): EjmlVector =
        (toEjml().origin + other.toEjml().origin).wrapVector()

    override fun Vector<Double>.minus(other: Vector<Double>): EjmlVector =
        (toEjml().origin - other.toEjml().origin).wrapVector()

    override fun Double.times(m: Matrix<Double>): EjmlMatrix =
        m.toEjml().origin.scale(this).wrapMatrix()

    override fun Vector<Double>.times(value: Double): EjmlVector =
        toEjml().origin.scale(value).wrapVector()

    override fun Double.times(v: Vector<Double>): EjmlVector =
        v.toEjml().origin.scale(this).wrapVector()
}

/**
 * Solves for X in the following equation: x = a^-1*b, where 'a' is base matrix and 'b' is an n by p matrix.
 *
 * @param a the base matrix.
 * @param b n by p matrix.
 * @return the solution for 'x' that is n by p.
 * @author Iaroslav Postovalov
 */
public fun EjmlLinearSpace.solve(a: Matrix<Double>, b: Matrix<Double>): EjmlMatrix =
    EjmlMatrix(a.toEjml().origin.solve(b.toEjml().origin))

/**
 * Solves for X in the following equation: x = a^(-1)*b, where 'a' is base matrix and 'b' is an n by p matrix.
 *
 * @param a the base matrix.
 * @param b n by p vector.
 * @return the solution for 'x' that is n by p.
 * @author Iaroslav Postovalov
 */
public fun EjmlLinearSpace.solve(a: Matrix<Double>, b: Point<Double>): EjmlVector =
    EjmlVector(a.toEjml().origin.solve(b.toEjml().origin))

@OptIn(UnstableKMathAPI::class)
public fun EjmlMatrix.inverted(): EjmlMatrix = getFeature<InverseMatrixFeature<Double>>()!!.inverse as EjmlMatrix

public fun EjmlLinearSpace.inverse(matrix: Matrix<Double>): Matrix<Double> = matrix.toEjml().inverted()