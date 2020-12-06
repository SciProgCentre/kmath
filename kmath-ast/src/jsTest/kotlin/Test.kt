package kscience.kmath.ast

import kscience.kmath.expressions.invoke
import kscience.kmath.operations.IntRing
import kscience.kmath.operations.RealField
import kscience.kmath.wasm.compile
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.measureTime

internal class Test {
    @Test
    fun int() {
        val res = IntRing.mstInRing { number(100000000) + number(10000000) }.compile()()
        assertEquals(110000000, res)
    }

    @Test
    fun real() {
        val res = RealField.mstInExtendedField { number(100000000) + number(2).pow(10) }.compile()()
        assertEquals(100001024.0, res)
    }

    @Test
    fun argsPassing() {
        val res = RealField
            .mstInExtendedField { symbol("y") + symbol("x").pow(10) }
            .compile()("x" to 2.0, "y" to 100000000.0)

        assertEquals(100001024.0, res)
    }

    @Test
    fun powFunction() {
        val expr = RealField.mstInExtendedField { symbol("x").pow(1.0 / 6.0) }.compile()
        assertEquals(0.9730585187140817, expr("x" to 0.8488554755054833))
    }

    @Test
    fun manyRuns() {
        println("Compiled")
        val times = 1_000_000
        var rng = Random(0)
        var sum1 = 0.0
        var sum2 = 0.0
        val e2 = RealField.mstInExtendedField { symbol("x").pow(1.0 / 6.0) }
        val e1 = e2.compile()
        measureTime { repeat(times) { sum1 += e1("x" to rng.nextDouble()) } }.also(::println)
        println("MST")
        rng = Random(0)
        measureTime { repeat(times) { sum2 += e2("x" to rng.nextDouble()) } }.also(::println)
        assertEquals(sum1, sum2)
    }
}
