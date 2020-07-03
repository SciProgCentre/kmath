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
        assertEquals(ComplexField { i * sinh(one) }, ComplexField { sin(i) })
        assertEquals(ComplexField { i * sinh(PI.toComplex()) }, ComplexField { sin(i * PI.toComplex()) })
    }

    @Test
    fun testInverseSine() {
        assertEquals(Complex(0, -0.0), ComplexField { asin(zero) })

        assertEquals(ComplexField { i * asinh(one) }.let { it.im.toInt() to it.re.toInt() },
            ComplexField { asin(i) }.let { it.im.toInt() to it.re.toInt() })
    }

    @Test
    fun testInverseHyperbolicSine() {
        assertEquals(
            ComplexField { i * PI.toComplex() / 2 }.let { it.im.toInt() to it.re.toInt() },
            ComplexField { asinh(i) }.let { it.im.toInt() to it.re.toInt() })
    }

    @Test
    fun testPower() {
        assertEquals(ComplexField.zero, ComplexField { zero pow 2 })
        assertEquals(ComplexField.zero, ComplexField { zero pow 2 })

        assertEquals(
            ComplexField { i * 8 }.let { it.im.toInt() to it.re.toInt() },
            ComplexField { Complex(2, 2) pow 2 }.let { it.im.toInt() to it.re.toInt() })
    }
}
