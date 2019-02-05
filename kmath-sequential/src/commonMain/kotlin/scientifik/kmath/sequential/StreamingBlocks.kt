package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Initial chain block. Could produce an element sequence and be connected to single [Consumer]
 *
 * The general rule is that channel is created on first call. Also each element is responsible for its connection so
 * while the connections are symmetric, the scope, used for making the connection is responsible for cancelation.
 *
 * Also connections are not reversible. Once connected block stays faithful until it finishes processing.
 * Manually putting elements to connected block could lead to undetermined behavior and must be avoided.
 */
interface Producer<T> {
    val output: ReceiveChannel<T>
    fun connect(consumer: Consumer<T>)

    val consumer: Consumer<T>?

    val outputIsConnected: Boolean get() = consumer != null
}

/**
 * Terminal chain block. Could consume an element sequence and be connected to signle [Producer]
 */
interface Consumer<T> {
    val input: SendChannel<T>
    fun connect(producer: Producer<T>)

    val producer: Producer<T>?

    val inputIsConnected: Boolean get() = producer != null
}

interface Processor<T, R> : Consumer<T>, Producer<R>

abstract class AbstractProducer<T>(protected val scope: CoroutineScope) : Producer<T> {
    override var consumer: Consumer<T>? = null
        protected set

    override fun connect(consumer: Consumer<T>) {
        //Ignore if already connected to specific consumer
        if (consumer != this.consumer) {
            if (outputIsConnected) error("The output slot of producer is occupied")
            if (consumer.inputIsConnected) error("The input slot of consumer is occupied")
            this.consumer = consumer
            if (consumer.producer != null) {
                //No need to save the job, it will be canceled on scope cancel
                scope.launch {
                    output.toChannel(consumer.input)
                }
                // connect back, consumer is already set so no circular reference
                consumer.connect(this)
            } else error("Unreachable statement")
        }
    }
}

abstract class AbstractConsumer<T>(protected val scope: CoroutineScope) : Consumer<T> {
    override var producer: Producer<T>? = null
        protected set

    override fun connect(producer: Producer<T>) {
        //Ignore if already connected to specific consumer
        if (producer != this.producer) {
            if (inputIsConnected) error("The input slot of consumer is occupied")
            if (producer.outputIsConnected) error("The input slot of producer is occupied")
            this.producer = producer
            //No need to save the job, it will be canceled on scope cancel
            if (producer.consumer != null) {
                scope.launch {
                    producer.output.toChannel(input)
                }
                // connect back
                producer.connect(this)
            } else error("Unreachable statement")
        }
    }
}

abstract class AbstracProcessor<T, R>(scope: CoroutineScope) : Processor<T, R>, AbstractProducer<R>(scope) {

    override var producer: Producer<T>? = null
        protected set

    override fun connect(producer: Producer<T>) {
        //Ignore if already connected to specific consumer
        if (producer != this.producer) {
            if (inputIsConnected) error("The input slot of consumer is occupied")
            if (producer.outputIsConnected) error("The input slot of producer is occupied")
            this.producer = producer
            //No need to save the job, it will be canceled on scope cancel
            if (producer.consumer != null) {
                scope.launch {
                    producer.output.toChannel(input)
                }
                // connect back
                producer.connect(this)
            } else error("Unreachable statement")
        }
    }
}

/**
 * A simple [produce]-based producer
 */
class GenericProducer<T>(
    scope: CoroutineScope,
    capacity: Int = Channel.UNLIMITED,
    block: suspend ProducerScope<T>.() -> Unit
) : AbstractProducer<T>(scope) {
    //The generation begins on first request to output
    override val output: ReceiveChannel<T> by lazy { scope.produce(capacity = capacity, block = block) }
}

/**
 * Thread-safe aggregator of values from input. The aggregator does not store all incoming values, it uses fold procedure
 * to incorporate them into state on-arrival.
 * The current aggregated state could be accessed by [value]. The input channel is inactive unless requested
 * @param T - the type of the input element
 * @param S - the type of the aggregator
 */
class Reducer<T, S>(
    scope: CoroutineScope,
    initialState: S,
    fold: suspend (S, T) -> S
) : AbstractConsumer<T>(scope) {

    private val state = atomic(initialState)

    val value: S = state.value

    override val input: SendChannel<T> by lazy {
        //create a channel and start process of reading all elements into aggregator
        Channel<T>(capacity = Channel.RENDEZVOUS).also {
            scope.launch {
                it.consumeEach { value -> state.update { fold(it, value) } }
            }
        }
    }
}

/**
 * Collector that accumulates all values in a list. List could be accessed from non-suspending environment via [list] value.
 */
class Collector<T>(scope: CoroutineScope) : AbstractConsumer<T>(scope) {

    private val _list = ArrayList<T>()
    private val mutex = Mutex()
    val list: List<T> get() = _list

    override val input: SendChannel<T> by lazy {
        //create a channel and start process of reading all elements into aggregator
        Channel<T>(capacity = Channel.RENDEZVOUS).also {
            scope.launch {
                it.consumeEach { value ->
                    mutex.withLock {
                        _list.add(value)
                    }
                }
            }
        }
    }
}