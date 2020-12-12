package kscience.kmath.linear

import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.NDStructure
import kscience.kmath.structures.as2D
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixTest {
    @Test
    fun testTranspose() {
        val matrix = MatrixContext.real.one(3, 3)
        val transposed = matrix.transpose()
        assertEquals(matrix, transposed)
    }

    @Test
    fun testBuilder() {
        val matrix = Matrix.build(2, 3)(
            1.0, 0.0, 0.0,
            0.0, 1.0, 2.0
        )

        assertEquals(2.0, matrix[1, 2])
    }

    @Test
    fun testMatrixExtension() {
        val transitionMatrix: Matrix<Double> = VirtualMatrix(6, 6) { row, col ->
            when {
                col == 0 -> .50
                row + 1 == col -> .50
                row == 5 && col == 5 -> 1.0
                else -> 0.0
            }
        }

        infix fun Matrix<Double>.pow(power: Int): Matrix<Double> {
            var res = this
            repeat(power - 1) {
                res = RealMatrixContext.invoke { res dot this@pow }
            }
            return res
        }

        val toTenthPower = transitionMatrix pow 10
    }

    @Test
    fun test2DDot() {
        val firstMatrix = NDStructure.auto(2, 3) { (i, j) -> (i + j).toDouble() }.as2D()
        val secondMatrix = NDStructure.auto(3, 2) { (i, j) -> (i + j).toDouble() }.as2D()

        MatrixContext.real.run {
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
}
