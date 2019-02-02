package scientifik.kmath.sequential

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import scientifik.kmath.operations.Space

/**
 * An object with a state that accumulates incoming elements
 */
interface Accumulator<in T> {
    //PENDING use suspend operations?
    fun push(value: T)
}

fun <T> Accumulator<T>.pushAll(values: Iterable<T>) {
    values.forEach { push(it) }
}

/**
 * Generic thread-safe summator
 */
class GenericSum<T : Any>(val context: Space<T>) : Accumulator<T> {
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