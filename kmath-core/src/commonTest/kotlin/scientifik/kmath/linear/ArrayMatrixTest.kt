package scientifik.kmath.linear

import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayMatrixTest {

    @Test
    fun testSum() {
        val vector1 = realVector(5) { it.toDouble() }
        val vector2 = realVector(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2])
    }

    @Test
    fun testVectorToMatrix() {
        val vector = realVector(5) { it.toDouble() }
        val matrix = vector.toMatrix()
        assertEquals(4.0, matrix[4, 0])
    }


    @Test
    fun testDot() {
        val vector1 = realVector(5) { it.toDouble() }
        val vector2 = realVector(5) { 5 - it.toDouble() }
        val product = vector1.toMatrix() dot (vector2.toMatrix().transpose())


        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }
}