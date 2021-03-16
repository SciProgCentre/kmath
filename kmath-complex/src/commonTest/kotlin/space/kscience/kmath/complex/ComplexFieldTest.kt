/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.pi
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ComplexFieldTest {
    //    TODO make verifier classes available in this source set
    //    @Test
    //    fun verify() = ComplexField { FieldVerifier(this, 42.0 * i, 66.0 + 28 * i, 2.0 + 0 * i, 5).verify() }

    @Test
    fun testAddition() {
        assertEquals(Complex(42, 42), ComplexIntRing { Complex(16, 16) + Complex(26, 26) })
        assertEquals(Complex(42, 16), ComplexIntRing { Complex(16, 16) + 26 })
        assertEquals(Complex(42, 16), ComplexIntRing { 26 + Complex(16, 16) })
    }

    @Test
    fun testSubtraction() {
        assertEquals(Complex(42, 42), ComplexIntRing { Complex(86, 55) - Complex(44, 13) })
        assertEquals(Complex(42, 56), ComplexIntRing { Complex(86, 56) - 44 })
        assertEquals(Complex(42, 56), ComplexIntRing { 86 - Complex(44, -56) })
    }

    @Test
    fun testMultiplication() {
        assertEquals(Complex(42.0, 42.0), ComplexDoubleField { Complex(4.2, 0.0) * Complex(10.0, 10.0) })
        assertEquals(Complex(42.0, 21.0), ComplexDoubleField { Complex(4.2, 2.1) * 10 })
        assertEquals(Complex(42.0, 21.0), ComplexDoubleField { 10 * Complex(4.2, 2.1) })
    }

    @Test
    fun testDivision() {
        assertEquals(Complex(42.0, 42.0), ComplexDoubleField { Complex(0.0, 168.0) / Complex(2.0, 2.0) })
        assertEquals(Complex(42.0, 56.0), ComplexDoubleField { Complex(86.0, 56.0) - 44.0 })
        assertEquals(Complex(42.0, 56.0), ComplexDoubleField { 86.0 - Complex(44.0, -56.0) })
    }

    @Test
    fun testSine() {
        assertEquals(ComplexDoubleField { i * sinh(one) }, ComplexDoubleField { sin(i) })
        assertEquals(ComplexDoubleField { i * sinh(pi) }, ComplexDoubleField { sin(i * pi) })
    }

    @Test
    fun testInverseSine() = ComplexDoubleField {
        assertEquals(norm(zero), norm(asin(zero)), 1e-10)
        assertEquals(norm(i * asinh(one)), norm(i * asinh(one)), 1e-10)
    }

    @Test
    fun testInverseHyperbolicSine() {
        assertEquals(ComplexDoubleField { i * pi / 2 }, ComplexDoubleField { asinh(i) })
    }

    @Test
    fun testPower() {
        assertEquals(ComplexDoubleField.zero, ComplexDoubleField { zero pow 2 })
        assertEquals(ComplexDoubleField.zero, ComplexDoubleField { zero pow 2 })

        assertEquals(
            ComplexDoubleField { i * 8 }.let { it.im.toInt() to it.re.toInt() },
            ComplexDoubleField { Complex(2.0, 2.0) pow 2 }.let { it.im.toInt() to it.re.toInt() },
        )
    }

    @Test
    fun testNorm() {
        assertEquals(2.0, ComplexDoubleField { norm(number(2.0) * i) })
    }

    @Test
    fun conjugate() = ComplexDoubleField {
        assertEquals(Complex(0.0, 42.0), Complex(0.0, -42.0).conjugate)
    }

    @Test
    fun reciprocal() = ComplexDoubleField {
        assertEquals(norm(Complex(0.5, 0.0)), norm(Complex(2.0, 0.0).reciprocal), 1e-10)
    }

    @Test
    fun polar() {
        val num = Complex(0.5, 2.5)
        val theta = ComplexDoubleField { num.theta }
        val r = ComplexDoubleField { num.r }

        DoubleField {
            assertEquals(cos(theta), num.re / r, 1e-10)
            assertEquals(sin(theta), num.im / r, 1e-10)
        }
    }
}
