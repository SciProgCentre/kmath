package scientifik.commons.rng.sampling.distribution

import scientifik.commons.rng.UniformRandomProvider

class PoissonSampler(
    rng: UniformRandomProvider,
    mean: Double
) : SamplerBase(null), SharedStateDiscreteSampler {
    private val poissonSamplerDelegate: SharedStateDiscreteSampler

    override fun sample(): Int = poissonSamplerDelegate.sample()
    override fun toString(): String = poissonSamplerDelegate.toString()

    override fun withUniformRandomProvider(rng: UniformRandomProvider): SharedStateDiscreteSampler? =
// Direct return of the optimised sampler
        poissonSamplerDelegate.withUniformRandomProvider(rng)

    companion object {
        const val PIVOT = 40.0

        fun of(
            rng: UniformRandomProvider,
            mean: Double
        ): SharedStateDiscreteSampler =// Each sampler should check the input arguments.
            if (mean < PIVOT) SmallMeanPoissonSampler.of(rng, mean) else LargeMeanPoissonSampler.of(rng, mean)
    }

    init {
        // Delegate all work to specialised samplers.
        poissonSamplerDelegate = of(rng, mean)
    }
}