package space.kscience.kmath.misc

import space.kscience.kmath.operations.Space
import space.kscience.kmath.operations.invoke
import kotlin.jvm.JvmName

/**
 * Generic cumulative operation on iterator.
 *
 * @param T the type of initial iterable.
 * @param R the type of resulting iterable.
 * @param initial lazy evaluated.
 */
public inline fun <T, R> Iterator<T>.cumulative(initial: R, crossinline operation: (R, T) -> R): Iterator<R> =
    object : Iterator<R> {
        var state: R = initial

        override fun hasNext(): Boolean = this@cumulative.hasNext()

        override fun next(): R {
            state = operation(state, this@cumulative.next())
            return state
        }
    }

public inline fun <T, R> Iterable<T>.cumulative(initial: R, crossinline operation: (R, T) -> R): Iterable<R> =
    Iterable { this@cumulative.iterator().cumulative(initial, operation) }

public inline fun <T, R> Sequence<T>.cumulative(initial: R, crossinline operation: (R, T) -> R): Sequence<R> =
    Sequence { this@cumulative.iterator().cumulative(initial, operation) }

public fun <T, R> List<T>.cumulative(initial: R, operation: (R, T) -> R): List<R> =
    iterator().cumulative(initial, operation).asSequence().toList()

//Cumulative sum

/**
 * Cumulative sum with custom space
 */
public fun <T> Iterable<T>.cumulativeSum(space: Space<T>): Iterable<T> =
    space { cumulative(zero) { element: T, sum: T -> sum + element } }

@JvmName("cumulativeSumOfDouble")
public fun Iterable<Double>.cumulativeSum(): Iterable<Double> = cumulative(0.0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfInt")
public fun Iterable<Int>.cumulativeSum(): Iterable<Int> = cumulative(0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfLong")
public fun Iterable<Long>.cumulativeSum(): Iterable<Long> = cumulative(0L) { element, sum -> sum + element }

public fun <T> Sequence<T>.cumulativeSum(space: Space<T>): Sequence<T> =
    space { cumulative(zero) { element: T, sum: T -> sum + element } }

@JvmName("cumulativeSumOfDouble")
public fun Sequence<Double>.cumulativeSum(): Sequence<Double> = cumulative(0.0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfInt")
public fun Sequence<Int>.cumulativeSum(): Sequence<Int> = cumulative(0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfLong")
public fun Sequence<Long>.cumulativeSum(): Sequence<Long> = cumulative(0L) { element, sum -> sum + element }

public fun <T> List<T>.cumulativeSum(space: Space<T>): List<T> =
    space { cumulative(zero) { element: T, sum: T -> sum + element } }

@JvmName("cumulativeSumOfDouble")
public fun List<Double>.cumulativeSum(): List<Double> = cumulative(0.0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfInt")
public fun List<Int>.cumulativeSum(): List<Int> = cumulative(0) { element, sum -> sum + element }

@JvmName("cumulativeSumOfLong")
public fun List<Long>.cumulativeSum(): List<Long> = cumulative(0L) { element, sum -> sum + element }
