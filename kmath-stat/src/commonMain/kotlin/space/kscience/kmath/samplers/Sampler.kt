/*
 * Copyright 2018-2024 KMath contributors.
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
import space.kscience.kmath.structures.Float64
import kotlin.jvm.JvmName

/**
 * Sampler that generates chains of values of type [T].
 */
public fun interface Sampler<out T> {
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
    bufferFactory: BufferFactory<T>,
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
 * Generates [size] samples and chunks them into some buffers.
 */
@JvmName("sampleRealBuffer")
public inline fun <reified T : Any> Sampler<T>.sampleBuffer(generator: RandomGenerator, size: Int): Chain<Buffer<T>> =
    sampleBuffer(generator, size, BufferFactory())


/**
 * Samples a [Buffer] of values from this [Sampler].
 */
public suspend fun Sampler<Float64>.nextBuffer(generator: RandomGenerator, size: Int): Buffer<Float64> =
    sampleBuffer(generator, size).first()

//TODO add `context(RandomGenerator) Sampler.nextBuffer