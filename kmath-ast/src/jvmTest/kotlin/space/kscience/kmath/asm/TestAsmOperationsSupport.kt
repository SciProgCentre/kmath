/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm

import space.kscience.kmath.expressions.MstField
import space.kscience.kmath.expressions.MstGroup
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmOperationsSupport {
    @Test
    fun testUnaryOperationInvocation() {
        val expression = MstGroup { -bindSymbol(x) }.compileToExpression(DoubleField)
        val res = expression(x to 2.0)
        assertEquals(-2.0, res)
    }

    @Test
    fun testBinaryOperationInvocation() {
        val expression = MstGroup { -bindSymbol(x) + number(1.0) }.compileToExpression(DoubleField)
        val res = expression(x to 2.0)
        assertEquals(-1.0, res)
    }

    @Test
    fun testConstProductInvocation() {
        val res = MstField { bindSymbol(x) * 2 }.compileToExpression(DoubleField)(x to 2.0)
        assertEquals(4.0, res)
    }

    private companion object {
        private val x by symbol
    }
}
