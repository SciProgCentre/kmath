package scientifik.kmath.sequential

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.toChannel

interface DoubleProducer : Producer<Double> {
    val arrayOutput: ReceiveChannel<DoubleArray>
}

interface DoubleConsumer : Consumer<Double> {
    val arrayInput: SendChannel<DoubleArray>
}


abstract class AbstractDoubleProducer(scope: CoroutineScope) : AbstractProducer<Double>(scope), DoubleProducer {
    override suspend fun connectOutput(consumer: Consumer<Double>) {
        if (consumer is DoubleConsumer) {
            arrayOutput.toChannel(consumer.arrayInput)
        } else {
            super.connectOutput(consumer)
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
            super.connectOutput(consumer)
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