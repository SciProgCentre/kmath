package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.chain
import kotlin.math.*

/**
 * Based on commons-rng implementation.
 *
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/BoxMullerNormalizedGaussianSampler.html
 */
class BoxMullerNormalizedGaussianSampler private constructor() : NormalizedGaussianSampler, Sampler<Double> {
    private var nextGaussian: Double = Double.NaN

    override fun sample(generator: RandomGenerator): Chain<Double> = generator.chain {
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

    override fun toString(): String = "Box-Muller normalized Gaussian deviate"

    companion object {
        fun of(): BoxMullerNormalizedGaussianSampler = BoxMullerNormalizedGaussianSampler()
    }
}
