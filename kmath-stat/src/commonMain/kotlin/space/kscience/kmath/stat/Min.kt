/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*

/**
 * Minimum element of non-empty input data
 */
public class MinStatistic<T : Comparable<T>> : BlockingStatistic<T, T>, ComposableStatistic<T, T, T> {
    override fun evaluateBlocking(data: Buffer<T>): T {
        require(data.size > 0) { "Data must not be empty" }
        var res = data[0]
        for (i in 1..data.indices.last) {
            val e = data[i]
            if (e < res) res = e
        }
        return res
    }

    override suspend fun computeIntermediate(data: Buffer<T>): T = evaluateBlocking(data)

    override suspend fun composeIntermediate(first: T, second: T): T = if (first < second) first else second

    override suspend fun toResult(intermediate: T): T = intermediate

    override suspend fun evaluate(data: Buffer<T>): T = super<ComposableStatistic>.evaluate(data)

}

// min
public val Float64Field.min: MinStatistic<Float64> get() = MinStatistic()
public val Int32Ring.min: MinStatistic<Int32> get() = MinStatistic()
public val Int64Ring.min: MinStatistic<Int64> get() = MinStatistic()
