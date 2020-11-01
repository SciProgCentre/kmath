package kscience.kmath.prob.samplers

import kscience.kmath.chains.Chain
import kscience.kmath.prob.RandomGenerator
import kscience.kmath.prob.Sampler
import kscience.kmath.prob.chain
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * [Marsaglia polar method](https://en.wikipedia.org/wiki/Marsaglia_polar_method) for sampling from a Gaussian
 * distribution with mean 0 and standard deviation 1. This is a variation of the algorithm implemented in
 * [BoxMullerNormalizedGaussianSampler].
 *
 * Based on Commons RNG implementation.
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/MarsagliaNormalizedGaussianSampler.html
 */
public class MarsagliaNormalizedGaussianSampler private constructor() : NormalizedGaussianSampler, Sampler<Double> {
    private var nextGaussian = Double.NaN

    public override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
        if (nextGaussian.isNaN()) {
            val alpha: Double
            var x: Double

            // Rejection scheme for selecting a pair that lies within the unit circle.
            while (true) {
                // Generate a pair of numbers within [-1 , 1).
                x = 2.0 * generator.nextDouble() - 1.0
                val y = 2.0 * generator.nextDouble() - 1.0
                val r2 = x * x + y * y

                if (r2 < 1 && r2 > 0) {
                    // Pair (x, y) is within unit circle.
                    alpha = sqrt(-2 * ln(r2) / r2)
                    // Keep second element of the pair for next invocation.
                    nextGaussian = alpha * y
                    // Return the first element of the generated pair.
                    break
                }
                // Pair is not within the unit circle: Generate another one.
            }

            // Return the first element of the generated pair.
            alpha * x
        } else {
            // Use the second element of the pair (generated at the
            // previous invocation).
            val r = nextGaussian
            // Both elements of the pair have been used.
            nextGaussian = Double.NaN
            r
        }
    }

    public override fun toString(): String = "Box-Muller (with rejection) normalized Gaussian deviate"

    public companion object {
        public fun of(): MarsagliaNormalizedGaussianSampler = MarsagliaNormalizedGaussianSampler()
    }
}
