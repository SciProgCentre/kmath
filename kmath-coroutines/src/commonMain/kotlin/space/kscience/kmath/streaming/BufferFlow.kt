/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(FlowPreview::class)

package space.kscience.kmath.streaming

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.operations.Group
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.Float64Buffer

/**
 * Create a [Flow] from buffer
 */
public fun <T> Buffer<T>.asFlow(): Flow<T> = iterator().asFlow()

/**
 * Flat map a [Flow] of [Buffer] into continuous [Flow] of elements
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> Flow<Buffer<T>>.spread(): Flow<T> = flatMapConcat { it.asFlow() }

/**
 * Collect incoming flow into fixed size chunks
 */
public fun <T> Flow<T>.chunked(bufferSize: Int, bufferFactory: BufferFactory<T>): Flow<Buffer<T>> = flow {
    require(bufferSize > 0) { "Resulting chunk size must be more than zero" }
    val list = ArrayList<T>(bufferSize)
    var counter = 0

    this@chunked.collect { element ->
        list += element
        counter++

        if (counter == bufferSize) {
            val buffer = bufferFactory(bufferSize) { list[it] }
            emit(buffer)
            list.clear()
            counter = 0
        }
    }

    if (counter > 0) emit(bufferFactory(counter) { list[it] })
}

/**
 * Specialized flow chunker for real buffer
 */
public fun Flow<Double>.chunked(bufferSize: Int): Flow<Float64Buffer> = flow {
    require(bufferSize > 0) { "Resulting chunk size must be more than zero" }

    if (this@chunked is BlockingDoubleChain) {
        // performance optimization for blocking primitive chain
        while (true) emit(nextBufferBlocking(bufferSize))
    } else {
        val array = DoubleArray(bufferSize)
        var counter = 0

        this@chunked.collect { element ->
            array[counter] = element
            counter++

            if (counter == bufferSize) {
                val buffer = Float64Buffer(array)
                emit(buffer)
                counter = 0
            }
        }

        if (counter > 0) emit(Float64Buffer(counter) { array[it] })
    }
}

/**
 * Map a flow to a moving window buffer. The window step is one.
 * To get different steps, one could use skip operation.
 */
public fun <T> Flow<T>.windowed(window: Int, algebra: Group<T>): Flow<Buffer<T>> = flow {
    require(window > 1) { "Window size must be more than one" }
    val ringBuffer = RingBuffer(window, algebra)

    this@windowed.collect { element ->
        ringBuffer.push(element)
        emit(ringBuffer.snapshot())
    }
}
