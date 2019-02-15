package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.asBuffer
import scientifik.kmath.structures.asSequence

fun <T> Buffer<T>.asChannel(scope: CoroutineScope): ReceiveChannel<T> = scope.produce {
    for (i in (0 until size)) {
        send(get(i))
    }
}

interface DoubleProducer : Producer<Double> {
    suspend fun receiveArray(): Buffer<Double>
}

interface DoubleConsumer : Consumer<Double> {
    suspend fun sendArray(array: Buffer<Double>)
}

abstract class AbstractDoubleProducer(scope: CoroutineScope) : AbstractProducer<Double>(scope), DoubleProducer {

    override fun connectOutput(consumer: Consumer<Double>) {
        if (consumer is DoubleConsumer) {
            launch {
                while (this.isActive) {
                    consumer.sendArray(receiveArray())
                }
            }
        } else {
            super.connectOutput(consumer)
        }
    }
}

abstract class AbstractDoubleConsumer(scope: CoroutineScope) : AbstractConsumer<Double>(scope), DoubleConsumer {
    override fun connectInput(producer: Producer<Double>) {
        if (producer is DoubleProducer) {
            launch {
                while (isActive) {
                    sendArray(producer.receiveArray())
                }
            }
        } else {
            super.connectInput(producer)
        }
    }
}

abstract class AbstractDoubleProcessor(scope: CoroutineScope) : AbstractProcessor<Double, Double>(scope),
    DoubleProducer, DoubleConsumer {

    override fun connectOutput(consumer: Consumer<Double>) {
        if (consumer is DoubleConsumer) {
            launch {
                while (this.isActive) {
                    consumer.sendArray(receiveArray())
                }
            }
        } else {
            super.connectOutput(consumer)
        }
    }

    override fun connectInput(producer: Producer<Double>) {
        if (producer is DoubleProducer) {
            launch {
                while (isActive) {
                    sendArray(producer.receiveArray())
                }
            }
        } else {
            super.connectInput(producer)
        }
    }
}

/**
 * The basic [Double] producer supporting both arrays and element-by-element simultaneously
 */
class BasicDoubleProducer(
    scope: CoroutineScope,
    capacity: Int = Channel.UNLIMITED,
    block: suspend ProducerScope<Buffer<Double>>.() -> Unit
) : AbstractDoubleProducer(scope) {


    private val currentArray = atomic<ReceiveChannel<Double>?>(null)
    private val channel: ReceiveChannel<Buffer<Double>> by lazy { produce(capacity = capacity, block = block) }
    private val cachingChannel by lazy {
        channel.map {
            it.also { doubles -> currentArray.lazySet(doubles.asChannel(this)) }
        }
    }

    private fun DoubleArray.asChannel() = produce {
        for (value in this@asChannel) {
            send(value)
        }
    }

    override suspend fun receiveArray(): Buffer<Double> = cachingChannel.receive()

    override suspend fun receive(): Double = (currentArray.value ?: cachingChannel.receive().asChannel(this)).receive()
}


class DoubleReducer<S>(
    scope: CoroutineScope,
    initialState: S,
    val fold: suspend (S, Buffer<Double>) -> S
) : AbstractDoubleConsumer(scope) {

    var state: S = initialState
        private set

    override suspend fun sendArray(array: Buffer<Double>) {
        state = fold(state, array)
    }

    override suspend fun send(value: Double) = sendArray(doubleArrayOf(value).asBuffer())
}

/**
 * Convert an array to single element producer, splitting it in chunks if necessary
 */
fun Buffer<Double>.produce(scope: CoroutineScope = GlobalScope, chunkSize: Int = Int.MAX_VALUE) =
    if (size < chunkSize) {
        BasicDoubleProducer(scope) { send(this@produce) }
    } else {
        BasicDoubleProducer(scope) {
            //TODO optimize this!
            asSequence().chunked(chunkSize).forEach {
                send(it.asBuffer())
            }
        }
    }