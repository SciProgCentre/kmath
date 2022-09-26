/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.random

import kotlinx.coroutines.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
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

public fun MCScope.asCoroutineScope(): CoroutineScope = object : CoroutineScope {
    override val coroutineContext: CoroutineContext get() = this@asCoroutineScope.coroutineContext
}

/**
 * Launches a supervised Monte-Carlo scope
 */
public suspend inline fun <T> mcScope(generator: RandomGenerator, block: MCScope.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MCScope(coroutineContext, generator).block()
}

/**
 * Launch mc scope with a given seed
 */
public suspend inline fun <T> mcScope(seed: Long, block: MCScope.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return mcScope(RandomGenerator.default(seed), block)
}

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