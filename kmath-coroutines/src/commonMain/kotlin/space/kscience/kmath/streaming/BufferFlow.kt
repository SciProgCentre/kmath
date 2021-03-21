package space.kscience.kmath.streaming

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer

/**
 * Create a [Flow] from buffer
 */
public fun <T> Buffer<T>.asFlow(): Flow<T> = iterator().asFlow()

/**
 * Flat map a [Flow] of [Buffer] into continuous [Flow] of elements
 */
@FlowPreview
public fun <T> Flow<Buffer<T>>.spread(): Flow<T> = flatMapConcat { it.asFlow() }

/**
 * Collect incoming flow into fixed size chunks
 */
public fun <T> Flow<T>.chunked(bufferSize: Int, bufferFactory: BufferFactory<T>): Flow<Buffer<T>> = flow {
    require(bufferSize > 0) { "Resulting chunk size must be more than zero" }
    val list = ArrayList<T>(bufferSize)
    var counter = 0

    this@chunked.collect { element ->
        list.add(element)
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
public fun Flow<Double>.chunked(bufferSize: Int): Flow<DoubleBuffer> = flow {
    require(bufferSize > 0) { "Resulting chunk size must be more than zero" }

    if (this@chunked is BlockingDoubleChain) {
        // performance optimization for blocking primitive chain
        while (true) emit(nextBlock(bufferSize).asBuffer())
    } else {
        val array = DoubleArray(bufferSize)
        var counter = 0

        this@chunked.collect { element ->
            array[counter] = element
            counter++

            if (counter == bufferSize) {
                val buffer = DoubleBuffer(array)
                emit(buffer)
                counter = 0
            }
        }

        if (counter > 0) emit(DoubleBuffer(counter) { array[it] })
    }
}

/**
 * Map a flow to a moving window buffer. The window step is one.
 * In order to get different steps, one could use skip operation.
 */
public fun <T> Flow<T>.windowed(window: Int): Flow<Buffer<T>> = flow {
    require(window > 1) { "Window size must be more than one" }
    val ringBuffer = RingBuffer.boxing<T>(window)

    this@windowed.collect { element ->
        ringBuffer.push(element)
        emit(ringBuffer.snapshot())
    }
}
