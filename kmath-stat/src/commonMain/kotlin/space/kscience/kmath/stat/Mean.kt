package space.kscience.kmath.stat

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

/**
 * Arithmetic mean
 */
public class Mean<T>(
    private val group: Ring<T>,
    private val division: (sum: T, count: Int) -> T,
) : ComposableStatistic<T, Pair<T, Int>, T>, BlockingStatistic<T, T> {

    override fun evaluateBlocking(data: Buffer<T>): T = group {
        var res = zero
        for (i in data.indices) {
            res += data[i]
        }
        division(res, data.size)
    }

    override suspend fun evaluate(data: Buffer<T>): T = super<ComposableStatistic>.evaluate(data)

    public override suspend fun computeIntermediate(data: Buffer<T>): Pair<T, Int> =
        evaluateBlocking(data) to data.size

    public override suspend fun composeIntermediate(first: Pair<T, Int>, second: Pair<T, Int>): Pair<T, Int> =
        group { first.first + second.first } to (first.second + second.second)

    public override suspend fun toResult(intermediate: Pair<T, Int>): T = group {
        division(intermediate.first, intermediate.second)
    }

    public companion object {
        //TODO replace with optimized version which respects overflow
        public val double: Mean<Double> = Mean(DoubleField) { sum, count -> sum / count }
        public val int: Mean<Int> = Mean(IntRing) { sum, count -> sum / count }
        public val long: Mean<Long> = Mean(LongRing) { sum, count -> sum / count }

        public fun evaluate(buffer: Buffer<Double>): Double = double.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Int>): Int = int.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Long>): Long = long.evaluateBlocking(buffer)
    }
}