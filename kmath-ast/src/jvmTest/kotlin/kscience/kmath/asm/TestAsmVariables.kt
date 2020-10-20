package kscience.kmath.asm

import kscience.kmath.ast.mstInRing
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.ByteRing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TestAsmVariables {
    @Test
    fun testVariableWithoutDefault() {
        val expr = ByteRing.mstInRing { symbol("x") }
        assertEquals(1.toByte(), expr("x" to 1.toByte()))
    }

    @Test
    fun testVariableWithoutDefaultFails() {
        val expr = ByteRing.mstInRing { symbol("x") }
        assertFailsWith<IllegalStateException> { expr() }
    }
}
