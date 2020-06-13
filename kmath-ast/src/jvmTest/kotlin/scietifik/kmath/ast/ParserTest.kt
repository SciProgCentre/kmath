package scietifik.kmath.ast

import scientifik.kmath.ast.evaluate
import scientifik.kmath.ast.parseMath
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import kotlin.test.assertEquals
import kotlin.test.Test

internal class ParserTest {
    @Test
    fun parsedExpression() {
        val mst = "2+2*(2+2)".parseMath()
        val res = ComplexField.evaluate(mst)
        assertEquals(Complex(10.0, 0.0), res)
    }
}
