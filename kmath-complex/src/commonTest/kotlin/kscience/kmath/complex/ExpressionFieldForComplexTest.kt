package kscience.kmath.complex

import kscience.kmath.expressions.FunctionalExpressionField
import kscience.kmath.expressions.bind
import kscience.kmath.expressions.invoke
import kscience.kmath.expressions.symbol
import kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExpressionFieldForComplexTest {
    val x by symbol

    @Test
    fun testComplex() {
        val context = FunctionalExpressionField(ComplexField)

        val expression = context {
            val x = bind(x)
            x * x + 2 * x + one
        }

        assertEquals(expression(x to Complex(1.0, 0.0)), Complex(4.0, 0.0))
        //assertEquals(expression(), Complex(9.0, 0.0))
    }
}
