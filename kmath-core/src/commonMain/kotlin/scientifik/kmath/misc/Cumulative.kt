package scientifik.kmath.misc

import kotlin.jvm.JvmName


/**
 * Generic cumulative operation on iterator
 * @param T type of initial iterable
 * @param R type of resulting iterable
 * @param initial lazy evaluated
 */
fun <T, R> Iterator<T>.cumulative(initial: R, operation: (T, R) -> R): Iterator<R> = object : Iterator<R> {
    var state: R = initial
    override fun hasNext(): Boolean = this@cumulative.hasNext()

    override fun next(): R {
        state = operation.invoke(this@cumulative.next(), state)
        return state
    }
}

fun <T, R> Iterable<T>.cumulative(initial: R, operation: (T, R) -> R): Iterable<R> = object : Iterable<R> {
    override fun iterator(): Iterator<R> = this@cumulative.iterator().cumulative(initial, operation)
}

fun <T, R> Sequence<T>.cumulative(initial: R, operation: (T, R) -> R): Sequence<R> = object : Sequence<R> {
    override fun iterator(): Iterator<R> = this@cumulative.iterator().cumulative(initial, operation)
}

fun <T, R> List<T>.cumulative(initial: R, operation: (T, R) -> R): List<R> = this.iterator().cumulative(initial, operation).asSequence().toList()

//Cumulative sum

@JvmName("cumulativeSumOfDouble")
fun Iterable<Double>.cumulativeSum() = this.cumulative(0.0){ element, sum -> sum + element}

@JvmName("cumulativeSumOfInt")
fun Iterable<Int>.cumulativeSum() = this.cumulative(0){ element, sum -> sum + element}

@JvmName("cumulativeSumOfLong")
fun Iterable<Long>.cumulativeSum() = this.cumulative(0L){ element, sum -> sum + element}

@JvmName("cumulativeSumOfDouble")
fun Sequence<Double>.cumulativeSum() = this.cumulative(0.0){ element, sum -> sum + element}

@JvmName("cumulativeSumOfInt")
fun Sequence<Int>.cumulativeSum() = this.cumulative(0){ element, sum -> sum + element}

@JvmName("cumulativeSumOfLong")
fun Sequence<Long>.cumulativeSum() = this.cumulative(0L){ element, sum -> sum + element}

@JvmName("cumulativeSumOfDouble")
fun List<Double>.cumulativeSum() = this.cumulative(0.0){ element, sum -> sum + element}

@JvmName("cumulativeSumOfInt")
fun List<Int>.cumulativeSum() = this.cumulative(0){ element, sum -> sum + element}

@JvmName("cumulativeSumOfLong")
fun List<Long>.cumulativeSum() = this.cumulative(0L){ element, sum -> sum + element}