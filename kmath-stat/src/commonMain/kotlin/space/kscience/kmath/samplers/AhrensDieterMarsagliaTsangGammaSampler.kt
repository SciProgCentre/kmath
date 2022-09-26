/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.random.chain
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.stat.next
import kotlin.math.*

/**
 * Sampling from the [gamma distribution](http://mathworld.wolfram.com/GammaDistribution.html).
 * * For 0 < alpha < 1:
 *    Ahrens, J. H. and Dieter, U., Computer methods for sampling from gamma, beta, Poisson and binomial distributions, Computing, 12, 223-246, 1974.
 * * For alpha >= 1:
 *    Marsaglia and Tsang, A Simple Method for Generating Gamma Variables. ACM Transactions on Mathematical Software, Volume 26 Issue 3, September, 2000.
 *
 * Based on Commons RNG implementation.
 *
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/AhrensDieterMarsagliaTsangGammaSampler.html].
 */
public class AhrensDieterMarsagliaTsangGammaSampler private constructor(
    alpha: Double,
    theta: Double,
) : Sampler<Double> {
    private val delegate: BaseGammaSampler =
        if (alpha < 1) AhrensDieterGammaSampler(alpha, theta) else MarsagliaTsangGammaSampler(alpha, theta)

    private abstract class BaseGammaSampler internal constructor(
        protected val alpha: Double,
        protected val theta: Double,
    ) : Sampler<Double> {
        init {
            require(alpha > 0) { "alpha is not strictly positive: $alpha" }
            require(theta > 0) { "theta is not strictly positive: $theta" }
        }

        override fun toString(): String = "Ahrens-Dieter-Marsaglia-Tsang Gamma deviate"
    }

    private class AhrensDieterGammaSampler(alpha: Double, theta: Double) :
        BaseGammaSampler(alpha, theta) {
        private val oneOverAlpha: Double = 1.0 / alpha
        private val bGSOptim: Double = 1.0 + alpha / E

        override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
            var x: Double

            // [1]: p. 228, Algorithm GS.
            while (true) {
                // Step 1:
                val u = generator.nextDouble()
                val p = bGSOptim * u

                if (p <= 1) {
                    // Step 2:
                    x = p.pow(oneOverAlpha)
                    val u2 = generator.nextDouble()

                    if (u2 > exp(-x)) // Reject.
                        continue

                    break
                }

                // Step 3:
                x = -ln((bGSOptim - p) * oneOverAlpha)
                val u2: Double = generator.nextDouble()
                if (u2 <= x.pow(alpha - 1.0)) break
                // Reject and continue.
            }

            x * theta
        }
    }

    private class MarsagliaTsangGammaSampler(alpha: Double, theta: Double) :
        BaseGammaSampler(alpha, theta) {
        private val dOptim: Double
        private val cOptim: Double
        private val gaussian: NormalizedGaussianSampler

        init {
            gaussian = ZigguratNormalizedGaussianSampler
            dOptim = alpha - ONE_THIRD
            cOptim = ONE_THIRD / sqrt(dOptim)
        }

        override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
            var v: Double

            while (true) {
                val x = gaussian.next(generator)
                val oPcTx = 1 + cOptim * x
                v = oPcTx * oPcTx * oPcTx
                if (v <= 0) continue
                val x2 = x * x
                val u = generator.nextDouble()
                // Squeeze.
                if (u < 1 - 0.0331 * x2 * x2) break
                if (ln(u) < 0.5 * x2 + dOptim * (1 - v + ln(v))) break
            }

            theta * dOptim * v
        }

        companion object {
            private const val ONE_THIRD = 1.0 / 3.0
        }
    }

    override fun sample(generator: RandomGenerator): Chain<Double> = delegate.sample(generator)
    override fun toString(): String = delegate.toString()

    public companion object {
        public fun of(
            alpha: Double,
            theta: Double,
        ): Sampler<Double> = AhrensDieterMarsagliaTsangGammaSampler(alpha, theta)
    }
}