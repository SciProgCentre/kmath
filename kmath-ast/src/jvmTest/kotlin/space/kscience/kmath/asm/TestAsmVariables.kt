package space.kscience.kmath.asm

import space.kscience.kmath.ast.mstInRing
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.ByteRing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TestAsmVariables {
    @Test
    fun testVariable() {
        val expr = ByteRing.mstInRing { bindSymbol("x") }.compile()
        assertEquals(1.toByte(), expr("x" to 1.toByte()))
    }

    @Test
    fun testUndefinedVariableFails() {
        val expr = ByteRing.mstInRing { bindSymbol("x") }.compile()
        assertFailsWith<NoSuchElementException> { expr() }
    }
}
