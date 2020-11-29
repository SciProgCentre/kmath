package kscience.kmath.stat.samplers

import kscience.kmath.chains.Chain
import kscience.kmath.stat.RandomGenerator
import kscience.kmath.stat.Sampler
import kscience.kmath.stat.chain
import kotlin.math.*

/**
 * [Box-Muller algorithm](https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform) for sampling from a Gaussian
 * distribution.
 *
 * Based on Commons RNG implementation.
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/BoxMullerNormalizedGaussianSampler.html
 */
public class BoxMullerNormalizedGaussianSampler private constructor() : NormalizedGaussianSampler, Sampler<Double> {
    private var nextGaussian: Double = Double.NaN

    public override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
        val random: Double

        if (nextGaussian.isNaN()) {
            // Generate a pair of Gaussian numbers.
            val x = nextDouble()
            val y = nextDouble()
            val alpha = 2 * PI * x
            val r = sqrt(-2 * ln(y))
            // Return the first element of the generated pair.
            random = r * cos(alpha)
            // Keep second element of the pair for next invocation.
            nextGaussian = r * sin(alpha)
        } else {
            // Use the second element of the pair (generated at the
            // previous invocation).
            random = nextGaussian
            // Both elements of the pair have been used.
            nextGaussian = Double.NaN
        }

        random
    }

    public override fun toString(): String = "Box-Muller normalized Gaussian deviate"

    public companion object {
        public fun of(): BoxMullerNormalizedGaussianSampler = BoxMullerNormalizedGaussianSampler()
    }
}
