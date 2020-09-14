package scientifik.kmath.ejml

import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix
import scientifik.kmath.linear.DeterminantFeature
import scientifik.kmath.linear.FeaturedMatrix
import scientifik.kmath.linear.LUPDecompositionFeature
import scientifik.kmath.linear.MatrixFeature
import scientifik.kmath.structures.NDStructure

/**
 * Represents featured matrix over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 */
class EjmlMatrix(val origin: SimpleMatrix, features: Set<MatrixFeature>? = null) : FeaturedMatrix<Double> {
    override val rowNum: Int
        get() = origin.numRows()

    override val colNum: Int
        get() = origin.numCols()

    override val shape: IntArray
        get() = intArrayOf(origin.numRows(), origin.numCols())

    override val features: Set<MatrixFeature> = setOf(
        object : LUPDecompositionFeature<Double>, DeterminantFeature<Double> {
            override val determinant: Double
                get() = origin.determinant()

            private val lup by lazy {
                val ludecompositionF64 = DecompositionFactory_DDRM.lu(origin.numRows(), origin.numCols())
                    .also { it.decompose(origin.ddrm.copy()) }

                Triple(
                    EjmlMatrix(SimpleMatrix(ludecompositionF64.getRowPivot(null))),
                    EjmlMatrix(SimpleMatrix(ludecompositionF64.getLower(null))),
                    EjmlMatrix(SimpleMatrix(ludecompositionF64.getUpper(null)))
                )
            }

            override val l: FeaturedMatrix<Double>
                get() = lup.second

            override val u: FeaturedMatrix<Double>
                get() = lup.third

            override val p: FeaturedMatrix<Double>
                get() = lup.first
        }
    ) union features.orEmpty()

    override fun suggestFeature(vararg features: MatrixFeature): FeaturedMatrix<Double> =
        EjmlMatrix(origin, this.features + features)

    override operator fun get(i: Int, j: Int): Double = origin[i, j]

    override fun equals(other: Any?): Boolean {
        if (other is EjmlMatrix) return origin.isIdentical(other.origin, 0.0)
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }
}
