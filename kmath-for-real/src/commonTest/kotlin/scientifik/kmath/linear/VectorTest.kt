package scientifik.kmath.linear

import scientifik.kmath.operations.invoke
import scientifik.kmath.real.RealVector
import kotlin.test.Test
import kotlin.test.assertEquals

class VectorTest {
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
    fun testDot() {
        val vector1 = RealVector(5) { it.toDouble() }
        val vector2 = RealVector(5) { 5 - it.toDouble() }
        val matrix1 = vector1.asMatrix()
        val matrix2 = vector2.asMatrix().transpose()
        val product = MatrixContext.real { matrix1 dot matrix2 }
        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }
}
