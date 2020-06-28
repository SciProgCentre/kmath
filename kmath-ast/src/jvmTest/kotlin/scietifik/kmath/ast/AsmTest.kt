package scietifik.kmath.ast

import scientifik.kmath.asm.compile
import scientifik.kmath.asm.expression
import scientifik.kmath.ast.mstInField
import scientifik.kmath.ast.parseMath
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AsmTest {
    @Test
    fun `compile MST`() {
        val res = ComplexField.expression("2+2*(2+2)".parseMath())()
        assertEquals(Complex(10.0, 0.0), res)
    }

    @Test
    fun `compile MSTExpression`() {
        val res = ComplexField.mstInField { number(2) + number(2) * (number(2) + number(2)) }.compile()()
        assertEquals(Complex(10.0, 0.0), res)
    }
}
