package scientifik.kmath.structures

import org.junit.Test
import scientifik.kmath.operations.Complex
import kotlin.test.assertEquals

class ComplexBufferSpecTest {
    @Test
    fun testComplexBuffer() {
        val buffer = Complex.createBuffer(20)
        (0 until 20).forEach {
            buffer[it] = Complex(it.toDouble(), -it.toDouble())
        }

        assertEquals(Complex(5.0, -5.0), buffer[5])
    }
}