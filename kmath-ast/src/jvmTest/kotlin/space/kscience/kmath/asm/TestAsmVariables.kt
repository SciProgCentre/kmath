/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm

import space.kscience.kmath.expressions.MstRing
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.operations.ByteRing
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TestAsmVariables {
    @Test
    fun testVariable() {
        val expr = MstRing { bindSymbol(x) }.compileToExpression(ByteRing)
        assertEquals(1.toByte(), expr(x to 1.toByte()))
    }

    @Test
    fun testUndefinedVariableFails() {
        val expr = MstRing { bindSymbol(x) }.compileToExpression(ByteRing)
        assertFailsWith<NoSuchElementException> { expr() }
    }

    private companion object {
        private val x by symbol
    }
}
