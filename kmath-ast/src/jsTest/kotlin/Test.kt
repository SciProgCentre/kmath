package kscience.kmath.ast

import kscience.kmath.expressions.invoke
import kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Test {
    @Test
    fun int() {
        val res = IntWasmBuilder().compile(MstRing { number(100000000) + number(10000000) })()
        assertEquals(110000000, res)
    }

    @Test
    fun real() {
        val res = RealWasmBuilder().compile(MstExtendedField { number(100000000) + number(2).pow(10) })()
        assertEquals(100001024.0, res)
    }

    @Test
    fun argsPassing() {
        val res = RealWasmBuilder()
            .compile(MstExtendedField { symbol("y") + symbol("x").pow(10) })("x" to 2.0, "y" to 100000000.0)

        assertEquals(100001024.0, res)
    }
}
