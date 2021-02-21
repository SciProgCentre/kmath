package space.kscience.kmath.complex

import space.kscience.kmath.structures.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class ComplexBufferSpecTest {
    @Test
    fun testComplexBuffer() {
        val buffer = Buffer.complex(20) { Complex(it.toDouble(), -it.toDouble()) }
        assertEquals(Complex(5.0, -5.0), buffer[5])
    }
}