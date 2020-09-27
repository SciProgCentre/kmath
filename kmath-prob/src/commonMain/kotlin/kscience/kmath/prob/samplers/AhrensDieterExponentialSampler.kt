package kscience.kmath.prob.samplers

import kscience.kmath.chains.Chain
import kscience.kmath.prob.RandomGenerator
import kscience.kmath.prob.Sampler
import kscience.kmath.prob.chain
import kotlin.math.ln
import kotlin.math.pow

/**
 * Sampling from an [exponential distribution](http://mathworld.wolfram.com/ExponentialDistribution.html).
 *
 * Based on Commons RNG implementation.
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/AhrensDieterExponentialSampler.html
 */
public class AhrensDieterExponentialSampler private constructor(public val mean: Double) : Sampler<Double> {
    public override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
        // Step 1:
        var a = 0.0
        var u = nextDouble()

        // Step 2 and 3:
        while (u < 0.5) {
            a += EXPONENTIAL_SA_QI[0]
            u *= 2.0
        }

        // Step 4 (now u >= 0.5):
        u += u - 1
        // Step 5:
        if (u <= EXPONENTIAL_SA_QI[0]) return@chain mean * (a + u)
        // Step 6:
        var i = 0 // Should be 1, be we iterate before it in while using 0.
        var u2 = nextDouble()
        var umin = u2

        // Step 7 and 8:
        do {
            ++i
            u2 = nextDouble()
            if (u2 < umin) umin = u2
            // Step 8:
        } while (u > EXPONENTIAL_SA_QI[i]) // Ensured to exit since EXPONENTIAL_SA_QI[MAX] = 1.

        mean * (a + umin * EXPONENTIAL_SA_QI[0])
    }

    override fun toString(): String = "Ahrens-Dieter Exponential deviate"

    public companion object {
        private val EXPONENTIAL_SA_QI by lazy { DoubleArray(16) }

        init {
            /**
             * Filling EXPONENTIAL_SA_QI table.
             * Note that we don't want qi = 0 in the table.
             */
            val ln2 = ln(2.0)
            var qi = 0.0

            EXPONENTIAL_SA_QI.indices.forEach { i ->
                qi += ln2.pow(i + 1.0) / InternalUtils.factorial(i + 1)
                EXPONENTIAL_SA_QI[i] = qi
            }
        }

        public fun of(mean: Double): AhrensDieterExponentialSampler {
            require(mean > 0) { "mean is not strictly positive: $mean" }
            return AhrensDieterExponentialSampler(mean)
        }
    }
}
