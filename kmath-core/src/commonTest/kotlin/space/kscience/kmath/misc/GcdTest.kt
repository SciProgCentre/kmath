/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.operations.Int32Ring
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GcdTest {

    /**
     * Unit tests for the [space.kscience.kmath.operations.IntRing.gcd] function.
     *
     * The `gcd` function computes the greatest common divisor of two integers
     * using the IntRing algebra. It handles both positive and negative integers
     * and reports an overflow when it encounters edge-case values like `Int.MIN_VALUE`.
     */

    @Test
    fun testGcdOfPositiveNumbers() {
        val result = Int32Ring.gcd(48, 18)
        assertEquals(6, result, "GCD of 48 and 18 should be 6")
    }

    @Test
    fun testGcdOfNegativeNumbers() {
        val result = Int32Ring.gcd(-48, -18)
        assertEquals(6, result, "GCD of -48 and -18 should be 6")
    }

    @Test
    fun testGcdWithZeroAndPositiveNumber() {
        val result = Int32Ring.gcd(0, 18)
        assertEquals(18, result, "GCD of 0 and 18 should be 18")
    }

    @Test
    fun testGcdWithZeroAndNegativeNumber() {
        val result = Int32Ring.gcd(0, -18)
        assertEquals(18, result, "GCD of 0 and -18 should be 18")
    }

    @Test
    fun testGcdWithBothZeros() {
        val result = Int32Ring.gcd(0, 0)
        assertEquals(0, result, "GCD of 0 and 0 should be 0")
    }

    @Test
    fun testGcdWithSamePositiveNumbers() {
        val result = Int32Ring.gcd(42, 42)
        assertEquals(42, result, "GCD of 42 and 42 should be 42")
    }

    @Test
    fun testGcdWithSameNegativeNumbers() {
        val result = Int32Ring.gcd(-42, -42)
        assertEquals(42, result, "GCD of -42 and -42 should be 42")
    }

    @Test
    fun testGcdWithLargeNumbers() {
        val result = Int32Ring.gcd(123456, 789012)
        assertEquals(12, result, "GCD of 123456 and 789012 should be 12")
    }

    @Test
    fun testGcdOverflowError() {
        assertFailsWith<IllegalStateException>("Expected error for GCD overflow when using Int.MIN_VALUE and 0") {
            Int32Ring.gcd(Int.MIN_VALUE, 0)
        }
    }
}