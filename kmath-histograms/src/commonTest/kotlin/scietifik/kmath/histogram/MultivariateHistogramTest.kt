package scietifik.kmath.histogram

import scientifik.kmath.histogram.RealHistogram
import scientifik.kmath.histogram.fill
import scientifik.kmath.histogram.put
import scientifik.kmath.linear.RealVector
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MultivariateHistogramTest {
    @Test
    fun testSinglePutHistogram() {
        val histogram = RealHistogram.fromRanges(
            (-1.0..1.0),
            (-1.0..1.0)
        )
        histogram.put(0.55, 0.55)
        val bin = histogram.find { it.value.toInt() > 0 }!!
        assertTrue { bin.contains(RealVector(0.55, 0.55)) }
        assertTrue { bin.contains(RealVector(0.6, 0.5)) }
        assertFalse { bin.contains(RealVector(-0.55, 0.55)) }
    }

    @Test
    fun testSequentialPut() {
        val histogram = RealHistogram.fromRanges(
            (-1.0..1.0),
            (-1.0..1.0),
            (-1.0..1.0)
        )
        val random = Random(1234)

        fun nextDouble() = random.nextDouble(-1.0, 1.0)

        val n = 10000

        histogram.fill {
            repeat(n) {
                yield(RealVector(nextDouble(), nextDouble(), nextDouble()))
            }
        }
        assertEquals(n, histogram.sumBy { it.value.toInt() })
    }
}