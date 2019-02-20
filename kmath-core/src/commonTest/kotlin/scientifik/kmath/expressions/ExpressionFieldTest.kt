package scientifik.kmath.expressions

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionFieldTest {
    @Test
    fun testExpression() {
        val context = ExpressionField(RealField)
        val expression = with(context) {
            val x = variable("x", 2.0)
            x * x + 2 * x + one
        }
        assertEquals(expression("x" to 1.0), 4.0)
        assertEquals(expression(), 9.0)
    }

    @Test
    fun testComplex() {
        val context = ExpressionField(ComplexField)
        val expression = with(context) {
            val x = variable("x", Complex(2.0, 0.0))
            x * x + 2 * x + one
        }
        assertEquals(expression("x" to Complex(1.0, 0.0)), Complex(4.0, 0.0))
        assertEquals(expression(), Complex(9.0, 0.0))
    }

    @Test
    fun separateContext() {
        fun <T> ExpressionField<T>.expression(): Expression<T> {
            val x = variable("x")
            return x * x + 2 * x + one
        }

        val expression = ExpressionField(RealField).expression()
        assertEquals(expression("x" to 1.0), 4.0)
    }

    @Test
    fun valueExpression() {
        val expressionBuilder: ExpressionField<Double>.() -> Expression<Double> = {
            val x = variable("x")
            x * x + 2 * x + one
        }

        val expression = ExpressionField(RealField).expressionBuilder()
        assertEquals(expression("x" to 1.0), 4.0)
    }
}