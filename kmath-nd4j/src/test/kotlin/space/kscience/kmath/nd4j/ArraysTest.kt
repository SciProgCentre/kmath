/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArraysTest {
    @Test
    fun checkLinspaceBuilder() {
        val array = LongArray(3).linspace(15, 22)
        assertEquals(array[0],  15)
        assertEquals(array[1], 18)
        assertEquals(array[2], 21)
    }

    @Test
    fun checkZerosBuilder() {
        val array = LongArray(3).zeros()
        assertEquals(array[0], 0)
        assertEquals(array[1], 0)
        assertEquals(array[2], 0)
    }

    @Test
    fun checkOnesBuilder() {
        val array = LongArray(3).ones()
        assertEquals(array[0], 1)
        assertEquals(array[1], 1)
        assertEquals(array[2], 1)
    }

    @Test
    fun checkRepeatBuilder() {
        val array = repeat(5, 3)
        assertEquals(array[0], 5)
        assertEquals(array[1], 5)
        assertEquals(array[2], 5)
    }
}