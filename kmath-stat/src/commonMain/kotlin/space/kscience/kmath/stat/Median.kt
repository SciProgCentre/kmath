/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.misc.sortedWith
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer

/**
 * Non-composable median
 */
public class Median<T>(private val field: Field<T>, private val comparator: Comparator<T>) : BlockingStatistic<T, T> {

    override fun evaluateBlocking(data: Buffer<T>): T = when {
        data.size == 0 -> error("Can't compute median of an empty buffer")
        data.size == 1 -> data[0]
        data.size % 2 == 0 -> with(field) {
            val sorted = data.sortedWith(comparator)
            (sorted[data.size / 2 - 1] + sorted[data.size / 2]) / 2
        }

        else -> data.sortedWith(comparator)[(data.size - 1) / 2]
    }

    public companion object {

        public fun evaluate(buffer: Buffer<Double>): Double = Float64Field.mean.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Int>): Int = Int32Ring.mean.evaluateBlocking(buffer)
        public fun evaluate(buffer: Buffer<Long>): Long = Int64Ring.mean.evaluateBlocking(buffer)
    }
}

public val Float64Field.median: Median<Double> get() = Median(Float64Field) { a, b -> a.compareTo(b) }
public val Int32Ring.median: Median<Int> get() = Median(Int32Field) { a, b -> a.compareTo(b) }
public val Int64Ring.median: Median<Long> get() = Median(Int64Field) { a, b -> a.compareTo(b) }