package scientifik.kmath.prob

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * A scope for a monte-carlo simulation or multi-coroutine random number generation
 */
class MCScope private constructor(val coroutineContext: CoroutineContext) {
    fun mcScope(context: CoroutineContext): MCScope = MCScope(context + coroutineContext[Random]!!.split())

    companion object {
        suspend fun init(generator: RandomGenerator): MCScope =
            MCScope(coroutineContext + Random(generator))
    }
}

inline class Random(val generator: RandomGenerator) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Companion

    fun split(): Random = Random(generator.fork())

    companion object : CoroutineContext.Key<Random>
}

val CoroutineContext.random get() = this[Random]?.generator

val MCScope.random get() = coroutineContext.random!!

/**
 * Launches a supervised Monte-Carlo scope
 */
suspend fun <T> mc(generator: RandomGenerator, block: suspend MCScope.() -> T): T =
    supervisorScope {
        MCScope.init(generator).block()
    }

suspend fun <T> mc(seed: Long = -1, block: suspend MCScope.() -> T): T =
    mc(SplitRandomWrapper(seed), block)

inline fun MCScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend MCScope.() -> Unit
): Job = CoroutineScope(coroutineContext).launch(context + coroutineContext[Random]!!.split(), start) {
    mcScope(coroutineContext).block()
}

inline fun <T> MCScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend MCScope.() -> T
): Deferred<T> = CoroutineScope(coroutineContext).async(context, start) {
    mcScope(coroutineContext).block()
}

