package scientifik.kmath.streaming

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import scientifik.kmath.chains.BlockingRealChain
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory
import scientifik.kmath.structures.RealBuffer
import scientifik.kmath.structures.asBuffer

/**
 * Create a [Flow] from buffer
 */
fun <T> Buffer<T>.asFlow(): Flow<T> = iterator().asFlow()

/**
 * Flat map a [Flow] of [Buffer] into continuous [Flow] of elements
 */
@FlowPreview
fun <T> Flow<Buffer<out T>>.spread(): Flow<T> = flatMapConcat { it.asFlow() }

/**
 * Collect incoming flow into fixed size chunks
 */
fun <T> Flow<T>.chunked(bufferSize: Int, bufferFactory: BufferFactory<T>): Flow<Buffer<T>> = flow {
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
    if (counter > 0) {
        emit(bufferFactory(counter) { list[it] })
    }
}

/**
 * Specialized flow chunker for real buffer
 */
fun Flow<Double>.chunked(bufferSize: Int): Flow<RealBuffer> = flow {
    require(bufferSize > 0) { "Resulting chunk size must be more than zero" }

    if (this@chunked is BlockingRealChain) {
        //performance optimization for blocking primitive chain
        while (true) {
            emit(nextBlock(bufferSize).asBuffer())
        }
    } else {
        val array = DoubleArray(bufferSize)
        var counter = 0

        this@chunked.collect { element ->
            array[counter] = element
            counter++
            if (counter == bufferSize) {
                val buffer = RealBuffer(array)
                emit(buffer)
                counter = 0
            }
        }
        if (counter > 0) {
            emit(RealBuffer(counter) { array[it] })
        }
    }
}

/**
 * Map a flow to a moving window buffer. The window step is one.
 * In order to get different steps, one could use skip operation.
 */
fun <T> Flow<T>.windowed(window: Int): Flow<Buffer<T>> = flow {
    require(window > 1) { "Window size must be more than one" }
    val ringBuffer = RingBuffer.boxing<T>(window)
    this@windowed.collect { element ->
        ringBuffer.push(element)
        emit(ringBuffer.snapshot())
    }
}
