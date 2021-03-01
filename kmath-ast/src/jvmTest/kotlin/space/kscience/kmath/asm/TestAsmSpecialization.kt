package space.kscience.kmath.asm

import space.kscience.kmath.ast.mstInField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmSpecialization {
    @Test
    fun testUnaryPlus() {
        val expr = RealField.mstInField { unaryOperationFunction("+")(bindSymbol("x")) }.compile()
        assertEquals(2.0, expr("x" to 2.0))
    }

    @Test
    fun testUnaryMinus() {
        val expr = RealField.mstInField { unaryOperationFunction("-")(bindSymbol("x")) }.compile()
        assertEquals(-2.0, expr("x" to 2.0))
    }

    @Test
    fun testAdd() {
        val expr = RealField.mstInField { binaryOperationFunction("+")(bindSymbol("x"), bindSymbol("x")) }.compile()
        assertEquals(4.0, expr("x" to 2.0))
    }

    @Test
    fun testSine() {
        val expr = RealField.mstInField { unaryOperationFunction("sin")(bindSymbol("x")) }.compile()
        assertEquals(0.0, expr("x" to 0.0))
    }

    @Test
    fun testMinus() {
        val expr = RealField.mstInField { binaryOperationFunction("-")(bindSymbol("x"), bindSymbol("x")) }.compile()
        assertEquals(0.0, expr("x" to 2.0))
    }

    @Test
    fun testDivide() {
        val expr = RealField.mstInField { binaryOperationFunction("/")(bindSymbol("x"), bindSymbol("x")) }.compile()
        assertEquals(1.0, expr("x" to 2.0))
    }

    @Test
    fun testPower() {
        val expr = RealField
            .mstInField { binaryOperationFunction("pow")(bindSymbol("x"), number(2)) }
            .compile()

        assertEquals(4.0, expr("x" to 2.0))
    }
}
