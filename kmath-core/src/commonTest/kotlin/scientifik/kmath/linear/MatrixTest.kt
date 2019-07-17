package scientifik.kmath.linear

import scientifik.kmath.structures.Matrix
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixTest {

    @Test
    fun testSum() {
        val vector1 = RealVector(5) { it.toDouble() }
        val vector2 = RealVector(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2])
    }

    @Test
    fun testVectorToMatrix() {
        val vector = RealVector(5) { it.toDouble() }
        val matrix = vector.asMatrix()
        assertEquals(4.0, matrix[4, 0])
    }

    @Test
    fun testTranspose() {
        val matrix = MatrixContext.real.one(3, 3)
        val transposed = matrix.transpose()
        assertEquals(matrix, transposed)
    }


    @Test
    fun testDot() {
        val vector1 = RealVector(5) { it.toDouble() }
        val vector2 = RealVector(5) { 5 - it.toDouble() }

        val matrix1 = vector1.asMatrix()
        val matrix2 = vector2.asMatrix().transpose()
        val product = MatrixContext.real.run { matrix1 dot matrix2 }


        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }

    @Test
    fun testBuilder() {
        val matrix = Matrix.build<Double>(2, 3)(
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
                res = res dot this
            }
            return res
        }

        val toTenthPower = transitionMatrix pow 10
    }
}