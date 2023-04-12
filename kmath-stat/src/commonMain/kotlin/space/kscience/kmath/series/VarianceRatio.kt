/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.DoubleBufferOps.Companion.map
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.operations.fold


fun varianceRatio(series: Series<Double>, shift: Int): Double {
    val mean = series.fold(0.0) {acc, value -> acc + value} / series.size
    val demeanedSquares = series.map { power(it - mean, 2) }
    val variance = demeanedSquares.fold(0.0) {acc, value -> acc + value}

    with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
        val seriesAgg = series
        for (i in -1..-shift + 1) {
            seriesAgg.shiftOp(i) { v1, v2 -> v1 + v2 }
        }

        val demeanedSquaresAgg = seriesAgg.map { power(it - shift * mean, 2) }
        val varianceAgg = demeanedSquaresAgg.fold(0.0) { acc, value -> acc + value }

        return varianceAgg * (series.size - 1) / variance / (series.size - shift + 1) / (1 - shift / series.size)
    }
}
