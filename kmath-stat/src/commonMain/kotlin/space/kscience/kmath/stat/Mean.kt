/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.indices

/**
 * Arithmetic mean
 */
public class Mean<T>(
    private val field: Field<T>,
) : ComposableStatistic<T, Pair<T, Int>, T>, BlockingStatistic<T, T> {

    override fun evaluateBlocking(data: Buffer<T>): T = with(field) {
        var res = zero
        for (i in data.indices) {
            res += data[i]
        }
        res / data.size
    }

    override suspend fun evaluate(data: Buffer<T>): T = super<ComposableStatistic>.evaluate(data)

    override suspend fun computeIntermediate(data: Buffer<T>): Pair<T, Int> = with(field) {
        var res = zero
        for (i in data.indices) {
            res += data[i]
        }
        res to data.size
    }

    override suspend fun composeIntermediate(first: Pair<T, Int>, second: Pair<T, Int>): Pair<T, Int> =
        with(field) { first.first + second.first } to (first.second + second.second)

    override suspend fun toResult(intermediate: Pair<T, Int>): T = with(field) {
        intermediate.first / intermediate.second
    }

    public companion object {
        public fun evaluate(buffer: Buffer<Float64>): Double = Float64Field.mean.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Int>): Int = Int32Ring.mean.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Long>): Long = Int64Ring.mean.evaluateBlocking(buffer)
    }
}


//TODO replace with optimized version which respects overflow
public val Float64Field.mean: Mean<Float64> get() = Mean(Float64Field)
public val Int32Ring.mean: Mean<Int> get() = Mean(Int32Field)
public val Int64Ring.mean: Mean<Long> get() = Mean(Int64Field)


