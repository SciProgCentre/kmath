package scientifik.kmath.prob

import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.map
import kotlin.jvm.JvmName

interface Sampler<T : Any> {
    fun sample(generator: RandomGenerator): RandomChain<T>
}

/**
 * A distribution of typed objects
 */
interface Distribution<T : Any> : Sampler<T> {
    /**
     * A probability value for given argument [arg].
     * For continuous distributions returns PDF
     */
    fun probability(arg: T): Double

    /**
     * Create a chain of samples from this distribution.
     * The chain is not guaranteed to be stateless.
     */
    override fun sample(generator: RandomGenerator): RandomChain<T>

    /**
     * An empty companion. Distribution factories should be written as its extensions
     */
    companion object
}

interface UnivariateDistribution<T : Comparable<T>> : Distribution<T> {
    /**
     * Cumulative distribution for ordered parameter
     */
    fun cumulative(arg: T): Double
}

/**
 * Compute probability integral in an interval
 */
fun <T : Comparable<T>> UnivariateDistribution<T>.integral(from: T, to: T): Double {
    require(to > from)
    return cumulative(to) - cumulative(from)
}


/**
 * Sample a bunch of values
 */
fun <T : Any> Sampler<T>.sampleBunch(generator: RandomGenerator, size: Int): Chain<List<T>> {
    require(size > 1)
    return sample(generator).map{chain ->
        List(size){chain.next()}
    }
}

/**
 * Generate a bunch of samples from real distributions
 */
@JvmName("realSampleBunch")
fun Sampler<Double>.sampleBunch(generator: RandomGenerator, size: Int): Chain<DoubleArray> {
    require(size > 1)
    return sample(generator).map{chain ->
        DoubleArray(size){chain.next()}
    }
}