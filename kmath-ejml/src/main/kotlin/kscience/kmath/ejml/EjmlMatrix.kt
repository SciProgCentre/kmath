package kscience.kmath.ejml

import kscience.kmath.linear.DeterminantFeature
import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.linear.LUPDecompositionFeature
import kscience.kmath.linear.MatrixFeature
import kscience.kmath.structures.NDStructure
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix

/**
 * Represents featured matrix over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public class EjmlMatrix(public val origin: SimpleMatrix, features: Set<MatrixFeature>? = null) :
    FeaturedMatrix<Double> {
    public override val rowNum: Int
        get() = origin.numRows()

    public override val colNum: Int
        get() = origin.numCols()

    public override val shape: IntArray
        get() = intArrayOf(origin.numRows(), origin.numCols())

    public override val features: Set<MatrixFeature> = setOf(
        object : LUPDecompositionFeature<Double>, DeterminantFeature<Double> {
            override val determinant: Double
                get() = origin.determinant()

            private val lup by lazy {
                val ludecompositionF64 = DecompositionFactory_DDRM.lu(origin.numRows(), origin.numCols())
                    .also { it.decompose(origin.ddrm.copy()) }

                Triple(
                    EjmlMatrix(SimpleMatrix(ludecompositionF64.getRowPivot(null))),
                    EjmlMatrix(SimpleMatrix(ludecompositionF64.getLower(null))),
                    EjmlMatrix(SimpleMatrix(ludecompositionF64.getUpper(null))),
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
