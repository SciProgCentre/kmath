package space.kscience.kmath.linear

import kotlin.test.Test
import kotlin.test.assertEquals

class RealLUSolverTest {

    @Test
    fun testInvertOne() {
        val matrix = MatrixContext.real.one(2, 2)
        val inverted = MatrixContext.real.inverseWithLup(matrix)
        assertEquals(matrix, inverted)
    }

    @Test
    fun testDecomposition() {
        val matrix = Matrix.square(
            3.0, 1.0,
            1.0, 3.0
        )

        MatrixContext.real.run {
            val lup = lup(matrix)

            //Check determinant
            assertEquals(8.0, lup.determinant)

            assertEquals(lup.p dot matrix, lup.l dot lup.u)
        }
    }

    @Test
    fun testInvert() {
        val matrix = Matrix.square(
            3.0, 1.0,
            1.0, 3.0
        )

        val inverted = MatrixContext.real.inverseWithLup(matrix)

        val expected = Matrix.square(
            0.375, -0.125,
            -0.125, 0.375
        )

        assertEquals(expected, inverted)
    }
}
