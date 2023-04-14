/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.DoubleBufferOps.Companion.map
import space.kscience.kmath.operations.DoubleField.pow
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.operations.fold


// TODO: add p-value
public data class VarianceRatioTestResult(val varianceRatio: Double, val zScore: Double)

public fun varianceRatioTest(series: Series<Double>, shift: Int, homoscedastic: Boolean): VarianceRatioTestResult {

    /**
     * Calculate the Z statistic and the p-value for the Lo and MacKinlay's Variance Ratio test (1987)
     * under Homoscedastic or Heteroscedstic assumptions
     * 	https://ssrn.com/abstract=346975
     * **/

    val sum = { x: Double, y: Double -> x + y }
    //TODO: catch if shift is too large
    with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
        val mean = series.fold(0.0, sum) / series.size
        val demeanedSquares = series.map { power(it - mean, 2) }
        val variance = demeanedSquares.fold(0.0, sum) // TODO: catch if variance is zero


        var seriesAgg = series
        for (i in 1..<shift) {
            seriesAgg = seriesAgg.zip(series.moveTo(i)) { v1, v2 -> v1 + v2 }
        }

        val demeanedSquaresAgg = seriesAgg.map { power(it - shift * mean, 2) }
        val varianceAgg = demeanedSquaresAgg.fold(0.0, sum)

        val varianceRatio =
            varianceAgg * (series.size.toDouble() - 1) / variance / (series.size.toDouble() - shift.toDouble() + 1) / (1 - shift.toDouble()/series.size.toDouble()) / shift.toDouble()


        // calculating asymptotic variance
        var phi: Double
        if (homoscedastic) {  // under homoscedastic null hypothesis
            phi = 2 * (2 * shift - 1.0) * (shift - 1.0) / (3 * shift * series.size)
        } else { // under homoscedastic null hypothesis
            phi = 0.0
            var shiftedProd = demeanedSquares
            for (j in 1..<shift) {
                shiftedProd = shiftedProd.zip(demeanedSquares.moveTo(j)) { v1, v2 -> v1 * v2 }
                val delta = series.size * shiftedProd.fold(0.0, sum) / variance.pow(2)
                phi += delta * 4 * (shift - j) * (shift - j) / shift / shift // TODO: refactor with square
            }
        }

        val zScore = (varianceRatio - 1) / phi.pow(0.5)
        return VarianceRatioTestResult(varianceRatio, zScore)
    }
}
