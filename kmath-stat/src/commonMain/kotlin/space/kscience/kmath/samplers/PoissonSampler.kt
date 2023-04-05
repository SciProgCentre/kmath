/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingIntChain
import space.kscience.kmath.misc.toIntExact
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.IntBuffer
import kotlin.math.*


private const val PIVOT = 40.0

/**
 * Sampler for the Poisson distribution.
 * * For small means, a Poisson process is simulated using uniform deviates, as described in
 *   Knuth (1969). Seminumerical Algorithms. The Art of Computer Programming, Volume 2. Chapter 3.4.1.F.3
 *   Important integer-valued distributions: The Poisson distribution. Addison Wesley.
 * The Poisson process (and hence, the returned value) is bounded by 1000 * mean.
 * * For large means, we use the rejection algorithm described in
 *   Devroye, Luc. (1981). The Computer Generation of Poisson Random Variables Computing vol. 26 pp. 197-207.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/PoissonSampler.html].
 */
@Suppress("FunctionName")
public fun PoissonSampler(mean: Double): Sampler<Int> {
    return if (mean < PIVOT) SmallMeanPoissonSampler(mean) else LargeMeanPoissonSampler(mean)
}

/**
 * Sampler for the Poisson distribution.
 * * For small means, a Poisson process is simulated using uniform deviates, as described in
 *   Knuth (1969). Seminumerical Algorithms. The Art of Computer Programming, Volume 2. Chapter 3.4.1.F.3 Important
 *   integer-valued distributions: The Poisson distribution. Addison Wesley.
 * * The Poisson process (and hence, the returned value) is bounded by 1000 * mean.
 *   This sampler is suitable for mean < 40. For large means, [LargeMeanPoissonSampler] should be used instead.
 *
 * Based on Commons RNG implementation.
 *
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/SmallMeanPoissonSampler.html].
 */
public class SmallMeanPoissonSampler(public val mean: Double) : Sampler<Int> {

    init {
        require(mean > 0) { "mean is not strictly positive: $mean" }
    }

    private val p0: Double = exp(-mean)

    private val limit: Int = if (p0 > 0) {
        ceil(1000 * mean)
    } else {
        throw IllegalArgumentException("No p(x=0) probability for mean: $mean")
    }.toInt()

    override fun sample(generator: RandomGenerator): BlockingIntChain = object : BlockingIntChain {
        override fun nextBlocking(): Int {
            var n = 0
            var r = 1.0

            while (n < limit) {
                r *= generator.nextDouble()
                if (r >= p0) n++ else break
            }

            return n
        }

        override fun nextBufferBlocking(size: Int): IntBuffer = IntBuffer(size) { nextBlocking() }

        override suspend fun fork(): BlockingIntChain = sample(generator.fork())
    }

    override fun toString(): String = "Small Mean Poisson deviate"
}


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
public class LargeMeanPoissonSampler(public val mean: Double) : Sampler<Int> {

    init {
        require(mean >= 1) { "mean is not >= 1: $mean" }
        // The algorithm is not valid if Math.floor(mean) is not an integer.
        require(mean <= MAX_MEAN) { "mean $mean > $MAX_MEAN" }
    }

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

    override fun sample(generator: RandomGenerator): BlockingIntChain = object : BlockingIntChain {
        override fun nextBlocking(): Int {
            val exponential = AhrensDieterExponentialSampler(1.0).sample(generator)
            val gaussian = ZigguratNormalizedGaussianSampler.sample(generator)

            val smallMeanPoissonSampler = if (mean - lambda < Double.MIN_VALUE) {
                null
            } else {
                KempSmallMeanPoissonSampler(mean - lambda).sample(generator)
            }

            val y2 = smallMeanPoissonSampler?.nextBlocking() ?: 0
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
                    val n = gaussian.nextBlocking()
                    x = n * sqrt(lambda + halfDelta) - 0.5
                    if (x > delta || x < -lambda) continue
                    y = if (x < 0) floor(x) else ceil(x)
                    val e = exponential.nextBlocking()
                    v = -e - 0.5 * n * n + c1
                } else {
                    // Step 3:
                    if (u > p1 + p2) {
                        y = lambda
                        break
                    }

                    x = delta + twolpd / delta * exponential.nextBlocking()
                    y = ceil(x)
                    v = -exponential.nextBlocking() - delta * (x + 1) / twolpd
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

            return min(y2 + y.toLong(), Int.MAX_VALUE.toLong()).toIntExact()
        }

        override fun nextBufferBlocking(size: Int): IntBuffer = IntBuffer(size) { nextBlocking() }

        override suspend fun fork(): BlockingIntChain = sample(generator.fork())
    }

    private fun getFactorialLog(n: Int): Double = factorialLog.value(n)
    override fun toString(): String = "Large Mean Poisson deviate"

    public companion object {
        private const val MAX_MEAN: Double = 0.5 * Int.MAX_VALUE
        private val NO_CACHE_FACTORIAL_LOG: InternalUtils.FactorialLog = InternalUtils.FactorialLog.create()
    }
}


