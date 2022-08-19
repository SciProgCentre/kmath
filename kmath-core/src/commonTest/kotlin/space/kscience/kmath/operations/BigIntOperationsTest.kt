/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@kotlin.ExperimentalUnsignedTypes
class BigIntOperationsTest {
    @Test
    fun testPlus_1_1() {
        val x = 1.toBigInt()
        val y = 1.toBigInt()

        val res = x + y
        val sum = 2.toBigInt()

        assertEquals(sum, res)
    }

    @Test
    fun testPlusBigNumbers() {
        val x = 0x7fffffff.toBigInt()
        val y = 0x7fffffff.toBigInt()
        val z = 0x7fffffff.toBigInt()

        val res = x + y + z
        val sum = uintArrayOf(0x7ffffffdU, 0x1U).toBigInt(1)

        assertEquals(sum, res)
    }

    @Test
    fun testUnaryMinus() {
        val x = 1234.toBigInt()
        val y = (-1234).toBigInt()
        assertEquals(-x, y)
    }

    @Test
    fun testMinus_2_1() {
        val x = 2.toBigInt()
        val y = 1.toBigInt()

        val res = x - y
        val sum = 1.toBigInt()

        assertEquals(sum, res)
    }

    @Test
    fun testMinus__2_1() {
        val x = (-2).toBigInt()
        val y = 1.toBigInt()

        val res = x - y
        val sum = (-3).toBigInt()

        assertEquals(sum, res)
    }

    @Test
    fun testMinus___2_1() {
        val x = (-2).toBigInt()
        val y = 1.toBigInt()

        val res = -x - y
        val sum = 1.toBigInt()

        assertEquals(sum, res)
    }

    @Test
    fun testMinusBigNumbers() {
        val x = 12345.toBigInt()
        val y = 0xffffffffaL.toBigInt()

        val res = x - y
        val sum = (-0xfffffcfc1L).toBigInt()

        assertEquals(sum, res)
    }

    @Test
    fun testMultiply_2_3() {
        val x = 2.toBigInt()
        val y = 3.toBigInt()

        val res = x * y
        val prod = 6.toBigInt()

        assertEquals(prod, res)
    }

    @Test
    fun testMultiply__2_3() {
        val x = (-2).toBigInt()
        val y = 3.toBigInt()

        val res = x * y
        val prod = (-6).toBigInt()

        assertEquals(prod, res)
    }

    @Test
    fun testMultiply_0xfff123_0xfff456() {
        val x = 0xfff123.toBigInt()
        val y = 0xfff456.toBigInt()

        val res = x * y
        val prod = 0xffe579ad5dc2L.toBigInt()

        assertEquals(prod, res)
    }

    @Test
    fun testMultiplyUInt_0xfff123_0xfff456() {
        val x = 0xfff123.toBigInt()
        val y = 0xfff456U

        val res = x * y
        val prod = 0xffe579ad5dc2L.toBigInt()

        assertEquals(prod, res)
    }

    @Test
    fun testMultiplyInt_0xfff123__0xfff456() {
        val x = 0xfff123.toBigInt()
        val y = -0xfff456

        val res = x * y
        val prod = (-0xffe579ad5dc2L).toBigInt()

        assertEquals(prod, res)
    }

    @Test
    fun testMultiply_0xffffffff_0xffffffff() {
        val x = 0xffffffffL.toBigInt()
        val y = 0xffffffffL.toBigInt()

        val res = x * y
        val prod = 0xfffffffe00000001UL.toBigInt()

        assertEquals(prod, res)
    }

    @Test
    fun testKaratsuba() {
        val random = Random(2222)
        val x = uintArrayOf(12U, 345U)
        val y = uintArrayOf(6U, 789U)
        assertContentEquals(BigInt.naiveMultiplyMagnitudes(x, y), BigInt.karatsubaMultiplyMagnitudes(x, y))
        val x1 = UIntArray(Random.nextInt(100, 1000)) { random.nextUInt() }
        val y1 = UIntArray(Random.nextInt(100, 1000)) { random.nextUInt() }
        assertContentEquals(BigInt.naiveMultiplyMagnitudes(x1, y1), BigInt.karatsubaMultiplyMagnitudes(x1, y1))
    }

    @Test
    fun test_shr_20() {
        val x = 20.toBigInt()
        assertEquals(10.toBigInt(), x shr 1)
    }

    @Test
    fun test_shl_20() {
        val x = 20.toBigInt()
        assertEquals(40.toBigInt(), x shl 1)
    }

    @Test
    fun test_shl_1_0() {
        assertEquals(
            BigInt.ONE,
            BigInt.ONE shl 0
        )
    }

    @Test
    fun test_shl_1_32() {
        assertEquals(
            0x100000000UL.toBigInt(),
            BigInt.ONE shl 32
        )
    }

    @Test
    fun test_shl_1_33() {
        assertEquals(
            0x200000000UL.toBigInt(),
            BigInt.ONE shl 33
        )
    }

