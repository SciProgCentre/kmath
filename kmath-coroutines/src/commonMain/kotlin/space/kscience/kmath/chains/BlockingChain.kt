/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

import space.kscience.kmath.structures.Buffer


public interface BufferChain<out T> : Chain<T> {
    public suspend fun nextBuffer(size: Int): Buffer<T>
    override suspend fun fork(): BufferChain<T>
}

/**
 * A chain with blocking generator that could be used without suspension
 */
public interface BlockingChain<out T> : Chain<T> {
    /**
     * Get the next value without concurrency support. Not guaranteed to be thread safe.
     */
    public fun nextBlocking(): T

    override suspend fun next(): T = nextBlocking()

    override suspend fun fork(): BlockingChain<T>
}


public interface BlockingBufferChain<out T> : BlockingChain<T>, BufferChain<T> {

    public fun nextBufferBlocking(size: Int): Buffer<T>

    override fun nextBlocking(): T = nextBufferBlocking(1)[0]

    override suspend fun nextBuffer(size: Int): Buffer<T> = nextBufferBlocking(size)

    override suspend fun fork(): BlockingBufferChain<T>
}


public suspend inline fun <reified T : Any> Chain<T>.nextBuffer(size: Int): Buffer<T> = if (this is BufferChain) {
    nextBuffer(size)
} else {
    Buffer.auto(size) { next() }
}

public inline fun <reified T : Any> BlockingChain<T>.nextBufferBlocking(
    size: Int,
): Buffer<T> = if (this is BlockingBufferChain) {
    nextBufferBlocking(size)
} else {
    Buffer.auto(size) { nextBlocking() }
}