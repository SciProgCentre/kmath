package scietifik.kmath.ast

import scientifik.kmath.ast.evaluate
import scientifik.kmath.ast.mstInField
import scientifik.kmath.ast.parseMath
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParserTest {
    @Test
    fun `evaluate MST`() {
        val mst = "2+2*(2+2)".parseMath()
        val res = ComplexField.evaluate(mst)
        assertEquals(Complex(10.0, 0.0), res)
    }

    @Test
    fun `evaluate MSTExpression`() {
        val res = ComplexField.mstInField { number(2) + number(2) * (number(2) + number(2)) }()
        assertEquals(Complex(10.0, 0.0), res)
    }

    @Test
    fun `evaluate MST with singular`() {
        val mst = "i".parseMath()
        val res = ComplexField.evaluate(mst)
        assertEquals(ComplexField.i, res)
    }


    @Test
    fun `evaluate MST with unary function`() {
        val mst = "sin(0)".parseMath()
        val res = RealField.evaluate(mst)
        assertEquals(0.0, res)
    }

    @Test
    fun `evaluate MST with binary function`() {
        val magicalAlgebra = object : Algebra<String> {
            override fun symbol(value: String): String = value

            override fun unaryOperation(operation: String, arg: String): String = throw NotImplementedError()

            override fun binaryOperation(operation: String, left: String, right: String): String = when (operation) {
                "magic" -> "$left ★ $right"
                else -> throw NotImplementedError()
            }
        }

        val mst = "magic(a, b)".parseMath()
        val res = magicalAlgebra.evaluate(mst)
        assertEquals("a ★ b", res)
    }
}
