package scientifik.kmath.linear

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
        val matrix = vector.toMatrix()
        assertEquals(4.0, matrix[4, 0])
    }

    @Test
    fun testTranspose() {
        val matrix = GenericMatrixContext.real.one(3, 3)
        val transposed = matrix.transpose()
        assertEquals((matrix as BufferMatrix).buffer, (transposed as BufferMatrix).buffer)
        assertEquals(matrix, transposed)
    }


    @Test
    fun testDot() {
        val vector1 = Vector.real(5) { it.toDouble() }
        val vector2 = Vector.real(5) { 5 - it.toDouble() }

        val matrix1 = vector1.toMatrix()
        val matrix2 = vector2.toMatrix().transpose()
        val product = GenericMatrixContext.real.run { matrix1 dot matrix2 }


        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }
}