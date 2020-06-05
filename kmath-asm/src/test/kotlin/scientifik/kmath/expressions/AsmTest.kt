package scientifik.kmath.expressions

import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

class AsmTest {
    @Test
    fun test() {
        val expr = AsmSumExpression(AsmConstantExpression(1.0), AsmVariableExpression("x"))

        val gen = AsmGenerationContext(
            java.lang.Double::class.java,
            RealField,
            "MyAsmCompiled"
        )

        expr.invoke(gen)
        val compiled = gen.generate()
        val value = compiled.evaluate(mapOf("x" to 25.0))
        assertEquals(26.0, value)
    }
}
