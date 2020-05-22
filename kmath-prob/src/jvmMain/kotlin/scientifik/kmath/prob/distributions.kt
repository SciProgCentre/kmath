package scientifik.kmath.prob

import org.apache.commons.rng.UniformRandomProvider
import org.apache.commons.rng.sampling.distribution.*
import scientifik.kmath.chains.Chain
import java.util.*
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

abstract class ContinuousSamplerDistribution : Distribution<Double> {

    private inner class ContinuousSamplerChain(val generator: RandomGenerator) : Chain<Double> {
        private val sampler = buildSampler(generator)

        override suspend fun next(): Double = sampler.sample()

        override fun fork(): Chain<Double> = ContinuousSamplerChain(generator.fork())
    }

    protected abstract fun buildSampler(generator: RandomGenerator): ContinuousSampler

    override fun sample(generator: RandomGenerator): Chain<Double> = ContinuousSamplerChain(generator)
}

abstract class DiscreteSamplerDistribution : Distribution<Int> {

    private inner class ContinuousSamplerChain(val generator: RandomGenerator) : Chain<Int> {
        private val sampler = buildSampler(generator)

        override suspend fun next(): Int = sampler.sample()

        override fun fork(): Chain<Int> = ContinuousSamplerChain(generator.fork())
    }

    protected abstract fun buildSampler(generator: RandomGenerator): DiscreteSampler

    override fun sample(generator: RandomGenerator): Chain<Int> = ContinuousSamplerChain(generator)
}

enum class NormalSamplerMethod {
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

fun Distribution.Companion.normal(
    method: NormalSamplerMethod = NormalSamplerMethod.Ziggurat
): Distribution<Double> = object : ContinuousSamplerDistribution() {
    override fun buildSampler(generator: RandomGenerator): ContinuousSampler {
        val provider: UniformRandomProvider = generator.asUniformRandomProvider()
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
): Distribution<Double> = object : ContinuousSamplerDistribution() {
    private val sigma2 = sigma.pow(2)
    private val norm = sigma * sqrt(PI * 2)

    override fun buildSampler(generator: RandomGenerator): ContinuousSampler {
        val provider: UniformRandomProvider = generator.asUniformRandomProvider()
        val normalizedSampler = normalSampler(method, provider)
        return GaussianSampler(normalizedSampler, mean, sigma)
    }

    override fun probability(arg: Double): Double {
        return exp(-(arg - mean).pow(2) / 2 / sigma2) / norm
    }
}

fun Distribution.Companion.poisson(
    lambda: Double
): Distribution<Int> = object : DiscreteSamplerDistribution() {

    override fun buildSampler(generator: RandomGenerator): DiscreteSampler {
        return PoissonSampler.of(generator.asUniformRandomProvider(), lambda)
    }

    private val computedProb: HashMap<Int, Double> = hashMapOf(0 to exp(-lambda))

    override fun probability(arg: Int): Double {
        require(arg >= 0) { "The argument must be >= 0" }
        return if (arg > 40) {
            exp(-(arg - lambda).pow(2) / 2 / lambda) / sqrt(2 * PI * lambda)
        } else {
            computedProb.getOrPut(arg) {
                probability(arg - 1) * lambda / arg
            }
        }
    }
}
