package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch

interface DoubleProducer : Producer<Double> {
    suspend fun receiveArray(): DoubleArray
}

interface DoubleConsumer : Consumer<Double> {
    suspend fun sendArray(): DoubleArray
}


abstract class AbstractDoubleProducer(scope: CoroutineScope) : AbstractProducer<Double>(scope), DoubleProducer {
    override suspend fun connectOutput(consumer: Consumer<Double>) {
        if (consumer is DoubleConsumer) {
            arrayOutput.toChannel(consumer.arrayInput)
        } else {
            connectOutput(super, consumer)
        }
    }
}

abstract class AbstractDoubleConsumer(scope: CoroutineScope) : AbstractConsumer<Double>(scope), DoubleConsumer {
    override suspend fun connectInput(producer: Producer<Double>) {
        if (producer is DoubleProducer) {
            producer.arrayOutput.toChannel(arrayInput)
        } else {
            super.connectInput(producer)
        }
    }
}

abstract class AbstractDoubleProcessor(scope: CoroutineScope) : AbstractProcessor<Double, Double>(scope),
    DoubleProducer, DoubleConsumer {

    override suspend fun connectOutput(consumer: Consumer<Double>) {
        if (consumer is DoubleConsumer) {
            arrayOutput.toChannel(consumer.arrayInput)
        } else {
            connectOutput(super, consumer)
        }
    }

    override suspend fun connectInput(producer: Producer<Double>) {
        if (producer is DoubleProducer) {
            producer.arrayOutput.toChannel(arrayInput)
        } else {
            super.connectInput(producer)
        }
    }
}

class DoubleReducer<S>(
    scope: CoroutineScope,
    initialState: S,
    fold: suspend (S, DoubleArray) -> S
) : AbstractDoubleConsumer(scope) {
    private val state = atomic(initialState)

    val value: S = state.value

    override val arrayInput: SendChannel<DoubleArray> by lazy {
        //create a channel and start process of reading all elements into aggregator
        Channel<DoubleArray>(capacity = Channel.RENDEZVOUS).also {
            launch {
                it.consumeEach { value -> state.update { fold(it, value) } }
            }
        }
    }

    override val input: SendChannel<DoubleArray> = object :Abstr


}