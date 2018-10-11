package scientifik.kmath.linear

import scientifik.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals

class RealLUSolverTest {
    @Test
    fun testInvert() {
        val matrix = Matrix.one(2, 2, DoubleField)
        val inverted = RealLUSolver.inverse(matrix)
        assertEquals(1.0, inverted[0, 0])
    }
}