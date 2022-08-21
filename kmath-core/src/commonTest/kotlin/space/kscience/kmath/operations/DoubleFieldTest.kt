/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.testutils.FieldVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DoubleFieldTest {
    @Test
    fun verify() = FieldVerifier(DoubleField, 42.0, 66.0, 2.0, 5).verify()

    @Test
    fun testSqrt() {
        val sqrt = DoubleField { sqrt(25 * one) }
        assertEquals(5.0, sqrt)
    }

    @Test
    fun testPow() = DoubleField {
        val num = 5 * one
        assertEquals(5.0, power(num, 1), 0.01)
        assertEquals(25.0, power(num, 2), 0.01)
        assertEquals(1.0, power(num, 0), 0.01)
        assertEquals(0.2, power(num, -1), 0.01)
        assertEquals(0.04, power(num, -2), 0.01)
        assertEquals(0.0, power(num, Int.MIN_VALUE), 0.01)
        assertEquals(1.0, power(zero, 0), 0.01)
    }
}
