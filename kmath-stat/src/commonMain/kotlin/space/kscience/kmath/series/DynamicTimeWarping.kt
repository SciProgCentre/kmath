/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.abs

public const val LEFT_OFFSET : Int = -1
public const val BOTTOM_OFFSET : Int = 1
public const val DIAGONAL_OFFSET : Int = 0

// TODO: Change container for alignMatrix to kmath special ND structure
public data class DynamicTimeWarpingData(val totalCost : Double = 0.0,
                                     val alignMatrix : Array<BooleanArray> = Array(0) {BooleanArray(0)}) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DynamicTimeWarpingData

        if (totalCost != other.totalCost) return false
        if (!alignMatrix.contentDeepEquals(other.alignMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totalCost.hashCode()
        result = 31 * result + alignMatrix.contentDeepHashCode()
        return result
    }
}

/**
 * costMatrix calculates special matrix of costs alignment for two series.
 * Formula: costMatrix[i, j] = euqlidNorm(series1(i), series2(j)) + min(costMatrix[i - 1, j],
 *                                                                      costMatrix[i, j - 1],
 *                                                                      costMatrix[i - 1, j - 1]).
 * There is special cases for i = 0 or j = 0.
 */

public fun costMatrix(series1 : DoubleBuffer, series2 : DoubleBuffer) : Array<DoubleArray> {
    val dtwMatrix: Array<DoubleArray> = Array(series1.size){ row ->
        DoubleArray(series2.size) { col ->
            abs(series1[row] - series2[col])
        }
    }

    for (i in dtwMatrix.indices) {
        for (j in dtwMatrix[i].indices) {
            dtwMatrix[i][j] += when {
                i == 0 && j == 0 -> 0.0
                i == 0 -> dtwMatrix[i][j-1]
                j == 0 -> dtwMatrix[i-1][j]
                else -> minOf(
                    dtwMatrix[i][j-1],
                    dtwMatrix[i-1][j],
                    dtwMatrix[i-1][j-1]
                )
            }
        }
    }
    return dtwMatrix
}

/**
 * PathIndices class for better code perceptibility.
 * Special fun moveOption represent offset for indices. Arguments of this function
 * is flags for bottom, diagonal or left offsets respectively.
 */

public data class PathIndices (var id_x: Int, var id_y: Int) {
    public fun moveOption (direction: Int) {
        when(direction) {
            BOTTOM_OFFSET -> id_x--
            DIAGONAL_OFFSET -> {
                id_x--
                id_y--
            }
            LEFT_OFFSET -> id_y--
            else -> throw Exception("There is no such offset flag!")
        }
    }
}

/**
 * Final DTW method realization. Returns alignment matrix
 * for two series comparing and penalty for this alignment.
 */

public fun dynamicTimeWarping(series1 : DoubleBuffer, series2 : DoubleBuffer) : DynamicTimeWarpingData {
    var cost = 0.0
    var pathLength = 0
    val costMatrix = costMatrix(series1, series2)
    val alignMatrix : Array<BooleanArray> = Array(costMatrix.size) { BooleanArray(costMatrix.first().size) }
    val indexes = PathIndices(alignMatrix.lastIndex,  alignMatrix.last().lastIndex)

    with(indexes) {
        alignMatrix[id_x][id_y] = true
        cost += costMatrix[id_x][id_y]
        pathLength++
        while (id_x != 0 || id_y != 0) {
            when {
                id_x == 0 || costMatrix[id_x][id_y] == costMatrix[id_x][id_y - 1] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(LEFT_OFFSET)
                }
                id_y == 0 || costMatrix[id_x][id_y] == costMatrix[id_x - 1][id_y] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(BOTTOM_OFFSET)
                }
                costMatrix[id_x][id_y] == costMatrix[id_x - 1][id_y - 1] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(DIAGONAL_OFFSET)
                }
            }
            alignMatrix[id_x][id_y] = true
            cost += costMatrix[id_x][id_y]
            pathLength++
        }
        cost /= pathLength
    }
    return DynamicTimeWarpingData(cost, alignMatrix)
}

