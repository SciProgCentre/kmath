/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package space.kscience.kmath.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*

public val Dispatchers.Math: CoroutineDispatcher
    get() = Default

/**
 * An imitator of [Deferred] which holds a suspended function block and dispatcher
 */
@PublishedApi
internal class LazyDeferred<out T>(val dispatcher: CoroutineDispatcher, val block: suspend CoroutineScope.() -> T) {
    private var deferred: Deferred<T>? = null

    fun start(scope: CoroutineScope) {
        if (deferred == null) deferred = scope.async(dispatcher, block = block)
    }

    suspend fun await(): T = deferred?.await() ?: error("Coroutine not started")
}

public class AsyncFlow<out T> @PublishedApi internal constructor(
    @PublishedApi internal val deferredFlow: Flow<LazyDeferred<T>>,
) : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>): Unit =
        deferredFlow.collect { collector.emit((it.await())) }
}

public inline fun <T, R> Flow<T>.async(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline block: suspend CoroutineScope.(T) -> R,
): AsyncFlow<R> {
    val flow = map { LazyDeferred(dispatcher) { block(it) } }
    return AsyncFlow(flow)
}

public inline fun <T, R> AsyncFlow<T>.map(crossinline action: (T) -> R): AsyncFlow<R> =
    AsyncFlow(deferredFlow.map { input ->
        //TODO add function composition
        LazyDeferred(input.dispatcher) {
            input.start(this)
            action(input.await())
        }
    })

public suspend fun <T> AsyncFlow<T>.collect(concurrency: Int, collector: FlowCollector<T>) {
    require(concurrency >= 1) { "Buffer size should be more than 1, but was $concurrency" }

    coroutineScope {
        //Starting up to N deferred coroutines ahead of time
        val channel: ReceiveChannel<LazyDeferred<T>> = produce(capacity = concurrency - 1) {
            deferredFlow.collect { value ->
                value.start(this@coroutineScope)
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

public suspend inline fun <T> AsyncFlow<T>.collect(
    concurrency: Int,
    crossinline action: suspend (value: T) -> Unit,
): Unit = collect(concurrency, FlowCollector<T> { value -> action(value) })

public inline fun <T, R> Flow<T>.mapParallel(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline transform: suspend (T) -> R,
): Flow<R> = flatMapMerge { value -> flow { emit(transform(value)) } }.flowOn(dispatcher)
