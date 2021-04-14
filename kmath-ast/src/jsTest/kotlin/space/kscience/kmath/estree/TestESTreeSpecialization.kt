package space.kscience.kmath.estree

import space.kscience.kmath.expressions.MstExtendedField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestESTreeSpecialization {
    @Test
    fun testUnaryPlus() {
        val expr = MstExtendedField { unaryOperationFunction("+")(bindSymbol("x")) }.compileToExpression(DoubleField)
        assertEquals(2.0, expr("x" to 2.0))
    }

    @Test
    fun testUnaryMinus() {
        val expr = MstExtendedField { unaryOperationFunction("-")(bindSymbol("x")) }.compileToExpression(DoubleField)
        assertEquals(-2.0, expr("x" to 2.0))
    }

    @Test
    fun testAdd() {
        val expr = MstExtendedField {
            binaryOperationFunction("+")(bindSymbol("x"),
                bindSymbol("x"))
        }.compileToExpression(DoubleField)
        assertEquals(4.0, expr("x" to 2.0))
    }

    @Test
    fun testSine() {
        val expr = MstExtendedField { unaryOperationFunction("sin")(bindSymbol("x")) }.compileToExpression(DoubleField)
        assertEquals(0.0, expr("x" to 0.0))
    }

    @Test
    fun testMinus() {
        val expr = MstExtendedField {
            binaryOperationFunction("-")(bindSymbol("x"),
                bindSymbol("x"))
        }.compileToExpression(DoubleField)
        assertEquals(0.0, expr("x" to 2.0))
    }

    @Test
    fun testDivide() {
        val expr = MstExtendedField {
            binaryOperationFunction("/")(bindSymbol("x"),
                bindSymbol("x"))
        }.compileToExpression(DoubleField)
        assertEquals(1.0, expr("x" to 2.0))
    }

    @Test
    fun testPower() {
        val expr = MstExtendedField {
            binaryOperationFunction("pow")(bindSymbol("x"), number(2))
        }.compileToExpression(DoubleField)

        assertEquals(4.0, expr("x" to 2.0))
    }
}
