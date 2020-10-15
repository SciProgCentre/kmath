package kscience.kmath.prob.distributions

import kscience.kmath.chains.Chain
import kscience.kmath.prob.RandomGenerator
import kscience.kmath.prob.UnivariateDistribution
import kscience.kmath.prob.internal.InternalErf
import kscience.kmath.prob.samplers.GaussianSampler
import kotlin.math.*

public inline class NormalDistribution(public val sampler: GaussianSampler) : UnivariateDistribution<Double> {
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
