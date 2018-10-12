package scientifik.kmath.linear

import scientifik.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertTrue

class RealLUSolverTest {
    @Test
    fun testInvertOne() {
        val matrix = Matrix.diagonal(2, 2, DoubleField)
        val inverted = RealLUSolver.inverse(matrix)
        assertTrue { Matrix.equals(matrix,inverted) }
    }

//    @Test
//    fun testInvert() {
//        val matrix = realMatrix(2,2){}
//        val inverted = RealLUSolver.inverse(matrix)
//        assertTrue { Matrix.equals(matrix,inverted) }
//    }
}