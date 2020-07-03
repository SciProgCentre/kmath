package scientifik.kmath.operations

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ComplexFieldTest {
    @Test
    fun testAddition() {
        assertEquals(Complex(42, 42), ComplexField { Complex(16, 16) + Complex(26, 26) })
        assertEquals(Complex(42, 16), ComplexField { Complex(16, 16) + 26 })
        assertEquals(Complex(42, 16), ComplexField { 26 + Complex(16, 16) })
    }

    @Test
    fun testSubtraction() {
        assertEquals(Complex(42, 42), ComplexField { Complex(86, 55) - Complex(44, 13) })
        assertEquals(Complex(42, 56), ComplexField { Complex(86, 56) - 44 })
        assertEquals(Complex(42, 56), ComplexField { 86 - Complex(44, -56) })
    }

    @Test
    fun testMultiplication() {
        assertEquals(Complex(42, 42), ComplexField { Complex(4.2, 0) * Complex(10, 10) })
        assertEquals(Complex(42, 21), ComplexField { Complex(4.2, 2.1) * 10 })
        assertEquals(Complex(42, 21), ComplexField { 10 * Complex(4.2, 2.1) })
    }

    @Test
    fun testDivision() {
        assertEquals(Complex(42, 42), ComplexField { Complex(0, 168) / Complex(2, 2) })
        assertEquals(Complex(42, 56), ComplexField { Complex(86, 56) - 44 })
        assertEquals(Complex(42, 56), ComplexField { 86 - Complex(44, -56) })
        assertEquals(Complex(Double.NaN, Double.NaN), ComplexField { Complex(1, 1) / Complex(Double.NaN, Double.NaN) })
        assertEquals(Complex(Double.NaN, Double.NaN), ComplexField { Complex(1, 1) / Complex(0, 0) })
    }

    @Test
    fun testSine() {
        assertEquals(Complex(1.2246467991473532E-16, 0), ComplexField { sin(PI.toComplex()) })
        assertEquals(Complex(0, 11.548739357257748), ComplexField { sin(i * PI.toComplex()) })
        assertEquals(Complex(0, 1.1752011936438014), ComplexField { sin(i) })
    }

    @Test
    fun testArcsine() {
        assertEquals(Complex(0, -0.0), ComplexField { asin(zero) })
    }
}
