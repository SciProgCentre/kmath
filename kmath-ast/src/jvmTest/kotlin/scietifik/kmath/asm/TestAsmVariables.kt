package scietifik.kmath.asm

import scientifik.kmath.ast.mstInRing
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.ByteRing
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
