package scientifik.kmath.expressions

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import scientifik.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldExpressionContextTest {
    @Test
    fun testExpression() {
        val context = FieldExpressionContext(DoubleField)
        val expression = with(context) {
            val x = variable("x", 2.0)
            x * x + 2 * x + 1.0
        }
        assertEquals(expression("x" to 1.0), 4.0)
        assertEquals(expression(), 9.0)
    }

    @Test
    fun testComplex() {
        val context = FieldExpressionContext(ComplexField)
        val expression = with(context) {
            val x = variable("x", Complex(2.0, 0.0))
            x * x + 2 * x + 1.0
        }
        assertEquals(expression("x" to Complex(1.0, 0.0)), Complex(4.0, 0.0))
        assertEquals(expression(), Complex(9.0, 0.0))
    }
}