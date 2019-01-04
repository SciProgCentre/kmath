package scientifik.kmath.operations

import kotlin.test.Test
import kotlin.test.assertEquals

class RealFieldTest {
    @Test
    fun testSqrt() {
        val sqrt = with(DoubleField) {
            sqrt(25 * one)
        }
        assertEquals(5.0, sqrt)
    }
}