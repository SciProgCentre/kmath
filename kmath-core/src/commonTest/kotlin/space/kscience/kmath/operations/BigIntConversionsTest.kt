/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@kotlin.ExperimentalUnsignedTypes
class BigIntConversionsTest {

    @Test
    fun testEmptyString() {
        assertNull("".parseBigInteger())
        assertNull("+".parseBigInteger())
        assertNull("-".parseBigInteger())

        assertNull("0x".parseBigInteger())
        assertNull("+0x".parseBigInteger())
        assertNull("-0x".parseBigInteger())


        assertNull("_".parseBigInteger())
        assertNull("+_".parseBigInteger())
        assertNull("-_".parseBigInteger())

        assertNull("0x_".parseBigInteger())
        assertNull("+0x_".parseBigInteger())
        assertNull("-0x_".parseBigInteger())
    }

    @Test
    fun testToString0x10() {
        val x = 0x10.toBigInt()
        assertEquals("0x10", x.toString())
    }

    @Test
    fun testUnderscores() {
        assertEquals("0x10", "0x_1_0_".parseBigInteger().toString())
        assertEquals("0xa", "_1_0_".parseBigInteger().toString())
    }

    @Test
    fun testToString0x17ffffffd() {
        val x = 0x17ffffffdL.toBigInt()
        assertEquals("0x17ffffffd", x.toString())
    }

    @Test
    fun testToString_0x17ead2ffffd() {
        val x = (-0x17ead2ffffdL).toBigInt()
        assertEquals("-0x17ead2ffffd", x.toString())
    }

    @Test
    fun testToString_0x17ead2ffffd11223344() {
        val x = uintArrayOf(0x11223344U, 0xad2ffffdU, 0x17eU).toBigInt(-1)
        assertEquals("-0x17ead2ffffd11223344", x.toString())
    }

    @Test
    fun testFromString_0x17ead2ffffd11223344() {
        val x = "0x17ead2ffffd11223344".parseBigInteger()
        assertEquals("0x17ead2ffffd11223344", x.toString())
    }

    @Test
    fun testFromString_7059135710711894913860() {
        val x = "-7059135710711894913860".parseBigInteger()
        assertEquals("-0x17ead2ffffd11223344", x.toString())
    }
}
