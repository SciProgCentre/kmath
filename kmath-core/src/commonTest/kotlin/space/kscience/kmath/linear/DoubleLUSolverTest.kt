package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T : Any> assertMatrixEquals(expected: StructureND<T>, actual: StructureND<T>) {
    assertTrue { StructureND.contentEquals(expected, actual) }
}

@UnstableKMathAPI
class DoubleLUSolverTest {

    @Test
    fun testInvertOne() {
        val matrix = LinearSpace.real.one(2, 2)
        val inverted = LinearSpace.real.inverseWithLup(matrix)
        assertMatrixEquals(matrix, inverted)
    }

    @Test
    fun testDecomposition() {
        LinearSpace.real.run {
            val matrix = matrix(2, 2)(
                3.0, 1.0,
                2.0, 3.0
            )

            val lup = lup(matrix)

            //Check determinant
            assertEquals(7.0, lup.determinant)

            assertMatrixEquals(lup.p dot matrix, lup.l dot lup.u)
        }
    }

    @Test
    fun testInvert() {
        val matrix = LinearSpace.real.matrix(2, 2)(
            3.0, 1.0,
            1.0, 3.0
        )

        val inverted = LinearSpace.real.inverseWithLup(matrix)

        val expected = LinearSpace.real.matrix(2, 2)(
            0.375, -0.125,
            -0.125, 0.375
        )

        assertMatrixEquals(expected, inverted)
    }
}
