/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

import space.kscience.kmath.structures.Float64Buffer

/**
 * Chunked, specialized chain for double values, which supports blocking [nextBlocking] operation
 */
public interface BlockingDoubleChain : BlockingBufferChain<Double> {

    /**
     * Returns an [DoubleArray] chunk of [size] values of [next].
     */
    override fun nextBufferBlocking(size: Int): Float64Buffer

    override suspend fun fork(): BlockingDoubleChain

    public companion object
}

public fun BlockingDoubleChain.map(transform: (Double) -> Double): BlockingDoubleChain = object : BlockingDoubleChain {
    override fun nextBufferBlocking(size: Int): Float64Buffer {
        val block = this@map.nextBufferBlocking(size)
        return Float64Buffer(size) { transform(block[it]) }
    }

    override suspend fun fork(): BlockingDoubleChain = this@map.fork().map(transform)
}
