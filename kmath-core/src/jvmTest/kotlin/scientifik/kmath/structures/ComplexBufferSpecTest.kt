package scientifik.kmath.structures

import org.junit.Test
import scientifik.kmath.operations.Complex
import kotlin.test.assertEquals

class ComplexBufferSpecTest {
    @Test
    fun testComplexBuffer() {
        val buffer = MutableBuffer.complex(20){Complex(it.toDouble(), -it.toDouble())}
        assertEquals(Complex(5.0, -5.0), buffer[5])
    }
}