/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.operations.ByteRing
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.pi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

internal class TestFolding {
    @Test
    fun foldUnary() = assertEquals(
        -1,
        ("-(1)".parseMath().evaluateConstants(IntRing) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldDeepUnary() = assertEquals(
        1,
        ("-(-(1))".parseMath().evaluateConstants(IntRing) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldBinary() = assertEquals(
        2,
        ("1*2".parseMath().evaluateConstants(IntRing) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldDeepBinary() = assertEquals(
        10,
        ("1*2*5".parseMath().evaluateConstants(IntRing) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldSymbol() = assertEquals(
        DoubleField.pi,
        ("pi".parseMath().evaluateConstants(DoubleField) as? TypedMst.Constant<Double> ?: fail()).value,
    )

    @Test
    fun foldNumeric() = assertEquals(
        42.toByte(),
        ("42".parseMath().evaluateConstants(ByteRing) as? TypedMst.Constant<Byte> ?: fail()).value,
    )
}
