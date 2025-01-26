/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@UnstableKMathAPI
@OptIn(PerformancePitfall::class)
@Suppress("UNUSED_VARIABLE")
class ParallelMatrixTest {

    @Test
    fun testTranspose() = Float64Field.linearSpace.parallel {
        val matrix = one(3, 3)
        val transposed = matrix.transposed()
        assertTrue { StructureND.contentEquals(matrix, transposed) }
    }

    @Test
    fun testBuilder() = Float64Field.linearSpace.parallel {
        val matrix = MatrixBuilder(2, 3).fill(
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0
        )

        assertEquals(2.0, matrix[1, 2])
    }

    @Test
    fun testMatrixExtension() = Float64Field.linearSpace.parallel {
        val transitionMatrix: Matrix<Float64> = VirtualMatrix(6, 6) { row, col ->
            when {
                col == 0 -> .50
                row + 1 == col -> .50
                row == 5 && col == 5 -> 1.0
                else -> 0.0
            }
        }

        infix fun Matrix<Float64>.pow(power: Int): Matrix<Float64> {
            var res = this
            repeat(power - 1) {
                res = res dot this@pow
            }
            return res
        }

        val toTenthPower = transitionMatrix pow 10
    }

    @Test
    fun test2DDot() = Float64Field.linearSpace.parallel {
        val firstMatrix = buildMatrix(2, 3) { i, j -> (i + j).toDouble() }
        val secondMatrix = buildMatrix(3, 2) { i, j -> (i + j).toDouble() }

//            val firstMatrix = produce(2, 3) { i, j -> (i + j).toDouble() }
//            val secondMatrix = produce(3, 2) { i, j -> (i + j).toDouble() }
        val result = firstMatrix dot secondMatrix
        assertEquals(2, result.rowNum)
        assertEquals(2, result.colNum)
        assertEquals(8.0, result[0, 1])
        assertEquals(8.0, result[1, 0])
        assertEquals(14.0, result[1, 1])
    }
}
