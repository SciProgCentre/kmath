/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.operations.invoke
import space.kscience.kmath.testutils.assertBufferEquals
import kotlin.test.Test
import kotlin.test.assertEquals

internal class QuaternionTest {

    @Test
    fun testNorm() {
        assertEquals(2.0, QuaternionAlgebra.norm(Quaternion(1.0, 1.0, 1.0, 1.0)))
    }

    @Test
    fun testInverse() = QuaternionAlgebra {
        val q = Quaternion(1.0, 2.0, -3.0, 4.0)
        assertBufferEquals(one, q * q.reciprocal, 1e-4)
    }

    @Test
    fun testAddition() {
        assertEquals(Quaternion(42, 42), QuaternionAlgebra { Quaternion(16, 16) + Quaternion(26, 26) })
        assertEquals(Quaternion(42, 16), QuaternionAlgebra { Quaternion(16, 16) + 26 })
        assertEquals(Quaternion(42, 16), QuaternionAlgebra { 26 + Quaternion(16, 16) })
    }

//    @Test
//    fun testSubtraction() {
//        assertEquals(Quaternion(42, 42), QuaternionField { Quaternion(86, 55) - Quaternion(44, 13) })
//        assertEquals(Quaternion(42, 56), QuaternionField { Quaternion(86, 56) - 44 })
//        assertEquals(Quaternion(42, 56), QuaternionField { 86 - Quaternion(44, -56) })
//    }

    @Test
    fun testMultiplication() {
        assertEquals(Quaternion(42, 42), QuaternionAlgebra { Quaternion(4.2, 0) * Quaternion(10, 10) })
        assertEquals(Quaternion(42, 21), QuaternionAlgebra { Quaternion(4.2, 2.1) * 10 })
        assertEquals(Quaternion(42, 21), QuaternionAlgebra { 10 * Quaternion(4.2, 2.1) })
    }

//    @Test
//    fun testDivision() {
//        assertEquals(Quaternion(42, 42), QuaternionField { Quaternion(0, 168) / Quaternion(2, 2) })
//        assertEquals(Quaternion(42, 56), QuaternionField { Quaternion(86, 56) - 44 })
//        assertEquals(Quaternion(42, 56) , QuaternionField { 86 - Quaternion(44, -56) })
//    }

    @Test
    fun testPower() {
        assertEquals(QuaternionAlgebra.zero, QuaternionAlgebra { zero pow 2 })
        assertEquals(QuaternionAlgebra.zero, QuaternionAlgebra { zero pow 2 })

        assertEquals(
            QuaternionAlgebra { i * 8 }.let { it.x.toInt() to it.w.toInt() },
            QuaternionAlgebra { Quaternion(2, 2) pow 2 }.let { it.x.toInt() to it.w.toInt() })
    }
}
