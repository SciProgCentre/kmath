/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.matrix
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.testutils.contentEquals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(PerformancePitfall::class)
fun <T : Any> assertMatrixEquals(expected: StructureND<T>, actual: StructureND<T>) {
    assertTrue { StructureND.contentEquals(expected, actual) }
}

@UnstableKMathAPI
internal class DoubleMatrixTest {
    @Test
    fun testSum() {
        val m = realMatrix(10, 10) { i, j -> (i + j).toDouble() }
        assertEquals(m.sum(), 900.0)
        assertEquals(m.average(), 9.0)
    }

    @Test
    fun testSequenceToMatrix() {
        val m = Sequence {
            listOf(
                DoubleArray(10) { 10.0 },
                DoubleArray(10) { 20.0 },
                DoubleArray(10) { 30.0 }).iterator()
        }.toMatrix()
        assertEquals(m.sum(), 20.0 * 30)
    }

    @Test
    fun testRepeatStackVertical() {
        val matrix1 = realMatrix(2, 3)(
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0
        )
        val matrix2 = realMatrix(6, 3)(
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0,
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0,
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0
        )
        assertMatrixEquals(matrix2, matrix1.repeatStackVertical(3))
    }

    @Test
    fun testMatrixAndDouble() = Double.algebra.linearSpace.run {
        val matrix1 = realMatrix(2, 3)(
            1.0, 0.0, 3.0,
            4.0, 6.0, 2.0
        )
        val matrix2 = (matrix1 * 2.5 + 1.0 - 2.0) / 2.0
        val expectedResult = matrix(2, 3)(
            0.75, -0.5, 3.25,
            4.5, 7.0, 2.0
        )
        assertMatrixEquals(matrix2, expectedResult)
    }

    @Test
    fun testDoubleAndMatrix() {
        val matrix1 = realMatrix(2, 3)(
            1.0, 0.0, 3.0,
            4.0, 6.0, 2.0
        )
        val matrix2 = 20.0 - (10.0 + (5.0 * matrix1))
        //val matrix2 = 10.0 + (5.0 * matrix1)
        val expectedResult = realMatrix(2, 3)(
            5.0, 10.0, -5.0,
            -10.0, -20.0, 0.0
        )
        assertMatrixEquals(matrix2, expectedResult)
    }

    @Test
    fun testSquareAndPower() {
        val matrix1 = realMatrix(2, 3)(
            -1.0, 0.0, 3.0,
            4.0, -6.0, -2.0
        )
        val matrix2 = realMatrix(2, 3)(
            1.0, 0.0, 9.0,
            16.0, 36.0, 4.0
        )
        val matrix3 = realMatrix(2, 3)(
            -1.0, 0.0, 27.0,
            64.0, -216.0, -8.0
        )
        assertMatrixEquals(matrix1.square(), matrix2)
        assertMatrixEquals(matrix1.pow(3), matrix3)
    }

    @OptIn(UnstableKMathAPI::class)
    @Test
    fun testTwoMatrixOperations() {
        val matrix1 = realMatrix(2, 3)(
            -1.0, 0.0, 3.0,
            4.0, -6.0, 7.0
        )
        val matrix2 = realMatrix(2, 3)(
            1.0, 0.0, 3.0,
            4.0, 6.0, -2.0
        )
        val result = matrix1 * matrix2 + matrix1 - matrix2
        val expectedResult = realMatrix(2, 3)(
            -3.0, 0.0, 9.0,
            16.0, -48.0, -5.0
        )
        assertMatrixEquals(result, expectedResult)
    }

    @Test
    fun testColumnOperations() {
        val matrix1 = realMatrix(2, 4)(
            -1.0, 0.0, 3.0, 15.0,
            4.0, -6.0, 7.0, -11.0
        )
        val matrix2 = realMatrix(2, 5)(
            -1.0, 0.0, 3.0, 15.0, -1.0,
            4.0, -6.0, 7.0, -11.0, 4.0
        )
        val col1 = realMatrix(2, 1)(0.0, -6.0)
        val cols1to2 = realMatrix(2, 2)(
            0.0, 3.0,
            -6.0, 7.0
        )

        assertMatrixEquals(matrix1.appendColumn { it[0] }, matrix2)
        assertMatrixEquals(matrix1.extractColumn(1), col1)
        assertMatrixEquals(matrix1.extractColumns(1..2), cols1to2)
        //equals should never be called on buffers
        assertTrue {
            matrix1.sumByColumn().contentEquals(3.0, -6.0, 10.0, 4.0)
        } //assertEquals(matrix1.sumByColumn(), DoubleBuffer(3.0, -6.0, 10.0, 4.0))
        assertTrue {
            matrix1.minByColumn().contentEquals(-1.0, -6.0, 3.0, -11.0)
        } //assertEquals(matrix1.minByColumn(), DoubleBuffer(-1.0, -6.0, 3.0, -11.0))
        assertTrue {
            matrix1.maxByColumn().contentEquals(4.0, 0.0, 7.0, 15.0)
        } //assertEquals(matrix1.maxByColumn(), DoubleBuffer(4.0, 0.0, 7.0, 15.0))
        assertTrue {
            matrix1.averageByColumn().contentEquals(1.5, -3.0, 5.0, 2.0)
        } //assertEquals(matrix1.averageByColumn(), DoubleBuffer(1.5, -3.0, 5.0, 2.0))
    }

    @Test
    fun testAllElementOperations() = Double.algebra.linearSpace.run {
        val matrix1 = matrix(2, 4)(
            -1.0, 0.0, 3.0, 15.0,
            4.0, -6.0, 7.0, -11.0
        )
        assertEquals(matrix1.sum(), 11.0)
        assertEquals(matrix1.min(), -11.0)
        assertEquals(matrix1.max(), 15.0)
        assertEquals(matrix1.average(), 1.375)
    }

//    fun printMatrix(m: Matrix<Double>) {
//        for (row in 0 until m.shape[0]) {
//            for (col in 0 until m.shape[1]) {
//                print(m[row, col])
//                print(" ")
//            }
//            println()
//        }
//    }

}
