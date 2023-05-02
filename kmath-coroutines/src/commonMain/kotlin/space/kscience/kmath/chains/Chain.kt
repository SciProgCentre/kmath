/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import space.kscience.kmath.UnstableKMathAPI

/**
 * A not-necessary-Markov chain of some type
 * @param T the chain element type
 */
public interface Chain<out T> : Flow<T> {
    /**
     * Generate next value, changing state if needed
     */
    public suspend fun next(): T

    /**
     * Create a copy of current chain state. Consuming resulting chain does not affect initial chain.
     */
    public suspend fun fork(): Chain<T>

    override suspend fun collect(collector: FlowCollector<T>): Unit =
        flow { while (true) emit(next()) }.collect(collector)

    public companion object
}

public fun <T> Iterator<T>.asChain(): Chain<T> = SimpleChain { next() }
public fun <T> Sequence<T>.asChain(): Chain<T> = iterator().asChain()

/**
 * A simple chain of independent tokens. [fork] returns the same chain.
 */
public class SimpleChain<out R>(private val gen: suspend () -> R) : Chain<R> {
    override suspend fun next(): R = gen()
    override suspend fun fork(): Chain<R> = this
}

/**
 * A stateless Markov chain
 */
public class MarkovChain<out R : Any>(private val seed: suspend () -> R, private val gen: suspend (R) -> R) : Chain<R> {
    private val mutex: Mutex = Mutex()
    private var value: R? = null

    public fun value(): R? = value

    override suspend fun next(): R = mutex.withLock {
        val newValue = gen(value ?: seed())
        value = newValue
        newValue
    }

    override suspend fun fork(): Chain<R> = MarkovChain(seed = { value ?: seed() }, gen = gen)
}

/**
 * A chain with possibly mutable state. The state must not be changed outside the chain. Two chins should never share
 * the state.
 *
 * @param S the state of the chain.
 * @param forkState the function to copy current state without modifying it.
 */
public class StatefulChain<S, out R>(
    private val state: S,
    private val seed: S.() -> R,
    private val forkState: ((S) -> S),
    private val gen: suspend S.(R) -> R,
) : Chain<R> {
    private val mutex: Mutex = Mutex()
    private var value: R? = null

    public fun value(): R? = value

    override suspend fun next(): R = mutex.withLock {
        val newValue = state.gen(value ?: state.seed())
        value = newValue
        newValue
    }

    override suspend fun fork(): Chain<R> = StatefulChain(forkState(state), seed, forkState, gen)
}

/**
 * A chain that repeats the same value
 */
public class ConstantChain<out T>(public val value: T) : Chain<T> {
    override suspend fun next(): T = value
    override suspend fun fork(): Chain<T> = this
}

/**
 * Map the chain result using suspended transformation. Initial chain result can no longer be safely consumed
 * since mapped chain consumes tokens. Accepts regular transformation function.
 */
public fun <T, R> Chain<T>.map(func: suspend (T) -> R): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = func(this@map.next())
    override suspend fun fork(): Chain<R> = this@map.fork().map(func)
}

/**
 * [block] must be a pure function or at least not use external random variables, otherwise fork could be broken
 */
public fun <T> Chain<T>.filter(block: (T) -> Boolean): Chain<T> = object : Chain<T> {
    override suspend fun next(): T {
        var next: T

        do next = this@filter.next()
        while (!block(next))

        return next
    }

    override suspend fun fork(): Chain<T> = this@filter.fork().filter(block)
}

/**
 * Map the whole chain
 */
@UnstableKMathAPI
public fun <T, R> Chain<T>.combine(mapper: suspend (Chain<T>) -> R): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = mapper(this@combine)
    override suspend fun fork(): Chain<R> = this@combine.fork().combine(mapper)
}

@UnstableKMathAPI
public fun <T, S, R> Chain<T>.combineWithState(
    state: S,
    stateFork: (S) -> S,
    mapper: suspend S.(Chain<T>) -> R,
): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = state.mapper(this@combineWithState)

    override suspend fun fork(): Chain<R> =
        this@combineWithState.fork().combineWithState(stateFork(state), stateFork, mapper)
}

/**
 * Zip two chains together using given transformation
 */
public fun <T, U, R> Chain<T>.zip(other: Chain<U>, block: suspend (T, U) -> R): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = block(this@zip.next(), other.next())
    override suspend fun fork(): Chain<R> = this@zip.fork().zip(other.fork(), block)
}
