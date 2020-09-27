package kscience.kmath.prob.samplers

import kscience.kmath.chains.Chain
import kscience.kmath.chains.map
import kscience.kmath.prob.RandomGenerator
import kscience.kmath.prob.Sampler

/**
 * Sampling from a Gaussian distribution with given mean and standard deviation.
 *
 * Based on Commons RNG implementation.
 * See https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/GaussianSampler.html
 */
public class GaussianSampler private constructor(
    private val mean: Double,
    private val standardDeviation: Double,
    private val normalized: NormalizedGaussianSampler
) : Sampler<Double> {
    public override fun sample(generator: RandomGenerator): Chain<Double> = normalized
        .sample(generator)
        .map { standardDeviation * it + mean }

    override fun toString(): String = "Gaussian deviate [$normalized]"

    public companion object {
        public fun of(
            mean: Double,
            standardDeviation: Double,
            normalized: NormalizedGaussianSampler = ZigguratNormalizedGaussianSampler.of()
        ): GaussianSampler {
            require(standardDeviation > 0.0) { "standard deviation is not strictly positive: $standardDeviation" }

            return GaussianSampler(
                mean,
                standardDeviation,
                normalized
            )
        }
    }
}
