package scientifik.kmath.expressions

import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

class AsmTest {
    private fun <T> testExpressionValue(
        expectedValue: T,
        expr: AsmExpression<T>,
        arguments: Map<String, T>,
        algebra: Algebra<T>,
        clazz: Class<*>
    ) {
        assertEquals(
            expectedValue, AsmGenerationContext(
                clazz,
                algebra,
                "TestAsmCompiled"
            ).also(expr::invoke).generate().evaluate(arguments)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun testDoubleExpressionValue(
        expectedValue: Double,
        expr: AsmExpression<Double>,
        arguments: Map<String, Double>,
        algebra: Algebra<Double> = RealField,
        clazz: Class<Double> = java.lang.Double::class.java as Class<Double>
    ) = testExpressionValue(expectedValue, expr, arguments, algebra, clazz)

    @Test
    fun testSum() = testDoubleExpressionValue(
        25.0,
        AsmSumExpression(AsmConstantExpression(1.0), AsmVariableExpression("x")),
        mapOf("x" to 24.0)
    )

    @Test
    fun testConst() = testDoubleExpressionValue(
        123.0,
        AsmConstantExpression(123.0),
        mapOf()
    )

    @Test
    fun testDiv() = testDoubleExpressionValue(
        0.5,
        AsmDivExpression(AsmConstantExpression(1.0), AsmConstantExpression(2.0)),
        mapOf()
    )

    @Test
    fun testProduct() = testDoubleExpressionValue(
        25.0,
        AsmProductExpression(AsmVariableExpression("x"), AsmVariableExpression("x")),
        mapOf("x" to 5.0)
    )

    @Test
    fun testCProduct() = testDoubleExpressionValue(
        25.0,
        AsmConstProductExpression(AsmVariableExpression("x"), 5.0),
        mapOf("x" to 5.0)
    )

    @Test
    fun testVar() = testDoubleExpressionValue(
        10000.0,
        AsmVariableExpression("x"),
        mapOf("x" to 10000.0)
    )

    @Test
    fun testVarWithDefault() = testDoubleExpressionValue(
        10000.0,
        AsmVariableExpression("x", 10000.0),
        mapOf()
    )
}
