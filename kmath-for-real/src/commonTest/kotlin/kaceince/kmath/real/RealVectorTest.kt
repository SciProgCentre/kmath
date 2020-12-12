package kaceince.kmath.real

import kscience.kmath.linear.*
import kscience.kmath.operations.invoke
import kscience.kmath.real.RealVector
import kscience.kmath.real.plus
import kscience.kmath.structures.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RealVectorTest {
    @Test
    fun testSum() {
        val vector1 = Buffer.real(5) { it.toDouble() }
        val vector2 =  Buffer.real(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2])
    }

    @Test
    fun testVectorToMatrix() {
        val vector =  Buffer.real(5) { it.toDouble() }
        val matrix = vector.asMatrix()
        assertEquals(4.0, matrix[4, 0])
    }

    @Test
    fun testDot() {
        val vector1 =  Buffer.real(5) { it.toDouble() }
        val vector2 =  Buffer.real(5) { 5 - it.toDouble() }
        val matrix1 = vector1.asMatrix()
        val matrix2 = vector2.asMatrix().transpose()
        val product = MatrixContext.real { matrix1 dot matrix2 }
        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }
}
