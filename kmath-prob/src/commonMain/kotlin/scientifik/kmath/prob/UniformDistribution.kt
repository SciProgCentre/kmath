package scientifik.kmath.prob

import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.SimpleChain

class UniformDistribution(val range: ClosedFloatingPointRange<Double>) : UnivariateDistribution<Double> {

    private val length = range.endInclusive - range.start

    override fun probability(arg: Double): Double {
        return if (arg in range) {
            return 1.0 / length
        } else {
            0.0
        }
    }

    override fun sample(generator: RandomGenerator): Chain<Double> {
        return SimpleChain {
            range.start + generator.nextDouble() * length
        }
    }

    override fun cumulative(arg: Double): Double {
        return when {
            arg < range.start -> 0.0
            arg >= range.endInclusive -> 1.0
            else -> (arg - range.start) / length
        }
    }
}

fun Distribution.Companion.uniform(range: ClosedFloatingPointRange<Double>): UniformDistribution =
    UniformDistribution(range)