/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MstRing
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TestCompilerVariables {
    @Test
    fun testVariable() = runCompilerTest {
        val expr = MstRing { x }.compileToExpression(IntRing)
        assertEquals(1, expr(x to 1))
    }

    @Test
    fun testUndefinedVariableFails() = runCompilerTest {
        val expr = MstRing { x }.compileToExpression(IntRing)
        assertFailsWith<NoSuchElementException> { expr() }
    }
}
