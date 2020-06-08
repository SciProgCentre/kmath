package scientifik.kmath.prob

import scientifik.kmath.commons.rng.sampling.distribution.ContinuousSampler
import scientifik.kmath.commons.rng.sampling.distribution.DiscreteSampler
import scientifik.kmath.commons.rng.sampling.distribution.GaussianSampler
import scientifik.kmath.commons.rng.sampling.distribution.PoissonSampler
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

fun Distribution.Companion.normal(
    method: NormalSamplerMethod = NormalSamplerMethod.Ziggurat
): Distribution<Double> = object : ContinuousSamplerDistribution() {
    override fun buildCMSampler(generator: RandomGenerator): ContinuousSampler {
        val provider = generator.asUniformRandomProvider()
        return normalSampler(method, provider)
    }

    override fun probability(arg: Double): Double {
        return exp(-arg.pow(2) / 2) / sqrt(PI * 2)
    }
}

fun Distribution.Companion.normal(
    mean: Double,
    sigma: Double,
    method: NormalSamplerMethod = NormalSamplerMethod.Ziggurat
): ContinuousSamplerDistribution = object : ContinuousSamplerDistribution() {
    private val sigma2 = sigma.pow(2)
    private val norm = sigma * sqrt(PI * 2)

    override fun buildCMSampler(generator: RandomGenerator): ContinuousSampler {
        val provider = generator.asUniformRandomProvider()
        val normalizedSampler = normalSampler(method, provider)
        return GaussianSampler(
            normalizedSampler,
            mean,
            sigma
        )
    }

    override fun probability(arg: Double): Double {
        return exp(-(arg - mean).pow(2) / 2 / sigma2) / norm
    }
}

fun Distribution.Companion.poisson(
    lambda: Double
): DiscreteSamplerDistribution = object : DiscreteSamplerDistribution() {

    override fun buildSampler(generator: RandomGenerator): DiscreteSampler {
        return PoissonSampler.of(generator.asUniformRandomProvider(), lambda)
    }

    private val computedProb: HashMap<Int, Double> = hashMapOf(0 to exp(-lambda))

    override fun probability(arg: Int): Double {
        require(arg >= 0) { "The argument must be >= 0" }

        return if (arg > 40)
            exp(-(arg - lambda).pow(2) / 2 / lambda) / sqrt(2 * PI * lambda)
        else
            computedProb.getOrPut(arg) { probability(arg - 1) * lambda / arg }
    }
}
