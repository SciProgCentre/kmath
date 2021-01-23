package kscience.kmath.estree

import kscience.kmath.ast.mstInRing
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.ByteRing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class TestESTreeVariables {
    @Test
    fun testVariable() {
        val expr = ByteRing.mstInRing { symbol("x") }.compile()
        assertEquals(1.toByte(), expr("x" to 1.toByte()))
    }

    @Test
    fun testUndefinedVariableFails() {
        val expr = ByteRing.mstInRing { symbol("x") }.compile()
        assertFailsWith<NoSuchElementException> { expr() }
    }
}
