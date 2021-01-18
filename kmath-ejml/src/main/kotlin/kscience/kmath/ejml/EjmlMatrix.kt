package kscience.kmath.ejml

import kscience.kmath.linear.*
import kscience.kmath.structures.NDStructure
import kscience.kmath.structures.RealBuffer
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix

/**
 * Represents featured matrix over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public class EjmlMatrix(public val origin: SimpleMatrix, features: Set<MatrixFeature> = emptySet()) :
    FeaturedMatrix<Double> {
    public override val rowNum: Int
        get() = origin.numRows()

    public override val colNum: Int
        get() = origin.numCols()

    public override val shape: IntArray by lazy { intArrayOf(rowNum, colNum) }

    public override val features: Set<MatrixFeature> = hashSetOf(
        object : InverseMatrixFeature<Double> {
            override val inverse: FeaturedMatrix<Double> by lazy { EjmlMatrix(origin.invert()) }
        },

        object : DeterminantFeature<Double> {
            override val determinant: Double by lazy(origin::determinant)
        },

        object : SingularValueDecompositionFeature<Double> {
            private val svd by lazy {
                DecompositionFactory_DDRM.svd(origin.numRows(), origin.numCols(), true, true, false)
                    .apply { decompose(origin.ddrm.copy()) }
            }

            override val u: FeaturedMatrix<Double> by lazy { EjmlMatrix(SimpleMatrix(svd.getU(null, false))) }
            override val s: FeaturedMatrix<Double> by lazy { EjmlMatrix(SimpleMatrix(svd.getW(null))) }
            override val v: FeaturedMatrix<Double> by lazy { EjmlMatrix(SimpleMatrix(svd.getV(null, false))) }
            override val singularValues: Point<Double> by lazy { RealBuffer(svd.singularValues) }
        },

        object : QRDecompositionFeature<Double> {
            private val qr by lazy {
                DecompositionFactory_DDRM.qr().apply { decompose(origin.ddrm.copy()) }
            }

            override val q: FeaturedMatrix<Double> by lazy {
                EjmlMatrix(SimpleMatrix(qr.getQ(null, false)), setOf(OrthogonalFeature))
            }
            override val r: FeaturedMatrix<Double> by lazy {
                EjmlMatrix(SimpleMatrix(qr.getR(null, false)), setOf(UFeature))
            }
        },

        object : CholeskyDecompositionFeature<Double> {
            override val l: FeaturedMatrix<Double> by lazy {
                val cholesky =
                    DecompositionFactory_DDRM.chol(rowNum, true).apply { decompose(origin.ddrm.copy()) }

                EjmlMatrix(SimpleMatrix(cholesky.getT(null)), setOf(LFeature))
            }
        },

        object : LupDecompositionFeature<Double> {
            private val lup by lazy {
                DecompositionFactory_DDRM.lu(origin.numRows(), origin.numCols()).apply { decompose(origin.ddrm.copy()) }
            }

            override val l: FeaturedMatrix<Double> by lazy {
                EjmlMatrix(SimpleMatrix(lup.getLower(null)), setOf(LFeature))
            }

            override val u: FeaturedMatrix<Double> by lazy {
                EjmlMatrix(SimpleMatrix(lup.getUpper(null)), setOf(UFeature))
            }

            override val p: FeaturedMatrix<Double> by lazy { EjmlMatrix(SimpleMatrix(lup.getRowPivot(null))) }
        },
    ) union features

    public override fun suggestFeature(vararg features: MatrixFeature): EjmlMatrix =
        EjmlMatrix(origin, this.features + features)

    public override operator fun get(i: Int, j: Int): Double = origin[i, j]

    public override fun equals(other: Any?): Boolean {
        if (other is EjmlMatrix) return origin.isIdentical(other.origin, 0.0)
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    public override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }

    public override fun toString(): String = "EjmlMatrix(origin=$origin, features=$features)"
}
