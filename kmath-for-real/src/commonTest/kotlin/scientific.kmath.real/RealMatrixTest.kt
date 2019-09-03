package scientific.kmath.real

import scientifik.kmath.real.average
import scientifik.kmath.real.realMatrix
import scientifik.kmath.real.sum
import kotlin.test.Test
import kotlin.test.assertEquals

class RealMatrixTest {
    @Test
    fun testSum() {
        val m = realMatrix(10, 10) { i, j -> (i + j).toDouble() }
        assertEquals(m.sum(), 900.0)
        assertEquals(m.average(), 9.0)
    }
}
