package scientifik.kmath.expressions

import scientifik.kmath.expressions.asm.AsmExpression
import scientifik.kmath.expressions.asm.AsmExpressionField
import scientifik.kmath.expressions.asm.asmField
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

class AsmTest {
    private fun testDoubleExpression(
        expected: Double?,
        arguments: Map<String, Double> = emptyMap(),
        block: AsmExpressionField<Double>.() -> AsmExpression<Double>
    ): Unit = assertEquals(expected = expected, actual = asmField(RealField, block)(arguments))

    @Test
    fun testConstantsSum() = testDoubleExpression(16.0) { const(8.0) + 8.0 }

    @Test
    fun testVarsSum() = testDoubleExpression(1000.0, mapOf("x" to 500.0)) { variable("x") + 500.0 }

    @Test
    fun testProduct() = testDoubleExpression(24.0) { const(4.0) * const(6.0) }

    @Test
    fun testConstantProduct() = testDoubleExpression(984.0) { const(8.0) * 123 }

    @Test
    fun testSubtraction() = testDoubleExpression(2.0) { const(4.0) - 2.0 }

    @Test
    fun testDivision() = testDoubleExpression(64.0) { const(128.0) / 2 }

    @Test
    fun testDirectCall() = testDoubleExpression(4096.0) { binaryOperation("*", const(64.0), const(64.0)) }

//    @Test
//    fun testSine() = testDoubleExpression(0.0) { unaryOperation("sin", const(PI)) }
}
