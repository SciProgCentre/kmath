/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*

/**
 * Extreme element (minimum or maximum) of non-empty input data according to the provided Comparator.
 */
public class ExtremeValueStatistic<T>(
    private val comparator: Comparator<T>,
) : ComposableStatistic<T, T, T>, BlockingStatistic<T, T> {

    public companion object {
        public fun <T> maxStatistic(): ExtremeValueStatistic<T> where T : Comparable<T> =
            ExtremeValueStatistic(reverseOrder())

        public fun <T> minStatistic(): ExtremeValueStatistic<T> where T : Comparable<T> =
            ExtremeValueStatistic(naturalOrder())
    }

    override fun evaluateBlocking(data: Buffer<T>): T {
        require(data.size > 0) { "Data must not be empty" }
        var res = data[0]
        for (i in data.indices) {
            if (comparator.compare(data[i], res) < 0) {
                res = data[i]
            }
        }
        return res
    }

    override suspend fun computeIntermediate(data: Buffer<T>): T = evaluateBlocking(data)

    override suspend fun composeIntermediate(first: T, second: T): T {
        if (comparator.compare(first, second) < 0) {
            return first
        } else {
            return second
        }
    }

    override suspend fun toResult(intermediate: T): T = intermediate

    override suspend fun evaluate(data: Buffer<T>): T = super<ComposableStatistic>.evaluate(data)

}

// min
public val Float64Field.min: ExtremeValueStatistic<Float64> get() = ExtremeValueStatistic.minStatistic()
public val Int32Ring.min: ExtremeValueStatistic<Int32> get() = ExtremeValueStatistic.minStatistic()
public val Int64Ring.min: ExtremeValueStatistic<Int64> get() = ExtremeValueStatistic.minStatistic()

// max
public val Float64Field.max: ExtremeValueStatistic<Float64> get() = ExtremeValueStatistic.maxStatistic()
public val Int32Ring.max: ExtremeValueStatistic<Int32> get() = ExtremeValueStatistic.maxStatistic()
public val Int64Ring.max: ExtremeValueStatistic<Int64> get() = ExtremeValueStatistic.maxStatistic()