package scientifik.kmath.structures

import org.junit.Assert.assertEquals
import kotlin.math.pow
import kotlin.test.Test

class RealNDFieldTest {
    val array1 = real2DArray(3, 3) { i, j -> (i + j).toDouble() }
    val array2 = real2DArray(3, 3) { i, j -> (i - j).toDouble() }

    @Test
    fun testSum() {
        val sum = array1 + array2
        assertEquals(4.0, sum[2, 2], 0.1)
    }

    @Test
    fun testProduct() {
        val product = array1 * array2
        assertEquals(0.0, product[2, 2], 0.1)
    }

    @Test
    fun testGeneration() {

        val array = real2DArray(3, 3) { i, j -> (i * 10 + j).toDouble() }

        for (i in 0..2) {
            for (j in 0..2) {
                val expected = (i * 10 + j).toDouble()
                assertEquals("Error at index [$i, $j]", expected, array[i, j], 0.1)
            }
        }
    }

    @Test
    fun testExternalFunction() {
        val function: (Double) -> Double = { x -> x.pow(2) + 2 * x + 1 }
        val result = function(array1) + 1.0
        assertEquals(10.0, result[1,1],0.01)
    }
}
