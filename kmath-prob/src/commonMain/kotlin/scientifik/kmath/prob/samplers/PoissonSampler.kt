package scientifik.kmath.prob.samplers

import scientifik.kmath.chains.Chain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler


class PoissonSampler private constructor(
    mean: Double
) : Sampler<Int> {
    private val poissonSamplerDelegate: Sampler<Int> = of(mean)
    override fun sample(generator: RandomGenerator): Chain<Int> = poissonSamplerDelegate.sample(generator)
    override fun toString(): String = poissonSamplerDelegate.toString()

    companion object {
        private const val PIVOT = 40.0

        fun of(mean: Double) =// Each sampler should check the input arguments.
            if (mean < PIVOT) SmallMeanPoissonSampler.of(mean) else LargeMeanPoissonSampler.of(mean)
    }
}
