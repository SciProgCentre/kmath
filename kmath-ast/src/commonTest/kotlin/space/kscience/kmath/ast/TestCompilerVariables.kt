/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MstRing
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.Symbol.Companion.y
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TestCompilerVariables {
    @Test
    fun testNoVariables() = runCompilerTest {
        val expr = "0".parseMath().compileToExpression(DoubleField)
        assertEquals(0.0, expr(), 0.0001)
    }

    @Test
    fun testOneVariable() = runCompilerTest {
        val expr = MstRing { x }.compileToExpression(IntRing)
        assertEquals(1, expr(x to 1))
    }

    @Test
    fun testTwoVariables() = runCompilerTest {
        val expr = "y+x/y+x".parseMath().compileToExpression(DoubleField)
        assertEquals(8.0, expr(x to 4.0, y to 2.0))
    }

    @Test
    fun testUndefinedVariableFails() = runCompilerTest {
        val expr = MstRing { x }.compileToExpression(IntRing)
        assertFailsWith<NoSuchElementException> { expr() }
    }
}
