/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.nd.*
import space.kscience.kmath.nd.DoubleBufferND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.ndAlgebra
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.math.abs

public const val LEFT_OFFSET : Int = -1
public const val BOTTOM_OFFSET : Int = 1
public const val DIAGONAL_OFFSET : Int = 0



// TODO: Change container for alignMatrix to kmath special ND structure
public data class DynamicTimeWarpingData(val totalCost : Double = 0.0,
                                     val alignMatrix : IntBufferND = IntRingOpsND.structureND(ShapeND(0, 0)) { (i, j) -> 0}) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DynamicTimeWarpingData

        if (totalCost != other.totalCost) return false

        return true
    }
}

/**
 * costMatrix calculates special matrix of costs alignment for two series.
 * Formula: costMatrix[i, j] = euqlidNorm(series1(i), series2(j)) + min(costMatrix[i - 1, j],
 *                                                                      costMatrix[i, j - 1],
 *                                                                      costMatrix[i - 1, j - 1]).
 * There is special cases for i = 0 or j = 0.
 */

public fun costMatrix(series1 : DoubleBuffer, series2 : DoubleBuffer) : DoubleBufferND {
    val dtwMatrix = DoubleField.ndAlgebra.structureND(ShapeND(series1.size, series2.size)) {
            (row, col) -> abs(series1[row] - series2[col])
    }
    for ( (row, col) in dtwMatrix.indices) {
        dtwMatrix[row, col] += when {
            row == 0 && col == 0 -> 0.0
            row == 0 -> dtwMatrix[row, col - 1]
            col == 0 -> dtwMatrix[row - 1, col]
            else -> minOf(
                dtwMatrix[row, col - 1],
                dtwMatrix[row - 1, col],
                dtwMatrix[row - 1, col - 1]
            )
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
    val alignMatrix: IntBufferND = IntRingOpsND.structureND(ShapeND(series1.size, series2.size)) {(row, col) -> 0}
    val indexes = PathIndices(alignMatrix.indices.shape.first() - 1,  alignMatrix.indices.shape.last() - 1)

    with(indexes) {
        alignMatrix[id_x, id_y] = 1
        cost += costMatrix[id_x, id_y]
        pathLength++
        while (id_x != 0 || id_y != 0) {
            when {
                id_x == 0 || costMatrix[id_x, id_y] == costMatrix[id_x, id_y - 1] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(LEFT_OFFSET)
                }
                id_y == 0 || costMatrix[id_x, id_y] == costMatrix[id_x - 1, id_y] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(BOTTOM_OFFSET)
                }
                costMatrix[id_x, id_y] == costMatrix[id_x - 1, id_y - 1] + abs(series1[id_x] - series2[id_y]) -> {
                    moveOption(DIAGONAL_OFFSET)
                }
            }
            alignMatrix[id_x, id_y] = 1
            cost += costMatrix[id_x, id_y]
            pathLength++
        }
        cost /= pathLength
    }
    return DynamicTimeWarpingData(cost, alignMatrix)
}

