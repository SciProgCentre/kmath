/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.wasm

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.MstExtendedField
import space.kscience.kmath.expressions.MstRing
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(UnstableKMathAPI::class)
internal class TestWasmSpecific {
    @Test
    fun int() {
        val res = MstRing { number(100000000) + number(10000000) }.compile(IntRing)
        assertEquals(110000000, res)
    }

    @Test
    fun real() {
        val res = MstExtendedField { number(100000000) + number(2).pow(10) }.compile(DoubleField)
        assertEquals(100001024.0, res)
    }

    @Test
    fun argsPassing() {
        val res = MstExtendedField { y + x.pow(10) }.compile(
            DoubleField,
            x to 2.0,
            y to 100000000.0,
        )

        assertEquals(100001024.0, res)
    }

    @Test
    fun powFunction() {
        val expr = MstExtendedField { x.pow(1.0 / 6.0) }.compileToExpression(DoubleField)
        assertEquals(0.9730585187140817, expr(x to 0.8488554755054833))
    }

    private companion object {
        private val x by symbol
        private val y by symbol
    }
}
