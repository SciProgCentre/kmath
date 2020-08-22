package scientifik.kmath.commons.expressions

import scientifik.kmath.expressions.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.Test
import kotlin.test.assertEquals

inline fun <R> diff(order: Int, vararg parameters: Pair<String, Double>, block: DerivativeStructureField.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return DerivativeStructureField(order, mapOf(*parameters)).run(block)
}

class AutoDiffTest {
    @Test
    fun derivativeStructureFieldTest() {
        val res = diff(3, "x" to 1.0, "y" to 1.0) {
            val x by variable
            val y = variable("y")
            val z = x * (-sin(x * y) + y)
            z.deriv("x")
        }
    }

    @Test
    fun autoDifTest() {
        val f = DiffExpression {
            val x by variable
            val y by variable
            x.pow(2) + 2 * x * y + y.pow(2) + 1
        }

        assertEquals(10.0, f("x" to 1.0, "y" to 2.0))
        assertEquals(6.0, f.derivative("x")("x" to 1.0, "y" to 2.0))
    }
}