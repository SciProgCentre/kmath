package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

/**
 * Initial chain block. Could produce an element sequence and be connected to single [Consumer]
 *
 * The general rule is that channel is created on first call. Also each element is responsible for its connection so
 * while the connections are symmetric, the scope, used for making the connection is responsible for cancelation.
 *
 * Also connections are not reversible. Once connected block stays faithful until it finishes processing.
 * Manually putting elements to connected block could lead to undetermined behavior and must be avoided.
 */
interface Producer<T> : CoroutineScope {
    val output: ReceiveChannel<T>
    fun connect(consumer: Consumer<T>)

    val consumer: Consumer<T>?

    val outputIsConnected: Boolean get() = consumer != null
}

/**
 * Terminal chain block. Could consume an element sequence and be connected to signle [Producer]
 */
interface Consumer<T> : CoroutineScope {
    val input: SendChannel<T>
    fun connect(producer: Producer<T>)

    val producer: Producer<T>?

    val inputIsConnected: Boolean get() = producer != null
}

interface Processor<T, R> : Consumer<T>, Producer<R>

abstract class AbstractProducer<T>(scope: CoroutineScope) : Producer<T> {
    override val coroutineContext: CoroutineContext = scope.coroutineContext

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
                launch {
                    connectOutput(consumer)
                }
                // connect back, consumer is already set so no circular reference
                consumer.connect(this)
            } else error("Unreachable statement")
        }
    }

    protected open suspend fun connectOutput(consumer: Consumer<T>) {
        output.toChannel(consumer.input)
    }
}

abstract class AbstractConsumer<T>(scope: CoroutineScope) : Consumer<T> {
    override val coroutineContext: CoroutineContext = scope.coroutineContext

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
                launch {
                    connectInput(producer)
                }
                // connect back
                producer.connect(this)
            } else error("Unreachable statement")
        }
    }

    protected open suspend fun connectInput(producer: Producer<T>) {
        producer.output.toChannel(input)
    }
}

abstract class AbstractProcessor<T, R>(scope: CoroutineScope) : Processor<T, R>, AbstractProducer<R>(scope) {

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
                launch {
                    connectInput(producer)
                }
                // connect back
                producer.connect(this)
            } else error("Unreachable statement")
        }
    }

    protected open suspend fun connectInput(producer: Producer<T>) {
        producer.output.toChannel(input)
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
    override val output: ReceiveChannel<T> by lazy { produce(capacity = capacity, block = block) }
}

/**
 * A simple pipeline [Processor] block
 */
class PipeProcessor<T, R>(
    scope: CoroutineScope,
    capacity: Int = Channel.RENDEZVOUS,
    process: suspend (T) -> R
) : AbstractProcessor<T, R>(scope) {

    private val _input = Channel<T>(capacity)
    override val input: SendChannel<T> get() = _input
    override val output: ReceiveChannel<R> = _input.map(coroutineContext, process)
}

/**
 * A [Processor] that splits the input in fixed chunk size and transforms each chunk
 */
class ChunkProcessor<T, R>(
    scope: CoroutineScope,
    chunkSize: Int,
    process: suspend (List<T>) -> R
) : AbstractProcessor<T, R>(scope) {

    private val _input = Channel<T>(chunkSize)

    override val input: SendChannel<T> get() = _input

    private val chunked = produce<List<T>>(coroutineContext) {
        val list = ArrayList<T>(chunkSize)
        repeat(chunkSize) {
            list.add(_input.receive())
        }
        send(list)
    }

    override val output: ReceiveChannel<R> = chunked.map(coroutineContext, process)
}

/**
 * A moving window [Processor]
 */
class WindowProcessor<T, R>(
    scope: CoroutineScope,
    window: Int,
    process: suspend (List<T>) -> R
) : AbstractProcessor<T, R>(scope) {


    override val output: ReceiveChannel<R>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val input: SendChannel<T>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.


}

//TODO add circular buffer processor

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
            launch {
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
            launch {
                it.consumeEach { value ->
                    mutex.withLock {
                        _list.add(value)
                    }
                }
            }
        }
    }
}

/**
 * Convert a sequence to [Producer]
 */
fun <T> Sequence<T>.produce(scope: CoroutineScope = GlobalScope) =
    GenericProducer<T>(scope) { forEach { send(it) } }

/**
 * Convert a [ReceiveChannel] to [Producer]
 */
fun <T> ReceiveChannel<T>.produce(scope: CoroutineScope = GlobalScope) =
    GenericProducer<T>(scope) { for (e in this@produce) send(e) }

/**
 * Create a reducer and connect this producer to reducer
 */
fun <T, S> Producer<T>.reduce(initialState: S, fold: suspend (S, T) -> S) =
    Reducer(this, initialState, fold).also { connect(it) }

/**
 * Create a [Collector] and attach it to this [Producer]
 */
fun <T> Producer<T>.collect() =
    Collector<T>(this).also { connect(it) }

fun <T, R> Producer<T>.process(capacity: Int = Channel.RENDEZVOUS, process: suspend (T) -> R) =
    PipeProcessor(this, capacity, process)

fun <T, R> Producer<T>.chunk(chunkSize: Int, process: suspend (List<T>) -> R) =
    ChunkProcessor(this, chunkSize, process)