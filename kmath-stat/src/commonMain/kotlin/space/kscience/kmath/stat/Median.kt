/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.structures.Buffer

/**
 * Non-composable median
 */
public class Median<T>(private val comparator: Comparator<T>) : BlockingStatistic<T, T> {
    override fun evaluateBlocking(data: Buffer<T>): T =
        data.asSequence().sortedWith(comparator).toList()[data.size / 2] //TODO check if this is correct

    public companion object {
        public val real: Median<Double> = Median { a: Double, b: Double -> a.compareTo(b) }
    }
}