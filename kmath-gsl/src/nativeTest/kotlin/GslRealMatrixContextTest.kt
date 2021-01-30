package kscience.kmath.gsl

import kscience.kmath.linear.Matrix
import kscience.kmath.linear.RealMatrixContext
import kscience.kmath.operations.invoke
import kscience.kmath.structures.RealBuffer
import kscience.kmath.structures.asSequence
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.measureTime

internal class GslRealMatrixContextTest {
    @Test
    fun testScale() = GslRealMatrixContext {
        val ma = produce(10, 10) { _, _ -> 0.1 }
        val mb = ma * 20.0
        assertEquals(mb[0, 1], 2.0)
    }

    @Test
    fun testDotOfMatrixAndVector() = GslRealMatrixContext {
        val ma = produce(2, 2) { _, _ -> 100.0 }
        val vb = RealBuffer(2) { 0.1 }
        val res1 = ma dot vb
        val res2 = RealMatrixContext { ma dot vb }
        println(res1.asSequence().toList())
        println(res2.asSequence().toList())
        assertTrue(res1.contentEquals(res2))
    }

    @Test
    fun testDotOfMatrixAndMatrix() = GslRealMatrixContext {
        val ma = produce(2, 2) { _, _ -> 100.0 }
        val mb = produce(2, 2) { _, _ -> 100.0 }
        val res1: Matrix<Double> = ma dot mb
        val res2: Matrix<Double> = RealMatrixContext { ma dot mb }
        assertEquals(res1, res2)
    }

    @Test
    fun testManyCalls() = GslRealMatrixContext {
        val expected: Matrix<Double> = RealMatrixContext {
            val rng = Random(0)
            var prod = produce(20, 20) { _, _ -> rng.nextDouble() }
            val mult = produce(20, 20) { _, _ -> rng.nextDouble() }
            measureTime { repeat(100) { prod = prod dot mult } }.also(::println)
            prod
        }

        val rng = Random(0)
        var prod: Matrix<Double> = produce(20, 20) { _, _ -> rng.nextDouble() }
        val mult = produce(20, 20) { _, _ -> rng.nextDouble() }
        measureTime { repeat(100) { prod = prod dot mult } }.also(::println)
        assertEquals(expected, prod)
    }
}
