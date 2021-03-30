package space.kscience.kmath.asm

import space.kscience.kmath.ast.mstInField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmSpecialization {
    @Test
    fun testUnaryPlus() {
        val expr = DoubleField.mstInField { unaryOperationFunction("+")(bindSymbol("x")) }.compile()
        assertEquals(2.0, expr("x" to 2.0))
    }

    @Test
    fun testUnaryMinus() {
        val expr = DoubleField.mstInField { unaryOperationFunction("-")(bindSymbol("x")) }.compile()
        assertEquals(-2.0, expr("x" to 2.0))
    }

    @Test
    fun testAdd() {
        val expr = DoubleField.mstInField { binaryOperationFunction("+")(bindSymbol("x"), bindSymbol("x")) }.compile()
        assertEquals(4.0, expr("x" to 2.0))
    }

    @Test
    fun testSine() {
        val expr = DoubleField.mstInField { unaryOperationFunction("sin")(bindSymbol("x")) }.compile()
        assertEquals(0.0, expr("x" to 0.0))
    }

    @Test
    fun testMinus() {
        val expr = DoubleField.mstInField { binaryOperationFunction("-")(bindSymbol("x"), bindSymbol("x")) }.compile()
        assertEquals(0.0, expr("x" to 2.0))
    }

    @Test
    fun testDivide() {
        val expr = DoubleField.mstInField { binaryOperationFunction("/")(bindSymbol("x"), bindSymbol("x")) }.compile()
        assertEquals(1.0, expr("x" to 2.0))
    }

    @Test
    fun testPower() {
        val expr = DoubleField
            .mstInField { binaryOperationFunction("pow")(bindSymbol("x"), number(2)) }
            .compile()

        assertEquals(4.0, expr("x" to 2.0))
    }
}
