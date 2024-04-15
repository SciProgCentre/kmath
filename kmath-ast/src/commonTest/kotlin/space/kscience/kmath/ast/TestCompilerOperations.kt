/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MstExtendedField
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestCompilerOperations {
    @Test
    fun testUnaryPlus() = runCompilerTest {
        val expr = MstExtendedField { +x }.compileToExpression(Float64Field)
        assertEquals(2.0, expr(x to 2.0))
    }

    @Test
    fun testUnaryMinus() = runCompilerTest {
        val expr = MstExtendedField { -x }.compileToExpression(Float64Field)
        assertEquals(-2.0, expr(x to 2.0))
    }

    @Test
    fun testAdd() = runCompilerTest {
        val expr = MstExtendedField { x + x }.compileToExpression(Float64Field)
        assertEquals(4.0, expr(x to 2.0))
    }

    @Test
    fun testSine() = runCompilerTest {
        val expr = MstExtendedField { sin(x) }.compileToExpression(Float64Field)
        assertEquals(0.0, expr(x to 0.0))
    }

    @Test
    fun testCosine() = runCompilerTest {
        val expr = MstExtendedField { cos(x) }.compileToExpression(Float64Field)
        assertEquals(1.0, expr(x to 0.0))
    }

    @Test
    fun testTangent() = runCompilerTest {
        val expr = MstExtendedField { tan(x) }.compileToExpression(Float64Field)
        assertEquals(0.0, expr(x to 0.0))
    }

    @Test
    fun testArcSine() = runCompilerTest {
        val expr = MstExtendedField { asin(x) }.compileToExpression(Float64Field)
        assertEquals(0.0, expr(x to 0.0))
    }

    @Test
    fun testArcCosine() = runCompilerTest {
        val expr = MstExtendedField { acos(x) }.compileToExpression(Float64Field)
        assertEquals(0.0, expr(x to 1.0))
    }

    @Test
    fun testAreaHyperbolicSine() = runCompilerTest {
        val expr = MstExtendedField { asinh(x) }.compileToExpression(Float64Field)
        assertEquals(0.0, expr(x to 0.0))
    }

    @Test
    fun testSubtract() = runCompilerTest {
        val expr = MstExtendedField { x - x }.compileToExpression(Float64Field)
        assertEquals(0.0, expr(x to 2.0))
    }

    @Test
    fun testDivide() = runCompilerTest {
        val expr = MstExtendedField { x / x }.compileToExpression(Float64Field)
        assertEquals(1.0, expr(x to 2.0))
    }

    @Test
    fun testPower() = runCompilerTest {
        val expr = MstExtendedField { x pow 2 }.compileToExpression(Float64Field)
        assertEquals(4.0, expr(x to 2.0))
    }
}
