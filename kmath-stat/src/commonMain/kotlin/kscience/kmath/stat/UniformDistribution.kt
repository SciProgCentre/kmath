package kscience.kmath.stat

import kscience.kmath.chains.Chain
import kscience.kmath.chains.SimpleChain

public class UniformDistribution(public val range: ClosedFloatingPointRange<Double>) : UnivariateDistribution<Double> {
    private val length: Double = range.endInclusive - range.start

    override fun probability(arg: Double): Double = if (arg in range) 1.0 / length else 0.0

    override fun sample(generator: RandomGenerator): Chain<Double> =
        SimpleChain { range.start + generator.nextDouble() * length }

    override fun cumulative(arg: Double): Double = when {
        arg < range.start -> 0.0
        arg >= range.endInclusive -> 1.0
        else -> (arg - range.start) / length
    }
}

public fun Distribution.Companion.uniform(range: ClosedFloatingPointRange<Double>): UniformDistribution =
    UniformDistribution(range)
