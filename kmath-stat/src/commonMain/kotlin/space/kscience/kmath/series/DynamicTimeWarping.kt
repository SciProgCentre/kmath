/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*
import space.kscience.kmath.nd.DoubleBufferND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.abs


/**
 * Stores a result of [dynamicTimeWarping]. The class contains:
 * 1. [Total penalty cost][totalCost] for series alignment.
 * 2. [Align matrix][alignMatrix] that describes which point of the first series matches to point of the other series.
 */
public data class DynamicTimeWarpingData(
    val totalCost : Double = 0.0,
    val alignMatrix : DoubleBufferND = DoubleFieldOpsND.structureND(ShapeND(0, 0)) { (_, _) -> 0.0}
)

/**
 * DTW method implementation. Returns alignment matrix for two series comparing and penalty for this alignment.
 */
@OptIn(PerformancePitfall::class)
public fun DoubleFieldOpsND.dynamicTimeWarping(series1 : DoubleBuffer, series2 : DoubleBuffer) : DynamicTimeWarpingData {
    // Create a special matrix of costs alignment for the two series.
    val costMatrix = structureND(ShapeND(series1.size, series2.size)) { (row, col) ->
        abs(series1[row] - series2[col])
    }

    // Initialise the cost matrix by formulas
    // costMatrix[i, j] = euclideanNorm(series1(i), series2(j)) +
    //     min(costMatrix[i - 1, j], costMatrix[i, j - 1], costMatrix[i - 1, j - 1]).
    for ((row, col) in costMatrix.indices) {
        costMatrix[row, col] += when {
            row == 0 && col == 0 -> continue
            row == 0 -> costMatrix[row, col - 1]
            col == 0 -> costMatrix[row - 1, col]
            else -> minOf(
                costMatrix[row, col - 1],
                costMatrix[row - 1, col],
                costMatrix[row - 1, col - 1]
            )
        }
    }

    // alignMatrix contains non-zero values at position where two points from series matches
    // Values are penalty for concatenation of current points.
    val alignMatrix = structureND(ShapeND(series1.size, series2.size)) { _ -> 0.0}
    var index1 = series1.size - 1
    var index2 = series2.size - 1
    var cost = 0.0
    var pathLength = 0

    alignMatrix[index1, index2] = costMatrix[index1, index2]
    cost += costMatrix[index1, index2]
    pathLength++
    while (index1 != 0 || index2 != 0) {
        when {
            index1 == 0 -> {
                index2--
            }
            index2 == 0 -> {
                index1--
            }
            costMatrix[index1, index2] == costMatrix[index1, index2 - 1] + abs(series1[index1] - series2[index2]) -> {
                index2--
            }
            costMatrix[index1, index2] == costMatrix[index1 - 1, index2] + abs(series1[index1] - series2[index2]) -> {
                index1--
            }
            costMatrix[index1, index2] == costMatrix[index1 - 1, index2 - 1] + abs(series1[index1] - series2[index2]) -> {
                index1--
                index2--
            }
        }
        alignMatrix[index1, index2] = costMatrix[index1, index2]
        cost += costMatrix[index1, index2]
        pathLength++
    }
    cost /= pathLength
    
    return DynamicTimeWarpingData(cost, alignMatrix)
}


