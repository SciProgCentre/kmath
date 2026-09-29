/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MstInterpreterContext
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.operations.Float64Field
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmContext {
    @Test
    fun compileWithContext() {
        val a = Symbol("a")
        val c = Symbol("c")

        val mstContext = MstInterpreterContext(
            algebra = Float64Field,
            arguments = mapOf(x to 3.0),
            constants = mapOf(c to 10.0),
            functions = mapOf("f" to Expression(Float64Field.type) { it.getValue(a) * 2 }),
        )

        val mst = MST.Binary("+", MST.FunctionCall("f", mapOf(a to x)), c)
        assertEquals(16.0, context(mstContext) { mst.compile() })
    }
}
