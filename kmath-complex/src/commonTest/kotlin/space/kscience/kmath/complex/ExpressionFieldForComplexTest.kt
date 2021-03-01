package space.kscience.kmath.complex

import space.kscience.kmath.expressions.FunctionalExpressionField
import space.kscience.kmath.expressions.bindSymbol
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExpressionFieldForComplexTest {
    val x by symbol

    @Test
    fun testComplex() {
        val context = FunctionalExpressionField(ComplexField)

        val expression = context {
            val x = bindSymbol(x)
            x * x + 2 * x + one
        }

        assertEquals(expression(x to Complex(1.0, 0.0)), Complex(4.0, 0.0))
        //assertEquals(expression(), Complex(9.0, 0.0))
    }
}
