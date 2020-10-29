package kscience.kmath.asm

import kscience.kmath.asm.compile
import kscience.kmath.ast.mstInField
import kscience.kmath.ast.mstInSpace
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmExpressions {
    @Test
    fun testUnaryOperationInvocation() {
        val expression = RealField.mstInSpace { -symbol("x") }.compile()
        val res = expression("x" to 2.0)
        assertEquals(-2.0, res)
    }

    @Test
    fun testBinaryOperationInvocation() {
        val expression = RealField.mstInSpace { -symbol("x") + number(1.0) }.compile()
        val res = expression("x" to 2.0)
        assertEquals(-1.0, res)
    }

    @Test
    fun testConstProductInvocation() {
        val res = RealField.mstInField { symbol("x") * 2 }("x" to 2.0)
        assertEquals(4.0, res)
    }
}
