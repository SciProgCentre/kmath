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
    ): Unit = assertEquals(
        expectedValue, AsmGenerationContext(clazz, algebra, "TestAsmCompiled")
            .also(expr::invoke)
            .generate()
            .invoke(arguments)
    )

    @Suppress("UNCHECKED_CAST")
    private fun testDoubleExpressionValue(
        expectedValue: Double,
        expr: AsmExpression<Double>,
        arguments: Map<String, Double>,
        algebra: Algebra<Double> = RealField,
        clazz: Class<Double> = java.lang.Double::class.java as Class<Double>
    ): Unit = testExpressionValue(expectedValue, expr, arguments, algebra, clazz)

    @Test
    fun testSum() = testDoubleExpressionValue(
        25.0,
        AsmSumExpression(AsmConstantExpression(1.0), AsmVariableExpression("x")),
        mapOf("x" to 24.0)
    )

    @Test
    fun testConst(): Unit = testDoubleExpressionValue(
        123.0,
        AsmConstantExpression(123.0),
        mapOf()
    )

    @Test
    fun testDiv(): Unit = testDoubleExpressionValue(
        0.5,
        AsmDivExpression(AsmConstantExpression(1.0), AsmConstantExpression(2.0)),
        mapOf()
    )

    @Test
    fun testProduct(): Unit = testDoubleExpressionValue(
        25.0,
        AsmProductExpression(AsmVariableExpression("x"), AsmVariableExpression("x")),
        mapOf("x" to 5.0)
    )

    @Test
    fun testCProduct(): Unit = testDoubleExpressionValue(
        25.0,
        AsmConstProductExpression(AsmVariableExpression("x"), 5.0),
        mapOf("x" to 5.0)
    )

    @Test
    fun testCProductWithOtherTypeNumber(): Unit = testDoubleExpressionValue(
        25.0,
        AsmConstProductExpression(AsmVariableExpression("x"), 5f),
        mapOf("x" to 5.0)
    )

    object CustomZero : Number() {
        override fun toByte(): Byte = 0
        override fun toChar(): Char = 0.toChar()
        override fun toDouble(): Double = 0.0
        override fun toFloat(): Float = 0f
        override fun toInt(): Int = 0
        override fun toLong(): Long = 0L
        override fun toShort(): Short = 0
    }

    @Test
    fun testCProductWithCustomTypeNumber(): Unit = testDoubleExpressionValue(
        0.0,
        AsmConstProductExpression(AsmVariableExpression("x"), CustomZero),
        mapOf("x" to 5.0)
    )

    @Test
    fun testVar(): Unit = testDoubleExpressionValue(
        10000.0,
        AsmVariableExpression("x"),
        mapOf("x" to 10000.0)
    )

    @Test
    fun testVarWithDefault(): Unit = testDoubleExpressionValue(
        10000.0,
        AsmVariableExpression("x", 10000.0),
        mapOf()
    )
}
