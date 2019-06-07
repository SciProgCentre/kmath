package scientifik.kmath.prob

import org.apache.commons.rng.sampling.distribution.*
import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.SimpleChain
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sqrt

class NormalDistribution(val mean: Double, val sigma: Double) : UnivariateDistribution<Double> {
    enum class Sampler {
        BoxMuller,
        Marsaglia,
        Ziggurat
    }

    override fun probability(arg: Double): Double {
        val d = (arg - mean) / sigma
        return 1.0 / sqrt(2.0 * PI * sigma) * exp(-d * d / 2)
    }

    override fun cumulative(arg: Double): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sample(generator: RandomGenerator, sampler: Sampler): Chain<Double> {
        val normalized = when (sampler) {
            Sampler.BoxMuller -> BoxMullerNormalizedGaussianSampler(generator.asProvider())
            Sampler.Marsaglia -> MarsagliaNormalizedGaussianSampler(generator.asProvider())
            Sampler.Ziggurat -> ZigguratNormalizedGaussianSampler(generator.asProvider())
        }
        val gauss = GaussianSampler(normalized, mean, sigma)
        //TODO add generator to chain state to allow stateful forks
        return SimpleChain { gauss.sample() }
    }

    override fun sample(generator: RandomGenerator): Chain<Double> = sample(generator, Sampler.BoxMuller)
}

class PoissonDistribution(val mean: Double): UnivariateDistribution<Int>{
    override fun probability(arg: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cumulative(arg: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sample(generator: RandomGenerator): Chain<Int> {
        val sampler = PoissonSampler(generator.asProvider(), mean)
        return SimpleChain{sampler.sample()}
    }
}