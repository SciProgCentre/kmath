package kscience.kmath.estree

import kscience.kmath.ast.mstInExtendedField
import kscience.kmath.ast.mstInField
import kscience.kmath.ast.mstInSpace
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.StringSymbol
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField
import kotlin.math.pow
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.measureTime

internal class TestESTreeExpressions {
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
        val e1 =
            RealField.mstInExtendedField { sin(symbol("x")).pow(4) - 6 * symbol("x") / tanh(symbol("x")) }.compile()

        val e2 = Expression<Double> { a ->
            val x = a.getValue(StringSymbol("x"))
            kotlin.math.sin(x).pow(4) - 6 * x / kotlin.math.tanh(x)
        }

        var r = Random(0)
        var s = 0.0
        measureTime { repeat(1000000) { s += e1("x" to r.nextDouble()) } }.also(::println)
        println(s)
        s = 0.0
        r = Random(0)
        measureTime { repeat(1000000) { s += e2("x" to r.nextDouble()) } }.also(::println)
        println(s)
    }
}
