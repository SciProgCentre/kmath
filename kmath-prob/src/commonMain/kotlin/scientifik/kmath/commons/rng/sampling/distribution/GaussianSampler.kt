package scientifik.kmath.commons.rng.sampling.distribution

import scientifik.kmath.commons.rng.UniformRandomProvider

class GaussianSampler :
    SharedStateContinuousSampler {
    private val mean: Double
    private val standardDeviation: Double
    private val normalized: NormalizedGaussianSampler

    constructor(
        normalized: NormalizedGaussianSampler,
        mean: Double,
        standardDeviation: Double
    ) {
        require(standardDeviation > 0) { "standard deviation is not strictly positive: $standardDeviation" }
        this.normalized = normalized
        this.mean = mean
        this.standardDeviation = standardDeviation
    }

    private constructor(
        rng: UniformRandomProvider,
        source: GaussianSampler
    ) {
        mean = source.mean
        standardDeviation = source.standardDeviation
        normalized =
            InternalUtils.newNormalizedGaussianSampler(
                source.normalized,
                rng
            )
    }

    override fun sample(): Double = standardDeviation * normalized.sample() + mean

    override fun toString(): String = "Gaussian deviate [$normalized]"

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateContinuousSampler {
        return GaussianSampler(rng, this)
    }

    companion object {
        fun of(
            normalized: NormalizedGaussianSampler,
            mean: Double,
            standardDeviation: Double
        ): SharedStateContinuousSampler =
            GaussianSampler(
                normalized,
                mean,
                standardDeviation
            )
    }
}
