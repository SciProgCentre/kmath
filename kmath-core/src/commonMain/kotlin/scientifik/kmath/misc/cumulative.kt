package scientifik.kmath.misc

import scientifik.kmath.operations.Space
import scientifik.kmath.operations.invoke
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmName

/**
 * Generic cumulative operation on iterator.
 *
 * @param T the type of initial iterable.
 * @param R the type of resulting iterable.
 * @param initial lazy evaluated.
 */
inline fun <T, R> Iterator<T>.cumulative(initial: R, crossinline operation: (R, T) -> R): Iterator<R> {
    contract { callsInPlace(operation) }

    return object : Iterator<R> {
        var state: R = initial

        override fun hasNext(): Boolean = this@cumulative.hasNext()

        override fun next(): R {
            state = operation(state, this@cumulative.next())
            return state
        }
    }
}

inline fun <T, R> Iterable<T>.cumulative(initial: R, crossinline operation: (R, T) -> R): Iterable<R> =
    Iterable { this@cumulative.iterator().cumulative(initial, operation) }

inline fun <T, R> Sequence<T>.cumulative(initial: R, crossinline operation: (R, T) -> R): Sequence<R> = Sequence {
    this@cumulative.iterator().cumulative(initial, operation)
}

fun <T, R> List<T>.cumulative(initial: R, operation: (R, T) -> R): List<R> =
    iterator().cumulative(initial, operation).asSequence().toList()

//Cumulative sum

/**
 * Cumulative sum with custom space
 */
fun <T> Iterable<T>.cumulativeSum(space: Space<T>): Iterable<T> =
    space { cumulative(zero) { element: T, sum: T -> sum + element } }

@JvmName("cumulativeSumOfDouble")
fun Iterable<Double>.cumulativeSum(): Iterable<Double> = cumulative(0.0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfInt")
fun Iterable<Int>.cumulativeSum(): Iterable<Int> = cumulative(0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfLong")
fun Iterable<Long>.cumulativeSum(): Iterable<Long> = cumulative(0L) { element, sum -> sum + element }

fun <T> Sequence<T>.cumulativeSum(space: Space<T>): Sequence<T> =
    space { cumulative(zero) { element: T, sum: T -> sum + element } }

@JvmName("cumulativeSumOfDouble")
fun Sequence<Double>.cumulativeSum(): Sequence<Double> = cumulative(0.0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfInt")
fun Sequence<Int>.cumulativeSum(): Sequence<Int> = cumulative(0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfLong")
fun Sequence<Long>.cumulativeSum(): Sequence<Long> = cumulative(0L) { element, sum -> sum + element }

fun <T> List<T>.cumulativeSum(space: Space<T>): List<T> =
    space { cumulative(zero) { element: T, sum: T -> sum + element } }

@JvmName("cumulativeSumOfDouble")
fun List<Double>.cumulativeSum(): List<Double> = cumulative(0.0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfInt")
fun List<Int>.cumulativeSum(): List<Int> = cumulative(0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfLong")
fun List<Long>.cumulativeSum(): List<Long> = cumulative(0L) { element, sum -> sum + element }
