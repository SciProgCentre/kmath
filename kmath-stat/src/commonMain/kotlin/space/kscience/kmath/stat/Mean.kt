/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

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

    override suspend fun computeIntermediate(data: Buffer<T>): Pair<T, Int> = group {
        var res = zero
        for (i in data.indices) {
            res += data[i]
        }
        res to data.size
    }

    override suspend fun composeIntermediate(first: Pair<T, Int>, second: Pair<T, Int>): Pair<T, Int> =
        group { first.first + second.first } to (first.second + second.second)

    override suspend fun toResult(intermediate: Pair<T, Int>): T = group {
        division(intermediate.first, intermediate.second)
    }

    public companion object {
        @Deprecated("Use Double.mean instead")
        public val double: Mean<Double> = Mean(DoubleField) { sum, count -> sum / count }

        @Deprecated("Use Int.mean instead")
        public val int: Mean<Int> = Mean(IntRing) { sum, count -> sum / count }

        @Deprecated("Use Long.mean instead")
        public val long: Mean<Long> = Mean(LongRing) { sum, count -> sum / count }

        public fun evaluate(buffer: Buffer<Double>): Double = DoubleField.mean.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Int>): Int = IntRing.mean.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Long>): Long = LongRing.mean.evaluateBlocking(buffer)
    }
}


//TODO replace with optimized version which respects overflow
public val DoubleField.mean: Mean<Double> get() = Mean(DoubleField) { sum, count -> sum / count }
public val IntRing.mean: Mean<Int> get() = Mean(IntRing) { sum, count -> sum / count }
public val LongRing.mean: Mean<Long> get() = Mean(LongRing) { sum, count -> sum / count }


