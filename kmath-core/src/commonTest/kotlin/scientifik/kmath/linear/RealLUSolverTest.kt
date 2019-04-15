package scientifik.kmath.linear

import scientifik.kmath.structures.Matrix
import kotlin.contracts.ExperimentalContracts
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalContracts
class RealLUSolverTest {

    @Test
    fun testInvertOne() {
        val matrix = MatrixContext.real.one(2, 2)
        val inverted = MatrixContext.real.inverse(matrix)
        assertEquals(matrix, inverted)
    }

    @Test
    fun testInvert() {
        val matrix = Matrix.square(
            3.0, 1.0,
            1.0, 3.0
        )

        val decomposition = MatrixContext.real.lup(matrix)

        //Check determinant
        assertEquals(8.0, decomposition.determinant)

        //Check decomposition
        with(MatrixContext.real) {
            assertEquals(decomposition.p dot matrix, decomposition.l dot decomposition.u)
        }

        val inverted = MatrixContext.real.inverse(matrix)

        val expected = Matrix.square(
            0.375, -0.125,
            -0.125, 0.375
        )

        assertEquals(expected, inverted)
    }
}