package space.kscience.kmath.stat.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.ConstantChain
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.stat.chain
import space.kscience.kmath.stat.internal.InternalUtils
import space.kscience.kmath.stat.next
import kotlin.math.*

/**
 * Sampler for the Poisson distribution.
 * - For large means, we use the rejection algorithm described in
 *   Devroye, Luc. (1981).The Computer Generation of Poisson Random Variables
 *   Computing vol. 26 pp. 197-207.
 *
 * This sampler is suitable for mean >= 40.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/LargeMeanPoissonSampler.html].
 */
public class LargeMeanPoissonSampler private constructor(public val mean: Double) : Sampler<Int> {
    private val exponential: Sampler<Double> = AhrensDieterExponentialSampler.of(1.0)
    private val gaussian: Sampler<Double> = ZigguratNormalizedGaussianSampler.of()
    private val factorialLog: InternalUtils.FactorialLog = NO_CACHE_FACTORIAL_LOG
    private val lambda: Double = floor(mean)
    private val logLambda: Double = ln(lambda)
    private val logLambdaFactorial: Double = getFactorialLog(lambda.toInt())
    private val delta: Double = sqrt(lambda * ln(32 * lambda / PI + 1))
    private val halfDelta: Double = delta / 2
    private val twolpd: Double = 2 * lambda + delta
    private val c1: Double = 1 / (8 * lambda)
    private val a1: Double = sqrt(PI * twolpd) * exp(c1)
    private val a2: Double = twolpd / delta * exp(-delta * (1 + delta) / twolpd)
    private val aSum: Double = a1 + a2 + 1
    private val p1: Double = a1 / aSum
    private val p2: Double = a2 / aSum

    private val smallMeanPoissonSampler: Sampler<Int> = if (mean - lambda < Double.MIN_VALUE)
        NO_SMALL_MEAN_POISSON_SAMPLER
    else  // Not used.
        KempSmallMeanPoissonSampler.of(mean - lambda)

    public override fun sample(generator: RandomGenerator): Chain<Int> = generator.chain {
        // This will never be null. It may be a no-op delegate that returns zero.
        val y2 = smallMeanPoissonSampler.next(generator)
        var x: Double
        var y: Double
        var v: Double
        var a: Int
        var t: Double
        var qr: Double
        var qa: Double

        while (true) {
            // Step 1:
            val u = generator.nextDouble()

            if (u <= p1) {
                // Step 2:
                val n = gaussian.next(generator)
                x = n * sqrt(lambda + halfDelta) - 0.5
                if (x > delta || x < -lambda) continue
                y = if (x < 0) floor(x) else ceil(x)
                val e = exponential.next(generator)
                v = -e - 0.5 * n * n + c1
            } else {
                // Step 3:
                if (u > p1 + p2) {
                    y = lambda
                    break
                }

                x = delta + twolpd / delta * exponential.next(generator)
                y = ceil(x)
                v = -exponential.next(generator) - delta * (x + 1) / twolpd
            }

            // The Squeeze Principle
            // Step 4.1:
            a = if (x < 0) 1 else 0
            t = y * (y + 1) / (2 * lambda)

            // Step 4.2
            if (v < -t && a == 0) {
                y += lambda
                break
            }

            // Step 4.3:
            qr = t * ((2 * y + 1) / (6 * lambda) - 1)
            qa = qr - t * t / (3 * (lambda + a * (y + 1)))

            // Step 4.4:
            if (v < qa) {
                y += lambda
                break
            }

            // Step 4.5:
            if (v > qr) continue

            // Step 4.6:
            if (v < y * logLambda - getFactorialLog((y + lambda).toInt()) + logLambdaFactorial) {
                y += lambda
                break
            }
        }

        min(y2 + y.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }

    private fun getFactorialLog(n: Int): Double = factorialLog.value(n)
    public override fun toString(): String = "Large Mean Poisson deviate"

    public companion object {
        private const val MAX_MEAN: Double = 0.5 * Int.MAX_VALUE
        private val NO_CACHE_FACTORIAL_LOG: InternalUtils.FactorialLog = InternalUtils.FactorialLog.create()

        private val NO_SMALL_MEAN_POISSON_SAMPLER: Sampler<Int> = Sampler { ConstantChain(0) }

        public fun of(mean: Double): LargeMeanPoissonSampler {
            require(mean >= 1) { "mean is not >= 1: $mean" }
            // The algorithm is not valid if Math.floor(mean) is not an integer.
            require(mean <= MAX_MEAN) { "mean $mean > $MAX_MEAN" }
            return LargeMeanPoissonSampler(mean)
        }
    }
}
