package kscience.kmath.gsl

import kotlinx.cinterop.memScoped
import kscience.kmath.linear.RealMatrixContext
import kscience.kmath.operations.invoke
import kscience.kmath.structures.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RealTest {
    @Test
    fun testScale() = memScoped {
        (GslRealMatrixContext(this)) {
            val ma = produce(10, 10) { _, _ -> 0.1 }
            val mb = (ma * 20.0)
            assertEquals(mb[0, 1], 2.0)
        }
    }

    @Test
    fun testDotOfMatrixAndVector() = memScoped {
        (GslRealMatrixContext(this)) {
            val ma = produce(2, 2) { _, _ -> 100.0 }
            val vb = RealBuffer(2) { 0.1 }
            val res1 = ma dot vb
            val res2 = RealMatrixContext { ma dot vb }
            println(res1.asSequence().toList())
            println(res2.asSequence().toList())
            assertTrue(res1.contentEquals(res2))
        }
    }

    @Test
    fun testDotOfMatrixAndMatrix() = memScoped {
        (GslRealMatrixContext(this)) {
            val ma = produce(2, 2) { _, _ -> 100.0 }
            val mb = produce(2, 2) { _, _ -> 100.0 }
            val res1: Matrix<Double> = ma dot mb
            val res2: Matrix<Double> = RealMatrixContext { ma dot mb }
            println(res1.rows.asIterable().map { it.asSequence() }.flatMap(Sequence<*>::toList))
            println(res2.rows.asIterable().map { it.asSequence() }.flatMap(Sequence<*>::toList))
            assertEquals(res1, res2)
        }
    }
}
