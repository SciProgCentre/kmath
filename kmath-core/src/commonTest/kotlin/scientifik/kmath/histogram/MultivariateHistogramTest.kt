package scientifik.kmath.histogram

import scientifik.kmath.linear.Vector
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MultivariateHistogramTest {
    @Test
    fun testSinglePutHistogram() {
        val histogram = FastHistogram.fromRanges(
                (-1.0..1.0),
                (-1.0..1.0)
        )
        histogram.put(0.6, 0.6)
        val bin = histogram.find { it.value.toInt() > 0 }!!
        assertTrue { bin.contains(Vector.ofReal(0.6, 0.6)) }
        assertFalse { bin.contains(Vector.ofReal(-0.6, 0.6)) }
    }

    @Test
    fun testSequentialPut(){
        val histogram = FastHistogram.fromRanges(
                (-1.0..1.0),
                (-1.0..1.0),
                (-1.0..1.0)
        )
        val random = Random(1234)

        fun nextDouble() = random.nextDouble(-1.0,1.0)

        val n = 10000

        histogram.fill {
            repeat(n){
                yield(Vector.ofReal(nextDouble(),nextDouble(),nextDouble()))
            }
        }
        assertEquals(n, histogram.sumBy { it.value.toInt() })
    }
}