package kscience.kmath.estree

import kscience.kmath.ast.mstInExtendedField
import kscience.kmath.ast.mstInField
import kscience.kmath.ast.mstInSpace
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestESTreeOperationsSupport {
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

    @Test
    fun testMultipleCalls() {
        val e = RealField.mstInExtendedField { sin(symbol("x")).pow(4) - 6 * symbol("x") / tanh(symbol("x")) }.compile()
        val r = Random(0)
        var s = 0.0
        repeat(1000000) { s += e("x" to r.nextDouble()) }
        println(s)
    }
}
