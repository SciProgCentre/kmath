package space.kscience.kmath.distributions

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.internal.InternalErf
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.samplers.NormalizedGaussianSampler
import space.kscience.kmath.samplers.ZigguratNormalizedGaussianSampler
import space.kscience.kmath.stat.RandomGenerator
import kotlin.math.*

/**
 * Implements [UnivariateDistribution] for the normal (gaussian) distribution.
 */
public class NormalDistribution(public val sampler: GaussianSampler) : UnivariateDistribution<Double> {
    public constructor(
        mean: Double,
        standardDeviation: Double,
        normalized: NormalizedGaussianSampler = ZigguratNormalizedGaussianSampler,
    ) : this(GaussianSampler(mean, standardDeviation, normalized))

    public override fun probability(arg: Double): Double {
        val x1 = (arg - sampler.mean) / sampler.standardDeviation
        return exp(-0.5 * x1 * x1 - (ln(sampler.standardDeviation) + 0.5 * ln(2 * PI)))
    }

    public override fun sample(generator: RandomGenerator): Chain<Double> = sampler.sample(generator)

    public override fun cumulative(arg: Double): Double {
        val dev = arg - sampler.mean

        return when {
            abs(dev) > 40 * sampler.standardDeviation -> if (dev < 0) 0.0 else 1.0
            else -> 0.5 * InternalErf.erfc(-dev / (sampler.standardDeviation * SQRT2))
        }
    }

    private companion object {
        private val SQRT2 = sqrt(2.0)
    }
}
