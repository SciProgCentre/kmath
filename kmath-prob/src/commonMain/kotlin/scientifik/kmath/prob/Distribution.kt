package scientifik.kmath.prob

import kotlinx.coroutines.flow.first
import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.collect
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory

interface Sampler<T : Any> {
    fun sample(generator: RandomGenerator): Chain<T>
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
     * The chain is not guaranteed to be stateless, but different sample chains should be independent.
     */
    override fun sample(generator: RandomGenerator): Chain<T>

    /**
     * An empty companion. Distribution factories should be written as its extensions
     */
    companion object
}

interface UnivariateDistribution<T : Comparable<T>> : Distribution<T> {
    /**
     * Cumulative distribution for ordered parameter (CDF)
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
fun <T : Any> Sampler<T>.sampleBuffer(
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
        repeat(size){
            tmp.add(chain.next())
        }
        //return new buffer with elements from tmp
        bufferFactory(size) { tmp[it] }
    }
}

suspend fun <T : Any> Sampler<T>.next(generator: RandomGenerator) = sample(generator).first()

/**
 * Generate a bunch of samples from real distributions
 */
fun Sampler<Double>.sampleBuffer(generator: RandomGenerator, size: Int) =
    sampleBuffer(generator, size, Buffer.Companion::real)