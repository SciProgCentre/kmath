package scientifik.kmath.structures

import org.junit.Assert.assertEquals
import kotlin.test.Test

class RealNDFieldTest {
    val array1 = real2DArray(3, 3) { i, j -> (i + j).toDouble() }
    val array2 = real2DArray(3, 3) { i, j -> (i - j).toDouble() }

    @Test
    fun testSum() {
        val sum = array1 + array2
        assertEquals(4.0, sum[2, 2].value, 0.1)
    }
}