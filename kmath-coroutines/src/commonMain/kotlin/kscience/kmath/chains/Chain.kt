/*
 * Copyright  2018 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package kscience.kmath.chains

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A not-necessary-Markov chain of some type
 * @param R - the chain element type
 */
public interface Chain<out R> : Flow<R> {
    /**
     * Generate next value, changing state if needed
     */
    public suspend fun next(): R

    /**
     * Create a copy of current chain state. Consuming resulting chain does not affect initial chain
     */
    public fun fork(): Chain<R>

    override suspend fun collect(collector: FlowCollector<R>): Unit =
        flow { while (true) emit(next()) }.collect(collector)

    public companion object
}

public fun <T> Iterator<T>.asChain(): Chain<T> = SimpleChain { next() }
public fun <T> Sequence<T>.asChain(): Chain<T> = iterator().asChain()

/**
 * A simple chain of independent tokens. [fork] returns the same chain.
 */
public class SimpleChain<out R>(private val gen: suspend () -> R) : Chain<R> {
    public override suspend fun next(): R = gen()
    public override fun fork(): Chain<R> = this
}

/**
 * A stateless Markov chain
 */
public class MarkovChain<out R : Any>(private val seed: suspend () -> R, private val gen: suspend (R) -> R) : Chain<R> {
    private val mutex: Mutex = Mutex()
    private var value: R? = null

    public fun value(): R? = value

    public override suspend fun next(): R {
        mutex.withLock {
            val newValue = gen(value ?: seed())
            value = newValue
            return newValue
        }
    }

    public override fun fork(): Chain<R> = MarkovChain(seed = { value ?: seed() }, gen = gen)
}

/**
 * A chain with possibly mutable state. The state must not be changed outside the chain. Two chins should never share the state
 * @param S - the state of the chain
 * @param forkState - the function to copy current state without modifying it
 */
public class StatefulChain<S, out R>(
    private val state: S,
    private val seed: S.() -> R,
    private val forkState: ((S) -> S),
    private val gen: suspend S.(R) -> R
) : Chain<R> {
    private val mutex: Mutex = Mutex()
    private var value: R? = null

    public fun value(): R? = value

    public override suspend fun next(): R {
        mutex.withLock {
            val newValue = state.gen(value ?: state.seed())
            value = newValue
            return newValue
        }
    }

    public override fun fork(): Chain<R> = StatefulChain(forkState(state), seed, forkState, gen)
}

/**
 * A chain that repeats the same value
 */
public class ConstantChain<out T>(public val value: T) : Chain<T> {
    public override suspend fun next(): T = value
    public override fun fork(): Chain<T> = this
}

/**
 * Map the chain result using suspended transformation. Initial chain result can no longer be safely consumed
 * since mapped chain consumes tokens. Accepts regular transformation function
 */
public fun <T, R> Chain<T>.map(func: suspend (T) -> R): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = func(this@map.next())
    override fun fork(): Chain<R> = this@map.fork().map(func)
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

    override fun fork(): Chain<T> = this@filter.fork().filter(block)
}

/**
 * Map the whole chain
 */
public fun <T, R> Chain<T>.collect(mapper: suspend (Chain<T>) -> R): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = mapper(this@collect)
    override fun fork(): Chain<R> = this@collect.fork().collect(mapper)
}

public fun <T, S, R> Chain<T>.collectWithState(
    state: S,
    stateFork: (S) -> S,
    mapper: suspend S.(Chain<T>) -> R
): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = state.mapper(this@collectWithState)

    override fun fork(): Chain<R> =
        this@collectWithState.fork().collectWithState(stateFork(state), stateFork, mapper)
}

/**
 * Zip two chains together using given transformation
 */
public fun <T, U, R> Chain<T>.zip(other: Chain<U>, block: suspend (T, U) -> R): Chain<R> = object : Chain<R> {
    override suspend fun next(): R = block(this@zip.next(), other.next())
    override fun fork(): Chain<R> = this@zip.fork().zip(other.fork(), block)
}
