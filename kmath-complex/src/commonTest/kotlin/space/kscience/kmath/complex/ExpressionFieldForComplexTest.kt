package space.kscience.kmath.complex

import space.kscience.kmath.expressions.FunctionalExpressionField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.operations.bindSymbol
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExpressionFieldForComplexTest {
    val x by symbol

    @Test
    fun testComplex() {
        val expression = FunctionalExpressionField(ComplexField).run {
            val x = bindSymbol(x)
            x * x + 2 * x + one
        }

        assertEquals(expression(x to Complex(1.0, 0.0)), Complex(4.0, 0.0))
    }
}
