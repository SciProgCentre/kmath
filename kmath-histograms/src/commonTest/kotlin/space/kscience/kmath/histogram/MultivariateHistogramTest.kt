package space.kscience.kmath.histogram

import space.kscience.kmath.operations.invoke
import space.kscience.kmath.real.RealVector
import space.kscience.kmath.real.invoke
import kotlin.random.Random
import kotlin.test.*

internal class MultivariateHistogramTest {
    @Test
    fun testSinglePutHistogram() {
        val hSpace = RealHistogramSpace.fromRanges(
            (-1.0..1.0),
            (-1.0..1.0)
        )
        val histogram = hSpace.produce {
            put(0.55, 0.55)
        }
        val bin = histogram.bins.find { it.value.toInt() > 0 } ?: fail()
        assertTrue { bin.contains(RealVector(0.55, 0.55)) }
        assertTrue { bin.contains(RealVector(0.6, 0.5)) }
        assertFalse { bin.contains(RealVector(-0.55, 0.55)) }
    }

    @Test
    fun testSequentialPut() {
        val hSpace = RealHistogramSpace.fromRanges(
            (-1.0..1.0),
            (-1.0..1.0),
            (-1.0..1.0)
        )
        val random = Random(1234)

        fun nextDouble() = random.nextDouble(-1.0, 1.0)

        val n = 10000
        val histogram = hSpace.produce {
            repeat(n) {
                put(nextDouble(), nextDouble(), nextDouble())
            }
        }
        assertEquals(n, histogram.bins.sumBy { it.value.toInt() })
    }

    @Test
    fun testHistogramAlgebra() {
       RealHistogramSpace.fromRanges(
            (-1.0..1.0),
            (-1.0..1.0),
            (-1.0..1.0)
        ).invoke {
            val random = Random(1234)

            fun nextDouble() = random.nextDouble(-1.0, 1.0)
            val n = 10000
            val histogram1 = produce {
                repeat(n) {
                    put(nextDouble(), nextDouble(), nextDouble())
                }
            }
            val histogram2 = produce {
                repeat(n) {
                    put(nextDouble(), nextDouble(), nextDouble())
                }
            }
            val res = histogram1 - histogram2
            assertTrue {
                strides.indices().all { index ->
                    res.values[index] <= histogram1.values[index]
                }
            }
            assertTrue {
                res.bins.count() >= histogram1.bins.count()
            }
            assertEquals(0.0, res.bins.sumByDouble { it.value.toDouble() })
        }
    }
}