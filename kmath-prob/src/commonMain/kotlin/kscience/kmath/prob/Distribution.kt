package kscience.kmath.prob

import kotlinx.coroutines.flow.first
import kscience.kmath.chains.Chain
import kscience.kmath.chains.collect
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.BufferFactory

public fun interface Sampler<T : Any> {
    public fun sample(generator: RandomGenerator): Chain<T>
}

/**
 * A distribution of typed objects
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

/**
 * Sample a bunch of values
 */
public fun <T : Any> Sampler<T>.sampleBuffer(
    generator: RandomGenerator,
    size: Int,
    bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
): Chain<Buffer<T>> {
    require(size > 1)
    //creating temporary storage once
    val tmp = ArrayList<T>(size)

    return sample(generator).collect { chain ->
        //clear list from previous run
        tmp.clear()
        //Fill list
        repeat(size) { tmp.add(chain.next()) }
        //return new buffer with elements from tmp
        bufferFactory(size) { tmp[it] }
    }
}

public suspend fun <T : Any> Sampler<T>.next(generator: RandomGenerator): T = sample(generator).first()

/**
 * Generate a bunch of samples from real distributions
 */
public fun Sampler<Double>.sampleBuffer(generator: RandomGenerator, size: Int): Chain<Buffer<Double>> =
    sampleBuffer(generator, size, Buffer.Companion::real)
