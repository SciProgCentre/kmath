package space.kscience.kmath.stat

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * A scope for a Monte-Carlo computations or multi-coroutine random number generation.
 * The scope preserves the order of random generator calls as long as all concurrency calls is done via [launch] and [async]
 * functions.
 */
public class MCScope(
    public val coroutineContext: CoroutineContext,
    public val random: RandomGenerator,
)

/**
 * Launches a supervised Monte-Carlo scope
 */
public suspend inline fun <T> mcScope(generator: RandomGenerator, block: MCScope.() -> T): T =
    MCScope(coroutineContext, generator).block()

/**
 * Launch mc scope with a given seed
 */
public suspend inline fun <T> mcScope(seed: Long, block: MCScope.() -> T): T =
    mcScope(RandomGenerator.default(seed), block)

/**
 * Specialized launch for [MCScope]. Behaves the same way as regular [CoroutineScope.launch], but also stores the generator fork.
 * The method itself is not thread safe.
 */
public inline fun MCScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend MCScope.() -> Unit,
): Job {
    val newRandom = random.fork()
    return CoroutineScope(coroutineContext).launch(context, start) {
        MCScope(coroutineContext, newRandom).block()
    }
}

/**
 * Specialized async for [MCScope]. Behaves the same way as regular [CoroutineScope.async], but also stores the generator fork.
 * The method itself is not thread safe.
 */
public inline fun <T> MCScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend MCScope.() -> T,
): Deferred<T> {
    val newRandom = random.fork()
    return CoroutineScope(coroutineContext).async(context, start) {
        MCScope(coroutineContext, newRandom).block()
    }
}