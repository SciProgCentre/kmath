package scietifik.kmath.ast

import scientifik.kmath.ast.evaluate
import scientifik.kmath.ast.mstInField
import scientifik.kmath.ast.parseMath
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
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
}
