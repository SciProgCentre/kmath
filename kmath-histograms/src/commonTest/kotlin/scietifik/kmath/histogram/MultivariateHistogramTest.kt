package scietifik.kmath.histogram

import kscience.kmath.histogram.RealHistogram
import kscience.kmath.histogram.fill
import kscience.kmath.histogram.put
import kscience.kmath.real.RealVector
import kotlin.random.Random
import kotlin.test.*

internal class MultivariateHistogramTest {
    @Test
    fun testSinglePutHistogram() {
        val histogram = RealHistogram.fromRanges(
            (-1.0..1.0),
            (-1.0..1.0)
        )
        histogram.put(0.55, 0.55)
        val bin = histogram.find { it.value.toInt() > 0 } ?: fail()
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