package space.kscience.kmath.expressions

import space.kscience.kmath.misc.symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ExpressionFieldTest {
    val x by symbol

    @Test
    fun testExpression() {
        val expression = FunctionalExpressionField(DoubleField).invoke {
            val x by binding()
            x * x + 2 * x + one
        }

        assertEquals(expression(x to 1.0), 4.0)
        assertFails { expression() }
    }

    @Test
    fun separateContext() {
        fun <T> FunctionalExpressionField<T, *>.expression(): Expression<T> {
            val x by binding()
            return x * x + 2 * x + one
        }

        val expression = FunctionalExpressionField(DoubleField).expression()
        assertEquals(expression(x to 1.0), 4.0)
    }

    @Test
    fun valueExpression() {
        val expressionBuilder: FunctionalExpressionField<Double, *>.() -> Expression<Double> = {
            val x by binding()
            x * x + 2 * x + one
        }

        val expression = FunctionalExpressionField(DoubleField).expressionBuilder()
        assertEquals(expression(x to 1.0), 4.0)
    }
}
