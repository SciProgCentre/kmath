package kscience.kmath.stat

import kscience.kmath.chains.BlockingIntChain
import kscience.kmath.chains.BlockingRealChain
import kscience.kmath.chains.Chain
import org.apache.commons.rng.UniformRandomProvider
import org.apache.commons.rng.sampling.distribution.*
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

public abstract class ContinuousSamplerDistribution : Distribution<Double> {
    private inner class ContinuousSamplerChain(val generator: RandomGenerator) : BlockingRealChain() {
        private val sampler = buildCMSampler(generator)

        override fun nextDouble(): Double = sampler.sample()
        override fun fork(): Chain<Double> = ContinuousSamplerChain(generator.fork())
    }

    protected abstract fun buildCMSampler(generator: RandomGenerator): ContinuousSampler

    public override fun sample(generator: RandomGenerator): BlockingRealChain = ContinuousSamplerChain(generator)
}

public abstract class DiscreteSamplerDistribution : Distribution<Int> {
    private inner class ContinuousSamplerChain(val generator: RandomGenerator) : BlockingIntChain() {
        private val sampler = buildSampler(generator)

        override fun nextInt(): Int = sampler.sample()
        override fun fork(): Chain<Int> = ContinuousSamplerChain(generator.fork())
    }

    protected abstract fun buildSampler(generator: RandomGenerator): DiscreteSampler

    public override fun sample(generator: RandomGenerator): BlockingIntChain = ContinuousSamplerChain(generator)
}

public enum class NormalSamplerMethod {
    BoxMuller,
    Marsaglia,
    Ziggurat
}

private fun normalSampler(method: NormalSamplerMethod, provider: UniformRandomProvider): NormalizedGaussianSampler =
    when (method) {
        NormalSamplerMethod.BoxMuller -> BoxMullerNormalizedGaussianSampler(provider)
        NormalSamplerMethod.Marsaglia -> MarsagliaNormalizedGaussianSampler(provider)
        NormalSamplerMethod.Ziggurat -> ZigguratNormalizedGaussianSampler(provider)
    }

public fun Distribution.Companion.normal(
    method: NormalSamplerMethod = NormalSamplerMethod.Ziggurat
): ContinuousSamplerDistribution = object : ContinuousSamplerDistribution() {
    override fun buildCMSampler(generator: RandomGenerator): ContinuousSampler {
        val provider = generator.asUniformRandomProvider()
        return normalSampler(method, provider)
    }

    override fun probability(arg: Double): Double = exp(-arg.pow(2) / 2) / sqrt(PI * 2)
}

/**
 * A univariate normal distribution with given [mean] and [sigma]. [method] defines commons-rng generation method
 */
public fun Distribution.Companion.normal(
    mean: Double,
    sigma: Double,
    method: NormalSamplerMethod = NormalSamplerMethod.Ziggurat
): ContinuousSamplerDistribution = object : ContinuousSamplerDistribution() {
    private val sigma2 = sigma.pow(2)
    private val norm = sigma * sqrt(PI * 2)

    override fun buildCMSampler(generator: RandomGenerator): ContinuousSampler {
        val provider = generator.asUniformRandomProvider()
        val normalizedSampler = normalSampler(method, provider)
        return GaussianSampler(normalizedSampler, mean, sigma)
    }

    override fun probability(arg: Double): Double = exp(-(arg - mean).pow(2) / 2 / sigma2) / norm
}

public fun Distribution.Companion.poisson(lambda: Double): DiscreteSamplerDistribution =
    object : DiscreteSamplerDistribution() {
        private val computedProb: MutableMap<Int, Double> = hashMapOf(0 to exp(-lambda))

        override fun buildSampler(generator: RandomGenerator): DiscreteSampler =
            PoissonSampler.of(generator.asUniformRandomProvider(), lambda)

        override fun probability(arg: Int): Double {
            require(arg >= 0) { "The argument must be >= 0" }

            return if (arg > 40)
                exp(-(arg - lambda).pow(2) / 2 / lambda) / sqrt(2 * PI * lambda)
            else
                computedProb.getOrPut(arg) { probability(arg - 1) * lambda / arg }
        }
    }
