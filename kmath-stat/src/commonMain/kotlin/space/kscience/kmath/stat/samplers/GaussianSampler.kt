package space.kscience.kmath.stat.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.map
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.Sampler

/**
 * Sampling from a Gaussian distribution with given mean and standard deviation.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/GaussianSampler.html].
 *
 * @property mean the mean of the distribution.
 * @property standardDeviation the variance of the distribution.
 */
public class GaussianSampler private constructor(
    public val mean: Double,
    public val standardDeviation: Double,
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
