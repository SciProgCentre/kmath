package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls
import kotlinx.atomicfu.getAndUpdate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import scientifik.kmath.operations.Space

/**
 * An object with a state that accumulates incoming elements
 */
interface Accumulator<in T> {
    /**
     * Push a value to accumulator. Blocks if [Accumulator] can't access any more elements at that time
     */
    fun push(value: T)

    /**
     * Does the same as [push], but suspends instead of blocking if accumulator is full
     */
    suspend fun send(value: T) = push(value)
}

/**
 * Push all elements to accumulator
 */
fun <T> Accumulator<T>.pushAll(values: Iterable<T>) {
    for (value in values) {
        push(value)
    }
}

/**
 * Offer all elements from channel to accumulator
 */
suspend fun <T> Accumulator<T>.offerAll(channel: ReceiveChannel<T>) {
    for (value in channel) {
        send(value)
    }
}

/**
 * Generic thread-safe average
 */
class GenericMean<T : Any>(val context: Space<T>) : Accumulator<T> {
    //TODO add guard against overflow
    val counter = atomic(0)
    val sum = atomic(context.zero)

    val value get() = with(context) { sum.value / counter.value }

    override fun push(value: T) {
        with(context) {
            counter.incrementAndGet()
            sum.getAndUpdate { it + value }
        }
    }
}