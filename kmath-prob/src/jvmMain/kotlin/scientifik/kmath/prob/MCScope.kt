package scientifik.kmath.prob

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * A scope for a monte-carlo simulation or multi-coroutine random number generation
 */
class MCScope(override val coroutineContext: CoroutineContext, val random: RandomGenerator): CoroutineScope

/**
 * Launches a supervised Monte-Carlo scope
 */
suspend fun <T> mc(generator: RandomGenerator, block: suspend MCScope.() -> T): T =
    MCScope(coroutineContext, generator).block()

suspend fun <T> mc(seed: Long = -1, block: suspend MCScope.() -> T): T =
    mc(SplitRandomWrapper(seed), block)

inline fun MCScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend MCScope.() -> Unit
): Job {

    val newRandom = synchronized(this){random.fork()}
    return CoroutineScope(coroutineContext).launch(context, start) {
        MCScope(coroutineContext, newRandom).block()
    }
}

inline fun <T> MCScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend MCScope.() -> T
): Deferred<T> {
    val newRandom = synchronized(this){random.fork()}
    return CoroutineScope(coroutineContext).async(context, start) {
        MCScope(coroutineContext, newRandom).block()
    }
}

