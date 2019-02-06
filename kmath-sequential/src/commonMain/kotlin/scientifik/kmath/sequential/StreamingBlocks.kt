package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.isActive
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
    fun connect(consumer: Consumer<T>)

    suspend fun receive(): T

    val consumer: Consumer<T>?

    val outputIsConnected: Boolean get() = consumer != null

    //fun close()
}

/**
 * Terminal chain block. Could consume an element sequence and be connected to signle [Producer]
 */
interface Consumer<T> : CoroutineScope {
    fun connect(producer: Producer<T>)

    suspend fun send(value: T)

    val producer: Producer<T>?

    val inputIsConnected: Boolean get() = producer != null

    //fun close()
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
                connectOutput(consumer)
                // connect back, consumer is already set so no circular reference
                consumer.connect(this)
            } else error("Unreachable statement")
        }
    }

    protected open fun connectOutput(consumer: Consumer<T>) {
        launch {
            while (this.isActive) {
                consumer.send(receive())
            }
        }
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
                connectInput(producer)
                // connect back
                producer.connect(this)
            } else error("Unreachable statement")
        }
    }

    protected open fun connectInput(producer: Producer<T>) {
        launch {
            while (isActive) {
                send(producer.receive())
            }
        }
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
                connectInput(producer)
                // connect back
                producer.connect(this)
            } else error("Unreachable statement")
        }
    }

    protected open fun connectInput(producer: Producer<T>) {
        launch {
            while (isActive) {
                send(producer.receive())
            }
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

    private val channel: ReceiveChannel<T> by lazy { produce(capacity = capacity, block = block) }

    override suspend fun receive(): T = channel.receive()
}

/**
 * A simple pipeline [Processor] block
 */
class PipeProcessor<T, R>(
    scope: CoroutineScope,
    capacity: Int = Channel.RENDEZVOUS,
    process: suspend (T) -> R
) : AbstractProcessor<T, R>(scope) {

    private val input = Channel<T>(capacity)
    private val output: ReceiveChannel<R> = input.map(coroutineContext, process)

    override suspend fun receive(): R = output.receive()

    override suspend fun send(value: T) {
        input.send(value)
    }
}

/**
 * A [Processor] that splits the input in fixed chunk size and transforms each chunk
 */
class ChunkProcessor<T, R>(
    scope: CoroutineScope,
    chunkSize: Int,
    process: suspend (List<T>) -> R
) : AbstractProcessor<T, R>(scope) {

    private val input = Channel<T>(chunkSize)

    private val chunked = produce<List<T>>(coroutineContext) {
        val list = ArrayList<T>(chunkSize)
        repeat(chunkSize) {
            list.add(input.receive())
        }
        send(list)
    }

    private val output: ReceiveChannel<R> = chunked.map(coroutineContext, process)

    override suspend fun receive(): R = output.receive()

    override suspend fun send(value: T) {
        input.send(value)
    }
}

/**
 * A moving window [Processor] with circular buffer
 */
class WindowedProcessor<T, R>(
    scope: CoroutineScope,
    window: Int,
    process: suspend (List<T>) -> R
) : AbstractProcessor<T, R>(scope) {

    override suspend fun receive(): R {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun send(value: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


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

    private val input: SendChannel<T> by lazy {
        //create a channel and start process of reading all elements into aggregator
        Channel<T>(capacity = Channel.RENDEZVOUS).also {
            launch {
                it.consumeEach { value -> state.update { fold(it, value) } }
            }
        }
    }

    override suspend fun send(value: T) = input.send(value)
}

/**
 * Collector that accumulates all values in a list. List could be accessed from non-suspending environment via [list] value.
 */
class Collector<T>(scope: CoroutineScope) : AbstractConsumer<T>(scope) {

    private val _list = ArrayList<T>()
    private val mutex = Mutex()
    val list: List<T> get() = _list

    private val input: SendChannel<T> by lazy {
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

    override suspend fun send(value: T) = input.send(value)
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