package scientifik.kmath.linear

import scientifik.kmath.structures.Matrix
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixTest {

    @Test
    fun testSum() {
        val vector1 = Vector.real(5) { it.toDouble() }
        val vector2 = Vector.real(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2])
    }

    @Test
    fun testVectorToMatrix() {
        val vector = Vector.real(5) { it.toDouble() }
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
        val vector1 = Vector.real(5) { it.toDouble() }
        val vector2 = Vector.real(5) { 5 - it.toDouble() }

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
}