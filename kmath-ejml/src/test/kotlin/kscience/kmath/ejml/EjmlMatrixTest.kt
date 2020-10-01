package kscience.kmath.ejml

import kscience.kmath.linear.DeterminantFeature
import kscience.kmath.linear.LUPDecompositionFeature
import kscience.kmath.linear.MatrixFeature
import kscience.kmath.linear.getFeature
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.*

internal class EjmlMatrixTest {
    private val random = Random(0)

    private val randomMatrix: SimpleMatrix
        get() {
            val s = random.nextInt(2, 100)
            return SimpleMatrix.random_DDRM(s, s, 0.0, 10.0, random.asJavaRandom())
        }

    @Test
    fun rowNum() {
        val m = randomMatrix
        assertEquals(m.numRows(), EjmlMatrix(m).rowNum)
    }

    @Test
    fun colNum() {
        val m = randomMatrix
        assertEquals(m.numCols(), EjmlMatrix(m).rowNum)
    }

    @Test
    fun shape() {
        val m = randomMatrix
        val w = EjmlMatrix(m)
        assertEquals(listOf(m.numRows(), m.numCols()), w.shape.toList())
    }

    @Test
    fun features() {
        val m = randomMatrix
        val w = EjmlMatrix(m)
        val det = w.getFeature<DeterminantFeature<Double>>() ?: fail()
        assertEquals(m.determinant(), det.determinant)
        val lup = w.getFeature<LUPDecompositionFeature<Double>>() ?: fail()

        val ludecompositionF64 = DecompositionFactory_DDRM.lu(m.numRows(), m.numCols())
            .also { it.decompose(m.ddrm.copy()) }

        assertEquals(EjmlMatrix(SimpleMatrix(ludecompositionF64.getLower(null))), lup.l)
        assertEquals(EjmlMatrix(SimpleMatrix(ludecompositionF64.getUpper(null))), lup.u)
        assertEquals(EjmlMatrix(SimpleMatrix(ludecompositionF64.getRowPivot(null))), lup.p)
    }

    private object SomeFeature : MatrixFeature {}

    @Test
    fun suggestFeature() {
        assertNotNull(EjmlMatrix(randomMatrix).suggestFeature(SomeFeature).getFeature<SomeFeature>())
    }

    @Test
    fun get() {
        val m = randomMatrix
        assertEquals(m[0, 0], EjmlMatrix(m)[0, 0])
    }

    @Test
    fun origin() {
        val m = randomMatrix
        assertSame(m, EjmlMatrix(m).origin)
    }
}
