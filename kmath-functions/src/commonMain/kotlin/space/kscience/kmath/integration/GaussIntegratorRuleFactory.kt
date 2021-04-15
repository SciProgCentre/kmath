package space.kscience.kmath.integration

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.*
import kotlin.jvm.Synchronized
import kotlin.math.ulp
import kotlin.native.concurrent.ThreadLocal

public interface GaussIntegratorRuleFactory<T : Any> {
    public val algebra: Field<T>
    public val bufferFactory: BufferFactory<T>
    public fun build(numPoints: Int): Pair<Buffer<T>, Buffer<T>>

    public companion object {
        public fun double(numPoints: Int, range: ClosedRange<Double>): Pair<Buffer<Double>, Buffer<Double>> =
            GaussLegendreDoubleRuleFactory.build(numPoints, range)
    }
}

/**
 * Create an integration rule by scaling existing normalized rule
 */
public fun <T : Comparable<T>> GaussIntegratorRuleFactory<T>.build(
    numPoints: Int,
    range: ClosedRange<T>,
): Pair<Buffer<T>, Buffer<T>> {
    val normalized = build(numPoints)
    val points = with(algebra) {
        val length = range.endInclusive - range.start
        normalized.first.map(bufferFactory) {
            range.start + length / 2 + length * it/2
        }
    }

    return points to normalized.second
}


/**
 * Gauss integrator rule based ont Legendre polynomials. All rules are normalized to
 *
 * The code is based on Apache Commons Math source code version 3.6.1
 * https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/org/apache/commons/math3/analysis/integration/gauss/LegendreRuleFactory.html
 */
@ThreadLocal
public object GaussLegendreDoubleRuleFactory : GaussIntegratorRuleFactory<Double> {

    override val algebra: Field<Double> get() = DoubleField

    override val bufferFactory: BufferFactory<Double> get() = ::DoubleBuffer

    private val cache = HashMap<Int, Pair<Buffer<Double>, Buffer<Double>>>()

    @Synchronized
    private fun getOrBuildRule(numPoints: Int): Pair<Buffer<Double>, Buffer<Double>> =
        cache.getOrPut(numPoints) { buildRule(numPoints) }


    private fun buildRule(numPoints: Int): Pair<Buffer<Double>, Buffer<Double>> {
        if (numPoints == 1) {
            // Break recursion.
            return Pair(
                DoubleBuffer(0.0),
                DoubleBuffer(0.0)
            )
        }

        // Get previous rule.
        // If it has not been computed yet it will trigger a recursive call
        // to this method.
        val previousPoints: Buffer<Double> = getOrBuildRule(numPoints - 1).first

        // Compute next rule.
        val points = DoubleArray(numPoints)
        val weights = DoubleArray(numPoints)

        // Find i-th root of P[n+1] by bracketing.
        val iMax = numPoints / 2
        for (i in 0 until iMax) {
            // Lower-bound of the interval.
            var a: Double = if (i == 0) -1.0 else previousPoints[i - 1]
            // Upper-bound of the interval.
            var b: Double = if (iMax == 1) 1.0 else previousPoints[i]
            // P[j-1](a)
            var pma = 1.0
            // P[j](a)
            var pa = a
            // P[j-1](b)
            var pmb = 1.0
            // P[j](b)
            var pb = b
            for (j in 1 until numPoints) {
                val two_j_p_1 = 2 * j + 1
                val j_p_1 = j + 1
                // P[j+1](a)
                val ppa = (two_j_p_1 * a * pa - j * pma) / j_p_1
                // P[j+1](b)
                val ppb = (two_j_p_1 * b * pb - j * pmb) / j_p_1
                pma = pa
                pa = ppa
                pmb = pb
                pb = ppb
            }
            // Now pa = P[n+1](a), and pma = P[n](a) (same holds for b).
            // Middle of the interval.
            var c = 0.5 * (a + b)
            // P[j-1](c)
            var pmc = 1.0
            // P[j](c)
            var pc = c
            var done = false
            while (!done) {
                done = b - a <= c.ulp
                pmc = 1.0
                pc = c
                for (j in 1 until numPoints) {
                    // P[j+1](c)
                    val ppc = ((2 * j + 1) * c * pc - j * pmc) / (j + 1)
                    pmc = pc
                    pc = ppc
                }
                // Now pc = P[n+1](c) and pmc = P[n](c).
                if (!done) {
                    if (pa * pc <= 0) {
                        b = c
                        pmb = pmc
                        pb = pc
                    } else {
                        a = c
                        pma = pmc
                        pa = pc
                    }
                    c = 0.5 * (a + b)
                }
            }
            val d = numPoints * (pmc - c * pc)
            val w = 2 * (1 - c * c) / (d * d)
            points[i] = c
            weights[i] = w
            val idx = numPoints - i - 1
            points[idx] = -c
            weights[idx] = w
        }
        // If "numberOfPoints" is odd, 0 is a root.
        // Note: as written, the test for oddness will work for negative
        // integers too (although it is not necessary here), preventing
        // a FindBugs warning.
        if (numPoints % 2 != 0) {
            var pmc = 1.0
            var j = 1
            while (j < numPoints) {
                pmc = -j * pmc / (j + 1)
                j += 2
            }
            val d = numPoints * pmc
            val w = 2 / (d * d)
            points[iMax] = 0.0
            weights[iMax] = w
        }
        return Pair(points.asBuffer(), weights.asBuffer())
    }

    override fun build(numPoints: Int): Pair<Buffer<Double>, Buffer<Double>> = getOrBuildRule(numPoints)
}

