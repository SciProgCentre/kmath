package space.kscience.kmath.distributions

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.Sampler

/**
 * A distribution of typed objects.
 */
public interface Distribution<T : Any> : Sampler<T> {
    /**
     * A probability value for given argument [arg].
     * For continuous distributions returns PDF
     */
    public fun probability(arg: T): Double

    public override fun sample(generator: RandomGenerator): Chain<T>

    /**
     * An empty companion. Distribution factories should be written as its extensions
     */
    public companion object
}

public interface UnivariateDistribution<T : Comparable<T>> : Distribution<T> {
    /**
     * Cumulative distribution for ordered parameter (CDF)
     */
    public fun cumulative(arg: T): Double
}

/**
 * Compute probability integral in an interval
 */
public fun <T : Comparable<T>> UnivariateDistribution<T>.integral(from: T, to: T): Double {
    require(to > from)
    return cumulative(to) - cumulative(from)
}