    @Test
    fun test_shr_1_33_33() {
        assertEquals(
            BigInt.ONE,
            (BigInt.ONE shl 33) shr 33
        )
    }

    @Test
    fun test_shr_1_32() {
        assertEquals(
            BigInt.ZERO,
            BigInt.ONE shr 32
        )
    }

    @Test
    fun test_and_123_456() {
        val x = 123.toBigInt()
        val y = 456.toBigInt()
        assertEquals(72.toBigInt(), x and y)
    }

    @Test
    fun test_or_123_456() {
        val x = 123.toBigInt()
        val y = 456.toBigInt()
        assertEquals(507.toBigInt(), x or y)
    }

    @Test
    fun test_asd() {
        assertEquals(
            BigInt.ONE,
            BigInt.ZERO or ((20.toBigInt() shr 4) and BigInt.ONE)
        )
    }

    @Test
    fun test_square_0x11223344U_0xad2ffffdU_0x17eU() {
        val num =
            uintArrayOf(0x11223344U, 0xad2ffffdU, 0x17eU).toBigInt(-1)
        println(num)
        val res = num * num
        assertEquals(
            res,
            uintArrayOf(0xb0542a10U, 0xbbd85bc8U, 0x2a1fa515U, 0x5069e03bU, 0x23c09U).toBigInt(1)
        )
    }

    @Test
    fun testDivision_6_3() {
        val x = 6.toBigInt()
        val y = 3U

        val res = x / y
        val div = 2.toBigInt()

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_6_3() {
        val x = 6.toBigInt()
        val y = 3.toBigInt()

        val res = x / y
        val div = 2.toBigInt()

        assertEquals(div, res)
    }

    @Test
    fun testDivision_20__3() {
        val x = 20.toBigInt()
        val y = -3

        val res = x / y
        val div = (-6).toBigInt()

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_20__3() {
        val x = 20.toBigInt()
        val y = (-3).toBigInt()

        val res = x / y
        val div = (-6).toBigInt()

        assertEquals(div, res)
    }

    @Test
    fun testDivision_0xfffffffe00000001_0xffffffff() {
        val x = 0xfffffffe00000001UL.toBigInt()
        val y = 0xffffffffU

        val res = x / y
        val div = 0xffffffffL.toBigInt()

        assertEquals(div, res)
    }

    @Test
    fun testBigDivision_0xfffffffeabcdef01UL_0xfffffffeabc() {
        val res = 0xfffffffeabcdef01UL.toBigInt() / 0xfffffffeabc.toBigInt()
        assertEquals(res, 0x100000.toBigInt())
    }

    @Test
    fun testBigDivision_0xfffffffe00000001_0xffffffff() {
        val x = 0xfffffffe00000001UL.toBigInt()
        val y = 0xffffffffU.toBigInt()

        val res = x / y
        val div = 0xffffffffL.toBigInt()

        assertEquals(div, res)
    }

    @Test
    fun testMod_20_3() {
        val x = 20.toBigInt()
        val y = 3

        val res = x % y
        val mod = 2

        assertEquals(mod, res)
    }

    @Test
    fun testBigMod_20_3() {
        val x = 20.toBigInt()
        val y = 3.toBigInt()

        val res = x % y
        val mod = 2.toBigInt()

        assertEquals(mod, res)
    }

    @Test
    fun testMod_0xfffffffe00000001_12345() {
        val x = 0xfffffffe00000001UL.toBigInt()
        val y = 12345

        val res = x % y
        val mod = 1980

        assertEquals(mod, res)
    }

    @Test
    fun testBigMod_0xfffffffe00000001_12345() {
        val x = 0xfffffffe00000001UL.toBigInt()
        val y = 12345.toBigInt()

        val res = x % y
        val mod = 1980.toBigInt()

        assertEquals(mod, res)
    }

    @Test
    fun testModPow_3_10_17() {
        val x = 3.toBigInt()
        val exp = 10.toBigInt()
        val mod = 17.toBigInt()

        val res = 8.toBigInt()

        return assertEquals(res, x.modPow(exp, mod))
    }

    @Test
    fun testModPowBigNumbers() {
        val x = 0xfffffffeabcdef01UL.toBigInt()
        val exp = 2.toBigInt()
        val mod = 0xfffffffeabcUL.toBigInt()

        val res = 0xc2253cde01.toBigInt()

        return assertEquals(res, x.modPow(exp, mod))
    }

    @Test
    fun testModBigNumbers() {
        val x = 0xfffffffeabcdef01UL.toBigInt()
        val mod = 0xfffffffeabcUL.toBigInt()

        val res = 0xdef01.toBigInt()

        return assertEquals(res, x % mod)
    }

    @Test
    fun testNotEqualsOtherTypeInstanceButButNotFails() = assertFalse(0.toBigInt().equals(""))

    @Test
    fun testIntAbsOverflow() {
        assertEquals((-Int.MAX_VALUE.toLong().toBigInt() - 1.toBigInt()) * 2, 2.toBigInt() * Int.MIN_VALUE)
    }
}
