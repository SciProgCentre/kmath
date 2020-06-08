package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.map
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler

class GaussianSampler private constructor(
    private val mean: Double,
    private val standardDeviation: Double,
    private val normalized: NormalizedGaussianSampler
) : Sampler<Double> {
    override fun sample(generator: RandomGenerator): Chain<Double> =
        normalized.sample(generator).map { standardDeviation * it + mean }

    override fun toString(): String = "Gaussian deviate [$normalized]"

    companion object {
        fun of(
            mean: Double,
            standardDeviation: Double,
            normalized: NormalizedGaussianSampler
        ): GaussianSampler {
            require(standardDeviation > 0) { "standard deviation is not strictly positive: $standardDeviation" }

            return GaussianSampler(
                mean,
                standardDeviation,
                normalized
            )
        }
    }
}
