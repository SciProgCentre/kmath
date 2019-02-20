package scientifik.kmath.operations

import kotlin.test.Test
import kotlin.test.assertEquals

class RealFieldTest {
    @Test
    fun testSqrt() {
        val sqrt = with(RealField) {
            sqrt(25 * one)
        }
        assertEquals(5.0, sqrt)
    }
}