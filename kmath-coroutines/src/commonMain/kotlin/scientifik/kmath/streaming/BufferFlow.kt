package scientifik.kmath.streaming

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory
import scientifik.kmath.structures.DoubleBuffer
import kotlin.coroutines.coroutineContext

/**
 * Create a [Flow] from buffer
 */
@FlowPreview
fun <T> Buffer<T>.asFlow() = iterator().asFlow()

/**
 * Flat map a [Flow] of [Buffer] into continuous [Flow] of elements
 */
@FlowPreview
fun <T> Flow<Buffer<out T>>.spread(): Flow<T> = flatMapConcat { it.asFlow() }

/**
 * Collect incoming flow into fixed size chunks
 */
@FlowPreview
fun <T> Flow<T>.chunked(bufferSize: Int, bufferFactory: BufferFactory<T>) = flow {
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
}

/**
 * Specialized flow chunker for real buffer
 */
@FlowPreview
fun Flow<Double>.chunked(bufferSize: Int) = flow {
    require(bufferSize > 0) { "Resulting chunk size must be more than zero" }
    val array = DoubleArray(bufferSize)
    var counter = 0

    this@chunked.collect { element ->
        array[counter] = element
        counter++
        if (counter == bufferSize) {
            val buffer = DoubleBuffer(array)
            emit(buffer)
        }
    }
}