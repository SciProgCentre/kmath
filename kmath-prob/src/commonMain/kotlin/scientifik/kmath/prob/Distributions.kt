package scientifik.kmath.prob

import scientifik.commons.rng.UniformRandomProvider
import scientifik.commons.rng.sampling.distribution.*
import scientifik.kmath.chains.BlockingIntChain
import scientifik.kmath.chains.BlockingRealChain
import scientifik.kmath.chains.Chain

abstract class ContinuousSamplerDistribution : Distribution<Double> {

    private inner class ContinuousSamplerChain(val generator: RandomGenerator) : BlockingRealChain() {
        private val sampler = buildCMSampler(generator)

        override fun nextDouble(): Double = sampler.sample()

        override fun fork(): Chain<Double> = ContinuousSamplerChain(generator.fork())
    }

    protected abstract fun buildCMSampler(generator: RandomGenerator): ContinuousSampler

    override fun sample(generator: RandomGenerator): BlockingRealChain = ContinuousSamplerChain(generator)
}

abstract class DiscreteSamplerDistribution : Distribution<Int> {

    private inner class ContinuousSamplerChain(val generator: RandomGenerator) : BlockingIntChain() {
        private val sampler = buildSampler(generator)

        override fun nextInt(): Int = sampler.sample()

        override fun fork(): Chain<Int> = ContinuousSamplerChain(generator.fork())
    }

    protected abstract fun buildSampler(generator: RandomGenerator): DiscreteSampler

    override fun sample(generator: RandomGenerator): BlockingIntChain = ContinuousSamplerChain(generator)
}

enum class NormalSamplerMethod {
    BoxMuller,
    Marsaglia,
    Ziggurat
}

fun normalSampler(method: NormalSamplerMethod, provider: UniformRandomProvider): NormalizedGaussianSampler =
    when (method) {
        NormalSamplerMethod.BoxMuller -> BoxMullerNormalizedGaussianSampler(
            provider
        )
        NormalSamplerMethod.Marsaglia -> MarsagliaNormalizedGaussianSampler(provider)
        NormalSamplerMethod.Ziggurat -> ZigguratNormalizedGaussianSampler(provider)
    }

