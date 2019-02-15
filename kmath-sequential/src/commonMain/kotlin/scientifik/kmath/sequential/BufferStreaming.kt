package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.asBuffer
import scientifik.kmath.structures.asSequence

fun <T> Buffer<T>.asChannel(scope: CoroutineScope): ReceiveChannel<T> = scope.produce {
    for (i in (0 until size)) {
        send(get(i))
    }
}


interface BufferProducer<T> : Producer<T> {
    suspend fun receiveBuffer(): Buffer<T>
}

interface BufferConsumer<T> : Consumer<T> {
    suspend fun sendBuffer(buffer: Buffer<T>)
}

abstract class AbstractBufferProducer<T>(scope: CoroutineScope) : AbstractProducer<T>(scope), BufferProducer<T> {

    override fun connectOutput(consumer: Consumer<T>) {
        if (consumer is BufferConsumer) {
            launch {
                while (this.isActive) {
                    consumer.sendBuffer(receiveBuffer())
                }
            }
        } else {
            super.connectOutput(consumer)
        }
    }
}

abstract class AbstractBufferConsumer<T>(scope: CoroutineScope) : AbstractConsumer<T>(scope), BufferConsumer<T> {
    override fun connectInput(producer: Producer<T>) {
        if (producer is BufferProducer) {
            launch {
                while (isActive) {
                    sendBuffer(producer.receiveBuffer())
                }
            }
        } else {
            super.connectInput(producer)
        }
    }
}

abstract class AbstractBufferProcessor<T, R>(scope: CoroutineScope) :
    AbstractProcessor<T, R>(scope),
    BufferProducer<R>,
    BufferConsumer<T> {

    override fun connectOutput(consumer: Consumer<R>) {
        if (consumer is BufferConsumer) {
            launch {
                while (this.isActive) {
                    consumer.sendBuffer(receiveBuffer())
                }
            }
        } else {
            super.connectOutput(consumer)
        }
    }

    override fun connectInput(producer: Producer<T>) {
        if (producer is BufferProducer) {
            launch {
                while (isActive) {
                    sendBuffer(producer.receiveBuffer())
                }
            }
        } else {
            super.connectInput(producer)
        }
    }
}

/**
 * The basic generic buffer producer supporting both arrays and element-by-element simultaneously
 */
class BasicBufferProducer<T>(
    scope: CoroutineScope,
    capacity: Int = Channel.UNLIMITED,
    block: suspend ProducerScope<Buffer<T>>.() -> Unit
) : AbstractBufferProducer<T>(scope) {


    private val currentArray = atomic<ReceiveChannel<T>?>(null)
    private val channel: ReceiveChannel<Buffer<T>> by lazy { produce(capacity = capacity, block = block) }
    private val cachingChannel by lazy {
        channel.map {
            it.also { buffer -> currentArray.lazySet(buffer.asChannel(this)) }
        }
    }

    private fun DoubleArray.asChannel() = produce {
        for (value in this@asChannel) {
            send(value)
        }
    }

    override suspend fun receiveBuffer(): Buffer<T> = cachingChannel.receive()

    override suspend fun receive(): T = (currentArray.value ?: cachingChannel.receive().asChannel(this)).receive()
}


class BufferReducer<T, S>(
    scope: CoroutineScope,
    initialState: S,
    val fold: suspend (S, Buffer<T>) -> S
) : AbstractBufferConsumer<T>(scope) {

    var state: S = initialState
        private set

    override suspend fun sendBuffer(buffer: Buffer<T>) {
        state = fold(state, buffer)
    }

    override suspend fun send(value: T) = sendBuffer(arrayOf(value).asBuffer())
}

/**
 * Convert a [Buffer] to single element producer, splitting it in chunks if necessary
 */
fun <T> Buffer<T>.produce(scope: CoroutineScope = GlobalScope, chunkSize: Int = Int.MAX_VALUE) =
    if (size < chunkSize) {
        BasicBufferProducer<T>(scope) { send(this@produce) }
    } else {
        BasicBufferProducer<T>(scope) {
            //TODO optimize this!
            asSequence().chunked(chunkSize).forEach {
                send(it.asBuffer())
            }
        }
    }


/**
 * A buffer processor that works with buffers but could accumulate at lest [accumulate] elements from single input before processing.
 *
 * This class combines functions from [ChunkProcessor] and single buffer processor
 */
class AccumulatingBufferProcessor<T, R>(
    scope: CoroutineScope,
    val accumulate: Int,
    val process: suspend (Buffer<T>) -> Buffer<R>
) :
    AbstractBufferProcessor<T, R>(scope) {

    private val inputChannel = Channel<Buffer<T>>()
    private val outputChannel = inputChannel.map { process(it) }

    override suspend fun receive(): R {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun send(value: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun receiveBuffer(): Buffer<R> = outputChannel.receive()

    override suspend fun sendBuffer(buffer: Buffer<T>) {
        inputChannel.send(buffer)
    }

}