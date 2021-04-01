package space.kscience.kmath.stat

import kotlinx.coroutines.flow.first
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.collect
import space.kscience.kmath.structures.*
import kotlin.jvm.JvmName

/**
 * Sampler that generates chains of values of type [T] in a chain of type [C].
 */
public fun interface Sampler<out T : Any> {
    /**
     * Generates a chain of samples.
     *
     * @param generator the randomness provider.
     * @return the new chain.
     */
    public fun sample(generator: RandomGenerator): Chain<T>
}

/**
 * Sample a bunch of values
 */
public fun <T : Any> Sampler<T>.sampleBuffer(
    generator: RandomGenerator,
    size: Int,
    bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
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

/**
 * Samples one value from this [Sampler].
 */
public suspend fun <T : Any> Sampler<T>.next(generator: RandomGenerator): T = sample(generator).first()

/**
 * Generates [size] real samples and chunks them into some buffers.
 */
@JvmName("sampleRealBuffer")
public fun Sampler<Double>.sampleBuffer(generator: RandomGenerator, size: Int): Chain<Buffer<Double>> =
    sampleBuffer(generator, size, ::DoubleBuffer)

/**
 * Generates [size] integer samples and chunks them into some buffers.
 */
@JvmName("sampleIntBuffer")
public fun Sampler<Int>.sampleBuffer(generator: RandomGenerator, size: Int): Chain<Buffer<Int>> =
    sampleBuffer(generator, size, ::IntBuffer)
