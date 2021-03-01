package space.kscience.kmath.asm

import space.kscience.kmath.ast.mstInExtendedField
import space.kscience.kmath.ast.mstInField
import space.kscience.kmath.ast.mstInSpace
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.RealField
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmOperationsSupport {
    @Test
    fun testUnaryOperationInvocation() {
        val expression = RealField.mstInSpace { -bindSymbol("x") }.compile()
        val res = expression("x" to 2.0)
        assertEquals(-2.0, res)
    }

    @Test
    fun testBinaryOperationInvocation() {
        val expression = RealField.mstInSpace { -bindSymbol("x") + number(1.0) }.compile()
        val res = expression("x" to 2.0)
        assertEquals(-1.0, res)
    }

    @Test
    fun testConstProductInvocation() {
        val res = RealField.mstInField { bindSymbol("x") * 2 }("x" to 2.0)
        assertEquals(4.0, res)
    }

    @Test
    fun testMultipleCalls() {
        val e = RealField.mstInExtendedField { sin(bindSymbol("x")).pow(4) - 6 * bindSymbol("x") / tanh(bindSymbol("x")) }.compile()
        val r = Random(0)
        var s = 0.0
        repeat(1000000) { s += e("x" to r.nextDouble()) }
        println(s)
    }
}
