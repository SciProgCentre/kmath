package space.kscience.kmath.stat

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import space.kscience.kmath.coroutines.mapParallel
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asIterable
import space.kscience.kmath.structures.asSequence

/**
 * A function, that transforms a buffer of random quantities to some resulting value
 */
public interface Statistic<T, R> {
    public suspend operator fun invoke(data: Buffer<T>): R
}

/**
 * A statistic tha could be computed separately on different blocks of data and then composed
 * @param T - source type
 * @param I - intermediate block type
 * @param R - result type
 */
public interface ComposableStatistic<T, I, R> : Statistic<T, R> {
    //compute statistic on a single block
    public suspend fun computeIntermediate(data: Buffer<T>): I

    //Compose two blocks
    public suspend fun composeIntermediate(first: I, second: I): I

    //Transform block to result
    public suspend fun toResult(intermediate: I): R

    public override suspend fun invoke(data: Buffer<T>): R = toResult(computeIntermediate(data))
}

@FlowPreview
@ExperimentalCoroutinesApi
private fun <T, I, R> ComposableStatistic<T, I, R>.flowIntermediate(
    flow: Flow<Buffer<T>>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Flow<I> = flow
    .mapParallel(dispatcher) { computeIntermediate(it) }
    .runningReduce(::composeIntermediate)


/**
 * Perform a streaming statistical analysis on a chunked data. The computation of inner representation is done in parallel
 * if [dispatcher] allows it.
 *
 * The resulting flow contains values that include the whole previous statistics, not only the last chunk.
 */
@FlowPreview
@ExperimentalCoroutinesApi
public fun <T, I, R> ComposableStatistic<T, I, R>.flow(
    flow: Flow<Buffer<T>>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Flow<R> = flowIntermediate(flow, dispatcher).map(::toResult)

/**
 * Arithmetic mean
 */
public class Mean<T>(
    private val group: Group<T>,
    private val division: (sum: T, count: Int) -> T,
) : ComposableStatistic<T, Pair<T, Int>, T> {
    public override suspend fun computeIntermediate(data: Buffer<T>): Pair<T, Int> =
        group { sum(data.asIterable()) } to data.size

    public override suspend fun composeIntermediate(first: Pair<T, Int>, second: Pair<T, Int>): Pair<T, Int> =
        group { first.first + second.first } to (first.second + second.second)

    public override suspend fun toResult(intermediate: Pair<T, Int>): T = group {
        division(intermediate.first, intermediate.second)
    }

    public companion object {
        //TODO replace with optimized version which respects overflow
        public val real: Mean<Double> = Mean(DoubleField) { sum, count -> sum / count }
        public val int: Mean<Int> = Mean(IntRing) { sum, count -> sum / count }
        public val long: Mean<Long> = Mean(LongRing) { sum, count -> sum / count }
    }
}

/**
 * Non-composable median
 */
public class Median<T>(private val comparator: Comparator<T>) : Statistic<T, T> {
    public override suspend fun invoke(data: Buffer<T>): T =
        data.asSequence().sortedWith(comparator).toList()[data.size / 2] //TODO check if this is correct

    public companion object {
        public val real: Median<Double> = Median { a: Double, b: Double -> a.compareTo(b) }
    }
}
