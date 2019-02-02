package scientifik.kmath.sequential

import scientifik.kmath.operations.Space


typealias Reducer<T, C, R> = (C, Iterable<T>) -> R

inline fun <T, C, R> Iterable<T>.reduce(context: C, crossinline reducer: Reducer<T, C, R>) =
    reducer(context, this@reduce)

inline fun <T, C, R> Sequence<T>.reduce(context: C, crossinline reducer: Reducer<T, C, R>) =
    asIterable().reduce(context, reducer)

inline fun <T, C, R> Array<T>.reduce(context: C, crossinline reducer: Reducer<T, C, R>) =
    asIterable().reduce(context, reducer)

object Reducers {
    fun <T : Any> mean(): Reducer<T, Space<T>, T> = { context, data ->
        data.fold(GenericSum(context)) { sum, value -> sum.apply { push(value) } }.value
    }
}