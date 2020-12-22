package kscience.kmath.commons.expressions

import kscience.kmath.expressions.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal inline fun diff(
    order: Int,
    vararg parameters: Pair<Symbol, Double>,
    block: DerivativeStructureField.() -> Unit,
): Unit {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    DerivativeStructureField(order, mapOf(*parameters)).run(block)
}

internal class AutoDiffTest {
    private val x by symbol
    private val y by symbol

    @Test
    fun derivativeStructureFieldTest() {
        diff(2, x to 1.0, y to 1.0) {
            val x = bind(x)//by binding()
            val y = symbol("y")
            val z = x * (-sin(x * y) + y) + 2.0
            println(z.derivative(x))
            println(z.derivative(y,x))
            assertEquals(z.derivative(x, y), z.derivative(y, x))
            //check that improper order cause failure
            assertFails { z.derivative(x,x,y) }
        }
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
        assertEquals(2.0, f.derivative(x, x)(x to 1.234, y to -2.0))
        assertEquals(2.0, f.derivative(x, y)(x to 1.0, y to 2.0))
    }
}
