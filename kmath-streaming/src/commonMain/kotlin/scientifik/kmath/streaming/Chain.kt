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

package scientifik.kmath.streaming

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive


/**
 * A not-necessary-Markov chain of some type
 * @param R - the chain element type
 */
interface Chain<out R> {
    /**
     * Last value of the chain. Returns null if [next] was not called
     */
    val value: R?

    /**
     * Generate next value, changing state if needed
     */
    suspend fun next(): R

    /**
     * Create a copy of current chain state. Consuming resulting chain does not affect initial chain
     */
    fun fork(): Chain<R>

}

/**
 * Chain as a coroutine receive channel
 */
@ExperimentalCoroutinesApi
fun <R> Chain<R>.asChannel(scope: CoroutineScope): ReceiveChannel<R> = scope.produce { while (isActive) send(next()) }

fun <T> Iterator<T>.asChain(): Chain<T> = SimpleChain { next() }
fun <T> Sequence<T>.asChain(): Chain<T> = iterator().asChain()


/**
 * Map the chain result using suspended transformation. Initial chain result can no longer be safely consumed
 * since mapped chain consumes tokens. Accepts regular transformation function
 */
fun <T, R> Chain<T>.map(func: (T) -> R): Chain<R> {
    val parent = this;
    return object : Chain<R> {
        override val value: R? get() = parent.value?.let(func)

        override suspend fun next(): R {
            return func(parent.next())
        }

        override fun fork(): Chain<R> {
            return parent.fork().map(func)
        }
    }
}

/**
 * A simple chain of independent tokens
 */
class SimpleChain<out R>(private val gen: suspend () -> R) : Chain<R> {
    private val atomicValue = atomic<R?>(null)
    override val value: R? get() = atomicValue.value

    override suspend fun next(): R = gen().also { atomicValue.lazySet(it) }

    override fun fork(): Chain<R> = this
}

//TODO force forks on mapping operations?

/**
 * A stateless Markov chain
 */
class MarkovChain<out R : Any>(private val seed: () -> R, private val gen: suspend (R) -> R) :
    Chain<R> {

    constructor(seed: R, gen: suspend (R) -> R) : this({ seed }, gen)

    private val atomicValue = atomic<R?>(null)
    override val value: R get() = atomicValue.value ?: seed()

    override suspend fun next(): R {
        val newValue = gen(value)
        atomicValue.lazySet(newValue)
        return value
    }

    override fun fork(): Chain<R> {
        return MarkovChain(value, gen)
    }
}

/**
 * A chain with possibly mutable state. The state must not be changed outside the chain. Two chins should never share the state
 * @param S - the state of the chain
 */
class StatefulChain<S, out R>(
    private val state: S,
    private val seed: S.() -> R,
    private val gen: suspend S.(R) -> R
) : Chain<R> {

    constructor(state: S, seed: R, gen: suspend S.(R) -> R) : this(state, { seed }, gen)

    private val atomicValue = atomic<R?>(null)
    override val value: R get() = atomicValue.value ?: seed(state)

    override suspend fun next(): R {
        val newValue = gen(state, value)
        atomicValue.lazySet(newValue)
        return value
    }

    override fun fork(): Chain<R> {
        throw RuntimeException("Fork not supported for stateful chain")
    }
}

/**
 * A chain that repeats the same value
 */
class ConstantChain<out T>(override val value: T) : Chain<T> {
    override suspend fun next(): T {
        return value
    }

    override fun fork(): Chain<T> {
        return this
    }
}