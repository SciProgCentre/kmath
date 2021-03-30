package space.kscience.kmath.ejml

import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.getFeature
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.*

fun <T : Any> assertMatrixEquals(expected: StructureND<T>, actual: StructureND<T>) {
    assertTrue { StructureND.contentEquals(expected, actual) }
}

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

    @OptIn(UnstableKMathAPI::class)
    @Test
    fun features() {
        val m = randomMatrix
        val w = EjmlMatrix(m)
        val det: DeterminantFeature<Double> = EjmlLinearSpace.getFeature(w) ?: fail()
        assertEquals(m.determinant(), det.determinant)
        val lup: LupDecompositionFeature<Double> = EjmlLinearSpace.getFeature(w) ?: fail()

        val ludecompositionF64 = DecompositionFactory_DDRM.lu(m.numRows(), m.numCols())
            .also { it.decompose(m.ddrm.copy()) }

        assertMatrixEquals(EjmlMatrix(SimpleMatrix(ludecompositionF64.getLower(null))), lup.l)
        assertMatrixEquals(EjmlMatrix(SimpleMatrix(ludecompositionF64.getUpper(null))), lup.u)
        assertMatrixEquals(EjmlMatrix(SimpleMatrix(ludecompositionF64.getRowPivot(null))), lup.p)
    }

    private object SomeFeature : MatrixFeature {}

    @OptIn(UnstableKMathAPI::class)
    @Test
    fun suggestFeature() {
        assertNotNull((EjmlMatrix(randomMatrix) + SomeFeature).getFeature<SomeFeature>())
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
