package kscience.kmath.commons.expressions

import kscience.kmath.expressions.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.Test
import kotlin.test.assertEquals

internal inline fun <R> diff(
    order: Int,
    vararg parameters: Pair<Symbol, Double>,
    block: DerivativeStructureField.() -> R,
): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return DerivativeStructureField(order, mapOf(*parameters)).run(block)
}

internal class AutoDiffTest {
    private val x by symbol
    private val y by symbol

    @Test
    fun derivativeStructureFieldTest() {
        val res: Double = diff(3, x to 1.0, y to 1.0) {
            val x = bind(x)//by binding()
            val y = symbol("y")
            val z = x * (-sin(x * y) + y)
            z.derivative(x)
        }
        println(res)
    }

    @Test
    fun autoDifTest() {
        val f = DerivativeStructureExpression {
            val x by binding()
            val y by binding()
            x.pow(2) + 2 * x * y + y.pow(2) + 1
        }

        assertEquals(10.0, f(x to 1.0, y to 2.0))
        assertEquals(6.0, f.derivative(x)(x to 1.0, y to 2.0))
    }
}
