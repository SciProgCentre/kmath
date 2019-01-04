package scientifik.kmath.linear

import kotlin.test.Test
import kotlin.test.assertEquals

class RealLUSolverTest {
    @Test
    fun testInvertOne() {
        val matrix = MatrixSpace.real(2, 2).one
        val inverted = RealLUSolver.inverse(matrix)
        assertEquals(matrix, inverted)
    }

//    @Test
//    fun testInvert() {
//        val matrix = realMatrix(2,2){}
//        val inverted = RealLUSolver.inverse(matrix)
//        assertTrue { Matrix.equals(matrix,inverted) }
//    }
}