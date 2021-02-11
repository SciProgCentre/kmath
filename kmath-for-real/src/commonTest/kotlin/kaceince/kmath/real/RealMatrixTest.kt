package kaceince.kmath.real

import kscience.kmath.linear.Matrix
import kscience.kmath.linear.build
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.real.*
import kscience.kmath.structures.contentEquals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RealMatrixTest {
    @Test
    fun testSum() {
        val m = realMatrix(10, 10) { i, j -> (i + j).toDouble() }
        assertEquals(m.sum(), 900.0)
        assertEquals(m.average(), 9.0)
    }

    @Test
    fun testSequenceToMatrix() {
        val m = Sequence<DoubleArray> {
            listOf(
                DoubleArray(10) { 10.0 },
                DoubleArray(10) { 20.0 },
                DoubleArray(10) { 30.0 }).iterator()
        }.toMatrix()
        assertEquals(m.sum(), 20.0 * 30)
    }

    @Test
    fun testRepeatStackVertical() {
        val matrix1 = Matrix.build(2, 3)(
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0
        )
        val matrix2 = Matrix.build(6, 3)(
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0,
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0,
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0
        )
        assertEquals(matrix2, matrix1.repeatStackVertical(3))
    }

    @Test
    fun testMatrixAndDouble() {
        val matrix1 = Matrix.build(2, 3)(
            1.0, 0.0, 3.0,
            4.0, 6.0, 2.0
        )
        val matrix2 = (matrix1 * 2.5 + 1.0 - 2.0) / 2.0
        val expectedResult = Matrix.build(2, 3)(
            0.75, -0.5, 3.25,
            4.5, 7.0, 2.0
        )
        assertEquals(matrix2, expectedResult)
    }

    @Test
    fun testDoubleAndMatrix() {
        val matrix1 = Matrix.build(2, 3)(
            1.0, 0.0, 3.0,
            4.0, 6.0, 2.0
        )
        val matrix2 = 20.0 - (10.0 + (5.0 * matrix1))
        //val matrix2 = 10.0 + (5.0 * matrix1)
        val expectedResult = Matrix.build(2, 3)(
            5.0, 10.0, -5.0,
            -10.0, -20.0, 0.0
        )
        assertEquals(matrix2, expectedResult)
    }

    @Test
    fun testSquareAndPower() {
        val matrix1 = Matrix.build(2, 3)(
            -1.0, 0.0, 3.0,
            4.0, -6.0, -2.0
        )
        val matrix2 = Matrix.build(2, 3)(
            1.0, 0.0, 9.0,
            16.0, 36.0, 4.0
        )
        val matrix3 = Matrix.build(2, 3)(
            -1.0, 0.0, 27.0,
            64.0, -216.0, -8.0
        )
        assertEquals(matrix1.square(), matrix2)
        assertEquals(matrix1.pow(3), matrix3)
    }

    @OptIn(UnstableKMathAPI::class)
    @Test
    fun testTwoMatrixOperations() {
        val matrix1 = Matrix.build(2, 3)(
            -1.0, 0.0, 3.0,
            4.0, -6.0, 7.0
        )
        val matrix2 = Matrix.build(2, 3)(
            1.0, 0.0, 3.0,
            4.0, 6.0, -2.0
        )
        val result = matrix1 * matrix2 + matrix1 - matrix2
        val expectedResult = Matrix.build(2, 3)(
            -3.0, 0.0, 9.0,
            16.0, -48.0, -5.0
        )
        assertEquals(result, expectedResult)
    }

    @Test
    fun testColumnOperations() {
        val matrix1 = Matrix.build(2, 4)(
            -1.0, 0.0, 3.0, 15.0,
            4.0, -6.0, 7.0, -11.0
        )
        val matrix2 = Matrix.build(2, 5)(
            -1.0, 0.0, 3.0, 15.0, -1.0,
            4.0, -6.0, 7.0, -11.0, 4.0
        )
        val col1 = Matrix.build(2, 1)(0.0, -6.0)
        val cols1to2 = Matrix.build(2, 2)(
            0.0, 3.0,
            -6.0, 7.0
        )

        assertEquals(matrix1.appendColumn { it[0] }, matrix2)
        assertEquals(matrix1.extractColumn(1), col1)
        assertEquals(matrix1.extractColumns(1..2), cols1to2)
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
    fun testAllElementOperations() {
        val matrix1 = Matrix.build(2, 4)(
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
