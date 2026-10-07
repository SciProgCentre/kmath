/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.testutils.FieldVerifier
import kotlin.test.*

class BigDecimalTest {

    @Test
    fun testFieldVerifier() {
        FieldVerifier(
            BigDecimalField,
            40.toBigDecimal(),
            25.toBigDecimal(),
            2.toBigDecimal(),
            5
        ).verify()
    }

    @Test
    fun testExactDecimalArithmetic() = with(BigDecimalField) {
        assertNotEquals(0.3, 0.1 + 0.2)
        val a = 0.1.toBigDecimal()
        val b = 0.2.toBigDecimal()
        val c = 0.3.toBigDecimal()
        assertEquals(c, a + b)
        assertEquals(one * 0.3, one * 0.1 + one * 0.2)
    }

    @Test
    fun testBasicOperations() = with(BigDecimalField) {
        val x = 12.34.toBigDecimal()
        val y = 5.66.toBigDecimal()

        assertEquals(18.0.toBigDecimal(), x + y)
        assertEquals(6.68.toBigDecimal(), x - y)
        assertEquals(69.8444.toBigDecimal(), x * y)
        assertEquals((-12.34).toBigDecimal(), -x)

        val oneHalf = 1.toBigDecimal() / 2.toBigDecimal()
        assertEquals(0.5.toBigDecimal(), oneHalf)

        val divExact = 6.toBigDecimal() / 3.toBigDecimal()
        assertEquals(2.toBigDecimal(), divExact)
    }

    @Test
    fun testComparisonAndEquality() {
        val a = BigDecimal(1, 0)
        val b = BigDecimal(10, 1)
        val c = BigDecimal(100, 2)
        val d = BigDecimal(2, 0)

        assertEquals(a, b)
        assertEquals(b, c)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(b.hashCode(), c.hashCode())

        assertTrue(a < d)
        assertTrue(d > a)
        assertEquals(0, a.compareTo(b))
    }

    @Test
    fun testParsing() = with(BigDecimalField) {
        assertNull("".parseBigDecimal())
        assertNull("+".parseBigDecimal())
        assertNull("-".parseBigDecimal())
        assertNull(".".parseBigDecimal())
        assertNull("abc".parseBigDecimal())
        assertNull("1.2.3".parseBigDecimal())

        assertEquals(BigDecimal(12345, 2), "123.45".parseBigDecimal())
        assertEquals(BigDecimal(-12345, 2), "-123.45".parseBigDecimal())
        assertEquals(BigDecimal(12345, 2), "+123.45".parseBigDecimal())
        assertEquals(BigDecimal(5, 1), ".5".parseBigDecimal())
        assertEquals(BigDecimal(5, 0), "5.".parseBigDecimal())
        assertEquals(BigDecimal(12345, 2), "1_2_3.4_5".parseBigDecimal())

        assertEquals(BigDecimal(123, -2), "1.23e4".parseBigDecimal())
        assertEquals(BigDecimal(123, 6), "1.23e-4".parseBigDecimal())
        assertEquals(BigDecimal(123, 6), "1.23E-4".parseBigDecimal())
        assertEquals(BigDecimal(100, 0), "1e2".parseBigDecimal())
    }

    @Test
    fun testToString() {
        assertEquals("0", BigDecimal.ZERO.toString())
        assertEquals("123.45", BigDecimal(12345, 2).toString())
        assertEquals("-123.45", BigDecimal(-12345, 2).toString())
        assertEquals("0.0012", BigDecimal(12, 4).toString())
        assertEquals("-0.0012", BigDecimal(-12, 4).toString())
        assertEquals("12000", BigDecimal(12, -3).toString())
        assertEquals("-12000", BigDecimal(-12, -3).toString())
    }

    @Test
    fun testPow() = with(BigDecimalField) {
        val num = 2.toBigDecimal()
        assertEquals(1.toBigDecimal(), num.pow(0))
        assertEquals(2.toBigDecimal(), num.pow(1))
        assertEquals(4.toBigDecimal(), num.pow(2))
        assertEquals(8.toBigDecimal(), num.pow(3))
        assertEquals(0.5.toBigDecimal(), num.pow(-1))
        assertEquals(0.25.toBigDecimal(), num.pow(-2))

        assertEquals(4.toBigDecimal(), power(num, 2))
        assertEquals(0.25.toBigDecimal(), power(num, -2))
    }

    @Test
    fun testTranscendentalFunctions() = with(BigDecimalField) {
        val zero = 0.toBigDecimal()
        val one = 1.toBigDecimal()
        val four = 4.toBigDecimal()

        assertEquals(0.0.toBigDecimal(), sin(zero))
        assertEquals(1.0.toBigDecimal(), cos(zero))
        assertEquals(2.0.toBigDecimal(), sqrt(four))
        assertEquals(1.0.toBigDecimal(), exp(zero))
        assertEquals(0.0.toBigDecimal(), ln(one))

        // Hyperbolic functions (inherited defaults from ExtendedField)
        assertEquals(0.0.toBigDecimal(), sinh(zero))
        assertEquals(1.0.toBigDecimal(), cosh(zero))
        assertEquals(0.0.toBigDecimal(), tanh(zero))
    }

    @Test
    fun testConversions() = with(BigDecimalField) {
        val bd = 123.45.toBigDecimal()
        assertEquals(123.45, bd.toDouble(), 1e-9)
        assertEquals(123.45f, bd.toFloat(), 1e-5f)
        assertEquals(123L, bd.toLong())
        assertEquals(123, bd.toInt())
        assertEquals(123.toBigInt(), bd.toBigInt())
    }

    @Test
    fun testStringUnaryOperators() = with(BigDecimalField) {
        val a = +"123.45"
        val b = -"123.45"
        assertEquals(123.45.toBigDecimal(), a)
        assertEquals((-123.45).toBigDecimal(), b)
    }
}
