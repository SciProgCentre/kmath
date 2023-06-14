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
 *  Offset constants which will be used later. Added them for avoiding "magical numbers" problem.
 */
internal enum class DtwOffset {
    LEFT,
    BOTTOM,
    DIAGONAL
}

/**
 * Public class to store result of method. Class contains total penalty cost for series alignment.
 * Also, this class contains align matrix (which point of the first series matches to point of the other series).
 */
public data class DynamicTimeWarpingData(
    val totalCost : Double = 0.0,
    val alignMatrix : DoubleBufferND = DoubleFieldOpsND.structureND(ShapeND(0, 0)) { (_, _) -> 0.0}
)

/**
 * PathIndices class for better code perceptibility.
 * Special fun moveOption represent offset for indices. Arguments of this function
 * is flags for bottom, diagonal or left offsets respectively.
 */
internal data class PathIndices (var id_x: Int, var id_y: Int) {
    fun moveOption (direction: DtwOffset) {
        when(direction) {
            DtwOffset.BOTTOM -> id_x--
            DtwOffset.DIAGONAL -> {
                id_x--
                id_y--
            }
            DtwOffset.LEFT -> id_y--
        }
    }
}

/**
 * Final DTW method realization. Returns alignment matrix
 * for two series comparing and penalty for this alignment.
 */
@OptIn(PerformancePitfall::class)
public fun DoubleFieldOpsND.dynamicTimeWarping(series1 : DoubleBuffer, series2 : DoubleBuffer) : DynamicTimeWarpingData {
    var cost = 0.0
    var pathLength = 0
    // Special matrix of costs alignment for two series.
    val costMatrix = structureND(ShapeND(series1.size, series2.size)) {
            (row, col) -> abs(series1[row] - series2[col])
    }
    // Formula: costMatrix[i, j] = euqlidNorm(series1(i), series2(j)) +
    // min(costMatrix[i - 1, j], costMatrix[i, j - 1], costMatrix[i - 1, j - 1]).
    for ( (row, col) in costMatrix.indices) {
        costMatrix[row, col] += when {
            // There is special cases for i = 0 or j = 0.
            row == 0 && col == 0 -> 0.0
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
    val alignMatrix = structureND(ShapeND(series1.size, series2.size)) {(_, _) -> 0.0}
    val indexes = PathIndices(series1.size - 1, series2.size - 1)

    with(indexes) {
        alignMatrix[id_x, id_y] = costMatrix[id_x, id_y]
        cost += costMatrix[id_x, id_y]
        pathLength++
        while (id_x != 0 || id_y != 0) {
            when {
                id_x == 0 || costMatrix[id_x, id_y] == costMatrix[id_x, id_y - 1] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(DtwOffset.LEFT)
                }
                id_y == 0 || costMatrix[id_x, id_y] == costMatrix[id_x - 1, id_y] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(DtwOffset.BOTTOM)
                }
                costMatrix[id_x, id_y] == costMatrix[id_x - 1, id_y - 1] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(DtwOffset.DIAGONAL)
                }
            }
            alignMatrix[id_x, id_y] = costMatrix[id_x, id_y]
            cost += costMatrix[id_x, id_y]
            pathLength++
        }
        cost /= pathLength
    }
    return DynamicTimeWarpingData(cost, alignMatrix)
}


