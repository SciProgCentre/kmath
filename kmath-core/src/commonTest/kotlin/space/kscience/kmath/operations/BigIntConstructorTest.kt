/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import kotlin.test.Test
import kotlin.test.assertEquals

class BigIntConstructorTest {
    @Test
    fun testConstructorZero() {
        assertEquals(0.toBigInt(), uintArrayOf().toBigInt(0))
    }

    @Test
    fun testConstructor8() {
        assertEquals(
            8.toBigInt(),
            uintArrayOf(8U).toBigInt(1)
        )
    }

    @Test
    fun testConstructor_0xffffffffaL() {
        val x = (-0xffffffffaL).toBigInt()
        val y = uintArrayOf(0xfffffffaU, 0xfU).toBigInt(-1)
        assertEquals(x, y)
    }
}
