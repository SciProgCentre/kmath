package kscience.kmath.ast

import kscience.kmath.asm.compile
import kscience.kmath.asm.expression
import kscience.kmath.ast.mstInField
import kscience.kmath.ast.parseMath
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.Complex
import kscience.kmath.operations.ComplexField
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
