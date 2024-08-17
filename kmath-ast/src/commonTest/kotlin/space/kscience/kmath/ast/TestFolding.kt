/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.Int8Ring
import space.kscience.kmath.operations.pi
import space.kscience.kmath.structures.Float64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

internal class TestFolding {
    @Test
    fun foldUnary() = assertEquals(
        -1,
        ("-(1)".parseMath().evaluateConstants(Int32Ring) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldDeepUnary() = assertEquals(
        1,
        ("-(-(1))".parseMath().evaluateConstants(Int32Ring) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldBinary() = assertEquals(
        2,
        ("1*2".parseMath().evaluateConstants(Int32Ring) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldDeepBinary() = assertEquals(
        10,
        ("1*2*5".parseMath().evaluateConstants(Int32Ring) as? TypedMst.Constant<Int> ?: fail()).value,
    )

    @Test
    fun foldSymbol() = assertEquals(
        Float64Field.pi,
        ("pi".parseMath().evaluateConstants(Float64Field) as? TypedMst.Constant<Float64> ?: fail()).value,
    )

    @Test
    fun foldNumeric() = assertEquals(
        42.toByte(),
        ("42".parseMath().evaluateConstants(Int8Ring) as? TypedMst.Constant<Byte> ?: fail()).value,
    )
}
