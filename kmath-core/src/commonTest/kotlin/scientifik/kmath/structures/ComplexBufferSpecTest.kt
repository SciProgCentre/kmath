package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.complex
import kotlin.test.Test
import kotlin.test.assertEquals

class ComplexBufferSpecTest {
    @Test
    fun testComplexBuffer() {
        val buffer = Buffer.complex(20) { Complex(it.toDouble(), -it.toDouble()) }
        assertEquals(Complex(5.0, -5.0), buffer[5])
    }
}
