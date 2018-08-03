package scientifik.kmath.structures

import org.junit.Assert.assertEquals
import org.junit.Test

class ArrayMatrixTest {

    @Test
    fun testSum() {
        val vector1 = realVector(5) { it.toDouble() }
        val vector2 = realVector(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2, 0], 0.1)
    }

    @Test
    fun testDot() {
        val vector1 = realVector(5) { it.toDouble() }
        val vector2 = realVector(5) { 5 - it.toDouble() }
        val product = with(vector1.context) {
            vector1 dot (vector2.transpose())
        }

        assertEquals(10.0, product[1, 0], 0.1)
    }
}