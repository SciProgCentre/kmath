package scietifik.kmath.asm

import scientifik.kmath.asm.compile
import scientifik.kmath.ast.mstInField
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestSpecialization {
    @Test
    fun testUnaryPlus() {
        val expr = RealField.mstInField { unaryOperation("+", symbol("x")) }.compile()
        val res = expr("x" to 2.0)
        assertEquals(2.0, res)
    }

    @Test
    fun testUnaryMinus() {
        val expr = RealField.mstInField { unaryOperation("-", symbol("x")) }.compile()
        val res = expr("x" to 2.0)
        assertEquals(-2.0, res)
    }

    @Test
    fun testAdd() {
        val expr = RealField.mstInField { binaryOperation("+", symbol("x"), symbol("x")) }.compile()
        val res = expr("x" to 2.0)
        assertEquals(4.0, res)
    }

    @Test
    fun testMinus() {
        val expr = RealField.mstInField { binaryOperation("-", symbol("x"), symbol("x")) }.compile()
        val res = expr("x" to 2.0)
        assertEquals(0.0, res)
    }

    @Test
    fun testDivide() {
        val expr = RealField.mstInField { binaryOperation("/", symbol("x"), symbol("x")) }.compile()
        val res = expr("x" to 2.0)
        assertEquals(1.0, res)
    }
}
