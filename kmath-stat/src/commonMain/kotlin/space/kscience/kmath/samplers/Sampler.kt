/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.flow.first
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.combine
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.IntBuffer
import kotlin.jvm.JvmName

/**
 * Sampler that generates chains of values of type [T].
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
@OptIn(UnstableKMathAPI::class)
public fun <T : Any> Sampler<T>.sampleBuffer(
    generator: RandomGenerator,
    size: Int,
    bufferFactory: BufferFactory<T> = BufferFactory.boxing(),
): Chain<Buffer<T>> {
    require(size > 1)
    //creating temporary storage once
    val tmp = ArrayList<T>(size)

    return sample(generator).combine { chain ->
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


/**
 * Samples a [Buffer] of values from this [Sampler].
 */
public suspend fun Sampler<Double>.nextBuffer(generator: RandomGenerator, size: Int): Buffer<Double> =
    sampleBuffer(generator, size).first()

//TODO add `context(RandomGenerator) Sampler.nextBuffer