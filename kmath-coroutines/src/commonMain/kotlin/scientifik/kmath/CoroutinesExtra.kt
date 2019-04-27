package scientifik.kmath

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*

val Dispatchers.Math: CoroutineDispatcher get() = Dispatchers.Default

@FlowPreview
inline class AsyncFlow<T>(val deferredFlow: Flow<Deferred<T>>) : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        deferredFlow.collect {
            collector.emit((it.await()))
        }
    }
}

@FlowPreview
fun <T, R> Flow<T>.async(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend (T) -> R
): AsyncFlow<R> {
    val flow = map {
        coroutineScope {
            async(dispatcher, start = CoroutineStart.LAZY) { block(it) }
        }
    }
    return AsyncFlow(flow)
}

@FlowPreview
fun <T, R> AsyncFlow<T>.map(action: (T) -> R) = deferredFlow.map { input ->
    coroutineScope {
        async(start = CoroutineStart.LAZY) { action(input.await()) }
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
suspend fun <T> AsyncFlow<T>.collect(concurrency: Int, collector: FlowCollector<T>){
    require(concurrency >= 0) { "Buffer size should be positive, but was $concurrency" }
    coroutineScope {
        //Starting up to N deferred coroutines ahead of time
        val channel = produce(capacity = concurrency) {
            deferredFlow.collect { value ->
                value.start()
                send(value)
            }
        }

        (channel as Job).invokeOnCompletion {
            if (it is CancellationException && it.cause == null) cancel()
        }

        for (element in channel) {
            collector.emit(element.await())
        }

        val producer = channel as Job
        if (producer.isCancelled) {
            producer.join()
            //throw producer.getCancellationException()
        }
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
suspend fun <T> AsyncFlow<T>.collect(concurrency: Int, action: suspend (value: T) -> Unit): Unit{
    collect(concurrency, object : FlowCollector<T> {
        override suspend fun emit(value: T) = action(value)
    })
}
