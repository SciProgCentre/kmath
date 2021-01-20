package kscience.kmath.expressions

import kscience.kmath.operations.Complex
import kscience.kmath.operations.ComplexField
import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ExpressionFieldTest {
    val x by symbol
    @Test
    fun testExpression() {
        val context = FunctionalExpressionField(RealField)

        val expression = context {
            val x by binding()
            x * x + 2 * x + one
        }

        assertEquals(expression(x to 1.0), 4.0)
        assertFails {  expression()}
    }

    @Test
    fun testComplex() {
        val context = FunctionalExpressionField(ComplexField)

        val expression = context {
            val  x = bind(x)
            x * x + 2 * x + one
        }

        assertEquals(expression(x to Complex(1.0, 0.0)), Complex(4.0, 0.0))
        //assertEquals(expression(), Complex(9.0, 0.0))
    }

    @Test
    fun separateContext() {
        fun <T> FunctionalExpressionField<T, *>.expression(): Expression<T> {
            val x by binding()
            return x * x + 2 * x + one
        }

        val expression = FunctionalExpressionField(RealField).expression()
        assertEquals(expression(x to 1.0), 4.0)
    }

    @Test
    fun valueExpression() {
        val expressionBuilder: FunctionalExpressionField<Double, *>.() -> Expression<Double> = {
            val x by binding()
            x * x + 2 * x + one
        }

        val expression = FunctionalExpressionField(RealField).expressionBuilder()
        assertEquals(expression(x to 1.0), 4.0)
    }
}
