package scietifik.kmath.asm

import scientifik.kmath.asm.asmField
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAsmExpressions {
    @Test
    fun testUnaryOperationInvocation() {
        val res = RealField.asmField { unaryOperation("+", variable("x")) }("x" to 2.0)
        assertEquals(2.0, res)
    }

    @Test
    fun testConstProductInvocation() {
        val res = RealField.asmField { variable("x") * 2 }("x" to 2.0)
        assertEquals(4.0, res)
    }
}
