package scietifik.kmath.asm

import scientifik.kmath.asm.compile
import scientifik.kmath.ast.mstInField
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmSpecialization {
    @Test
    fun testUnaryPlus() {
        val expr = RealField.mstInField { unaryOperation("+", symbol("x")) }.compile()
        assertEquals(2.0, expr("x" to 2.0))
    }

    @Test
    fun testUnaryMinus() {
        val expr = RealField.mstInField { unaryOperation("-", symbol("x")) }.compile()
        assertEquals(-2.0, expr("x" to 2.0))
    }

    @Test
    fun testAdd() {
        val expr = RealField.mstInField { binaryOperation("+", symbol("x"), symbol("x")) }.compile()
        assertEquals(4.0, expr("x" to 2.0))
    }

    @Test
    fun testSine() {
        val expr = RealField.mstInField { unaryOperation("sin", symbol("x")) }.compile()
        assertEquals(0.0, expr("x" to 0.0))
    }

    @Test
    fun testMinus() {
        val expr = RealField.mstInField { binaryOperation("-", symbol("x"), symbol("x")) }.compile()
        assertEquals(0.0, expr("x" to 2.0))
    }

    @Test
    fun testDivide() {
        val expr = RealField.mstInField { binaryOperation("/", symbol("x"), symbol("x")) }.compile()
        assertEquals(1.0, expr("x" to 2.0))
    }

    @Test
    fun testPower() {
        val expr = RealField
            .mstInField { binaryOperation("power", symbol("x"), number(2)) }
            .compile()

        assertEquals(4.0, expr("x" to 2.0))
    }
}
