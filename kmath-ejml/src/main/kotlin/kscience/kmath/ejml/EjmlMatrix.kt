package kscience.kmath.ejml

import kscience.kmath.linear.*
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.nd.NDStructure
import kscience.kmath.structures.RealBuffer
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Represents featured matrix over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public class EjmlMatrix(public val origin: SimpleMatrix) : Matrix<Double> {
    public override val rowNum: Int get() = origin.numRows()
    public override val colNum: Int get() = origin.numCols()

    @UnstableKMathAPI
    public override fun <T : Any> getFeature(type: KClass<T>): T? = when (type) {
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
                    DecompositionFactory_DDRM.chol(rowNum, true).apply { decompose(origin.ddrm.copy()) }

                EjmlMatrix(SimpleMatrix(cholesky.getT(null))) + LFeature
            }
        }

        LupDecompositionFeature::class -> object : LupDecompositionFeature<Double> {
            private val lup by lazy {
                DecompositionFactory_DDRM.lu(origin.numRows(), origin.numCols()).apply { decompose(origin.ddrm.copy()) }
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

    public override operator fun get(i: Int, j: Int): Double = origin[i, j]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix<*>) return false
        return NDStructure.contentEquals(this, other)
    }

    override fun hashCode(): Int = origin.hashCode()


}
