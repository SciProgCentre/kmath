package space.kscience.kmath.ejml

import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.getFeature
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.structures.RealBuffer
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Represents context of basic operations operating with [EjmlMatrix].
 *
 * @author Iaroslav Postovalov
 */
public object EjmlLinearSpace : LinearSpace<Double, RealField> {

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
    public fun Point<Double>.toEjml(): EjmlVector = when (this) {
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

    override fun buildVector(size: Int, initializer: RealField.(Int) -> Double): Point<Double> =
        EjmlVector(SimpleMatrix(size, 1).also {
            (0 until it.numRows()).forEach { row -> it[row, 0] = RealField.initializer(row) }
        })

    private fun SimpleMatrix.wrapMatrix() = EjmlMatrix(this)
    private fun SimpleMatrix.wrapVector() = EjmlVector(this)

    override fun Matrix<Double>.unaryMinus(): Matrix<Double> = this * (-1.0)

    public override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlMatrix =
        EjmlMatrix(toEjml().origin.mult(other.toEjml().origin))

    public override fun Matrix<Double>.dot(vector: Point<Double>): EjmlVector =
        EjmlVector(toEjml().origin.mult(vector.toEjml().origin))

    public override operator fun Matrix<Double>.minus(other: Matrix<Double>): EjmlMatrix =
        (toEjml().origin - other.toEjml().origin).wrapMatrix()

    public override operator fun Matrix<Double>.times(value: Double): EjmlMatrix =
        toEjml().origin.scale(value).wrapMatrix()

    override fun Point<Double>.unaryMinus(): EjmlVector =
        toEjml().origin.negative().wrapVector()

    override fun Matrix<Double>.plus(other: Matrix<Double>): EjmlMatrix =
        (toEjml().origin + other.toEjml().origin).wrapMatrix()

    override fun Point<Double>.plus(other: Point<Double>): EjmlVector =
        (toEjml().origin + other.toEjml().origin).wrapVector()

    override fun Point<Double>.minus(other: Point<Double>): EjmlVector =
        (toEjml().origin - other.toEjml().origin).wrapVector()

    override fun Double.times(m: Matrix<Double>): EjmlMatrix =
        m.toEjml().origin.scale(this).wrapMatrix()

    override fun Point<Double>.times(value: Double): EjmlVector =
        toEjml().origin.scale(value).wrapVector()

    override fun Double.times(v: Point<Double>): EjmlVector =
        v.toEjml().origin.scale(this).wrapVector()

    @UnstableKMathAPI
    override fun <F : Any> getFeature(structure: Matrix<Double>, type: KClass<F>): F? {
        //Return the feature if it is intrinsic to the structure
        structure.getFeature(type)?.let { return it }

        val origin = structure.toEjml().origin

        return when (type) {
            InverseMatrixFeature::class -> object : InverseMatrixFeature<Double> {
                override val inverse: Matrix<Double> by lazy { EjmlMatrix(origin.invert()) }
            }

            DeterminantFeature::class -> object : DeterminantFeature<Double> {
                override val determinant: Double by lazy(origin::determinant)
            }

            SingularValueDecompositionFeature::class -> object : SingularValueDecompositionFeature<Double> {
                private val svd by lazy {
                    DecompositionFactory_DDRM.svd(origin.numRows(), origin.numCols(), true, true, false)
                        .apply { decompose(origin.ddrm.copy()) }
                }

                override val u: Matrix<Double> by lazy { EjmlMatrix(SimpleMatrix(svd.getU(null, false))) }
                override val s: Matrix<Double> by lazy { EjmlMatrix(SimpleMatrix(svd.getW(null))) }
                override val v: Matrix<Double> by lazy { EjmlMatrix(SimpleMatrix(svd.getV(null, false))) }
                override val singularValues: Point<Double> by lazy { RealBuffer(svd.singularValues) }
            }

            QRDecompositionFeature::class -> object : QRDecompositionFeature<Double> {
                private val qr by lazy {
                    DecompositionFactory_DDRM.qr().apply { decompose(origin.ddrm.copy()) }
                }

                override val q: Matrix<Double> by lazy {
                    EjmlMatrix(SimpleMatrix(qr.getQ(null, false))) + OrthogonalFeature
                }

                override val r: Matrix<Double> by lazy { EjmlMatrix(SimpleMatrix(qr.getR(null, false))) + UFeature }
            }

            CholeskyDecompositionFeature::class -> object : CholeskyDecompositionFeature<Double> {
                override val l: Matrix<Double> by lazy {
                    val cholesky =
                        DecompositionFactory_DDRM.chol(structure.rowNum, true).apply { decompose(origin.ddrm.copy()) }

                    EjmlMatrix(SimpleMatrix(cholesky.getT(null))) + LFeature
                }
            }

            LupDecompositionFeature::class -> object : LupDecompositionFeature<Double> {
                private val lup by lazy {
                    DecompositionFactory_DDRM.lu(origin.numRows(), origin.numCols())
                        .apply { decompose(origin.ddrm.copy()) }
                }

                override val l: Matrix<Double> by lazy {
                    EjmlMatrix(SimpleMatrix(lup.getLower(null))) + LFeature
                }

                override val u: Matrix<Double> by lazy {
                    EjmlMatrix(SimpleMatrix(lup.getUpper(null))) + UFeature
                }

                override val p: Matrix<Double> by lazy { EjmlMatrix(SimpleMatrix(lup.getRowPivot(null))) }
            }

            else -> null
        }?.let(type::cast)
    }
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