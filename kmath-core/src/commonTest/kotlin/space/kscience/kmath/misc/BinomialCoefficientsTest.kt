/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.operations.IntRing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for the `binomialCoefficient` function in the `IntRing` class.
 *
 * Tests cover both normal and edge cases for inputs to ensure correct behavior.
 */
class BinomialCoefficientsTest {

    private val intRing = IntRing

    @Test
    fun `binomialCoefficient returns 1 for k = 0`() {
        val result = intRing.binomialCoefficient(5, 0)
        assertEquals(1, result)
    }

    @Test
    fun `binomialCoefficient returns n for k = 1`() {
        val result = intRing.binomialCoefficient(7, 1)
        assertEquals(7, result)
    }

    @Test
    fun `binomialCoefficient calculates correctly for valid inputs`() {
        val result = intRing.binomialCoefficient(5, 2)
        assertEquals(10, result)
    }

    @Test
    fun `binomialCoefficient calculates correctly for large n and small k`() {
        val result = intRing.binomialCoefficient(30, 3)
        assertEquals(4060, result)
    }

    @Test
    fun `binomialCoefficient returns 1 for k = n`() {
        val result = intRing.binomialCoefficient(6, 6)
        assertEquals(1, result)
    }

    @Test
    fun `binomialCoefficient throws IllegalArgumentException for n less than 0`() {
        assertFailsWith<IllegalArgumentException> {
            intRing.binomialCoefficient(-5, 2)
        }
    }

    @Test
    fun `binomialCoefficient throws IllegalArgumentException for k less than 0`() {
        assertFailsWith<IllegalArgumentException> {
            intRing.binomialCoefficient(5, -2)
        }
    }

    @Test
    fun `binomialCoefficient throws IllegalArgumentException for k greater than n`() {
        assertFailsWith<IllegalArgumentException> {
            intRing.binomialCoefficient(4, 5)
        }
    }

    @Test
    fun `binomialCoefficient throws ArithmeticException for overflow`() {
        assertFailsWith<ArithmeticException> {
            intRing.binomialCoefficient(67, 34)
        }
    }

    @Test
    fun `binomialCoefficient handles edge cases for valid inputs`() {
        val result = intRing.binomialCoefficient(0, 0)
        assertEquals(1, result)
    }
}