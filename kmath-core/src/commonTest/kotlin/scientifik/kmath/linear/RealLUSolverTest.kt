package scientifik.kmath.linear

import kotlin.test.Test
import kotlin.test.assertEquals

class RealLUSolverTest {
    @Test
    fun testInvertOne() {
        val matrix = MatrixContext.real.one(2, 2)
        val inverted = LUSolver.real.inverse(matrix)
        assertEquals(matrix, inverted)
    }

    @Test
    fun testInvert() {
        val matrix = Matrix.build(
            3.0, 1.0,
            1.0, 3.0
        )

        val decomposed = LUSolver.real.decompose(matrix)
        val decomposition = decomposed.getFeature<LUPDecomposition<Double>>()!!

        //Check determinant
        assertEquals(8.0, decomposition.determinant)

        //Check decomposition
        with(MatrixContext.real) {
            assertEquals(decomposition.p dot matrix, decomposition.l dot decomposition.u)
        }

        val inverted = LUSolver.real.inverse(decomposed)

        val expected = Matrix.build(
            0.375, -0.125,
            -0.125, 0.375
        )

        assertEquals(expected, inverted)
    }
}