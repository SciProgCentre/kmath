package kscience.kmath.ast

import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.measureTime

internal class Test {
    @Test
    fun int() {
        val res = IntWasmBuilder(MstRing { number(100000000) + number(10000000) }).instance()
        assertEquals(110000000, res)
    }

    @Test
    fun real() {
        val res = RealWasmBuilder(MstExtendedField { number(100000000) + number(2).pow(10) }).instance()
        assertEquals(100001024.0, res)
    }

    @Test
    fun argsPassing() {
        val res = RealWasmBuilder(MstExtendedField { symbol("y") + symbol("x").pow(10) })
            .instance("x" to 2.0, "y" to 100000000.0)

        assertEquals(100001024.0, res)
    }

    @Test
    fun powFunction() {
        val expr = RealWasmBuilder(MstExtendedField { symbol("x").pow(1.0 / 6.0) }).instance
        assertEquals(0.9730585187140817, expr("x" to 0.8488554755054833))
    }

    @Test
    fun manyRuns() {
        println("Compiled")
        val times = 1_000_000
        var rng = Random(0)
        var sum1 = 0.0
        var sum2 = 0.0

        measureTime {
            val res = RealWasmBuilder(MstExtendedField { symbol("x").pow(1.0 / 6.0) }).instance
            repeat(times) { sum1 += res("x" to rng.nextDouble()) }
        }.also(::println)

        println("MST")
        rng = Random(0)

        measureTime {
            val res = RealField.mstInExtendedField { symbol("x").pow(1.0 / 6.0) }
            repeat(times) { sum2 += res("x" to rng.nextDouble()) }
        }.also(::println)

        assertEquals(sum1, sum2)
    }
}
