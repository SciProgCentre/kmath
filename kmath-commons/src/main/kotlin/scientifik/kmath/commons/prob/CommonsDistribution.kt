package scientifik.kmath.commons.prob

import org.apache.commons.math3.distribution.*
import scientifik.kmath.prob.Distribution
import scientifik.kmath.prob.RandomChain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.UnivariateDistribution
import org.apache.commons.math3.random.RandomGenerator as CMRandom

class CMRealDistributionWrapper(val builder: (CMRandom?) -> RealDistribution) : UnivariateDistribution<Double> {

    private val defaultDistribution by lazy { builder(null) }

    override fun probability(arg: Double): Double = defaultDistribution.probability(arg)

    override fun cumulative(arg: Double): Double = defaultDistribution.cumulativeProbability(arg)

    override fun sample(generator: RandomGenerator): RandomChain<Double> {
        val distribution = builder(generator.asCMGenerator())
        return RandomChain(generator) { distribution.sample() }
    }
}

class CMIntDistributionWrapper(val builder: (CMRandom?) -> IntegerDistribution) : UnivariateDistribution<Int> {

    private val defaultDistribution by lazy { builder(null) }

    override fun probability(arg: Int): Double = defaultDistribution.probability(arg)

    override fun cumulative(arg: Int): Double = defaultDistribution.cumulativeProbability(arg)

    override fun sample(generator: RandomGenerator): RandomChain<Int> {
        val distribution = builder(generator.asCMGenerator())
        return RandomChain(generator) { distribution.sample() }
    }
}


fun Distribution.Companion.normal(mean: Double = 0.0, sigma: Double = 1.0): UnivariateDistribution<Double> =
    CMRealDistributionWrapper { generator -> NormalDistribution(generator, mean, sigma) }

fun Distribution.Companion.poisson(mean: Double): UnivariateDistribution<Int> = CMIntDistributionWrapper { generator ->
    PoissonDistribution(
        generator,
        mean,
        PoissonDistribution.DEFAULT_EPSILON,
        PoissonDistribution.DEFAULT_MAX_ITERATIONS
    )
}

fun Distribution.Companion.binomial(trials: Int, p: Double): UnivariateDistribution<Int> =
    CMIntDistributionWrapper { generator ->
        BinomialDistribution(generator, trials, p)
    }

fun Distribution.Companion.student(degreesOfFreedom: Double): UnivariateDistribution<Double> =
    CMRealDistributionWrapper { generator ->
        TDistribution(generator, degreesOfFreedom, TDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY)
    }

fun Distribution.Companion.chi2(degreesOfFreedom: Double): UnivariateDistribution<Double> =
    CMRealDistributionWrapper { generator ->
        ChiSquaredDistribution(generator, degreesOfFreedom)
    }

fun Distribution.Companion.fisher(
    numeratorDegreesOfFreedom: Double,
    denominatorDegreesOfFreedom: Double
): UnivariateDistribution<Double> =
    CMRealDistributionWrapper { generator ->
        FDistribution(generator, numeratorDegreesOfFreedom, denominatorDegreesOfFreedom)
    }

fun Distribution.Companion.exponential(mean: Double): UnivariateDistribution<Double> =
    CMRealDistributionWrapper { generator ->
        ExponentialDistribution(generator, mean)
    }

fun Distribution.Companion.uniform(a: Double, b: Double): UnivariateDistribution<Double> =
    CMRealDistributionWrapper { generator ->
        UniformRealDistribution(generator, a, b)
    }