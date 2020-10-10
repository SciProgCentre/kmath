package kscience.kmath.gsl

import kscience.kmath.linear.RealMatrixContext
import kscience.kmath.operations.invoke
import kscience.kmath.structures.RealBuffer
import kscience.kmath.structures.asIterable
import kscience.kmath.structures.asSequence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RealTest {
    @Test
    fun testScale() = GslRealMatrixContext {
        val ma = GslRealMatrixContext.produce(10, 10) { _, _ -> 0.1 }
        val mb = (ma * 20.0)
        assertEquals(mb[0, 1], 2.0)
        mb.close()
        ma.close()
    }

    @Test
    fun testDotOfMatrixAndVector() {
        val ma = GslRealMatrixContext.produce(2, 2) { _, _ -> 100.0 }
        val vb = RealBuffer(2) { 0.1 }
        val res1 = GslRealMatrixContext { ma dot vb }
        val res2 = RealMatrixContext { ma dot vb }
        println(res1.asSequence().toList())
        println(res2.asSequence().toList())
        assertTrue(res1.contentEquals(res2))
        res1.close()
    }

    @Test
    fun testDotOfMatrixAndMatrix() {
        val ma = GslRealMatrixContext.produce(2, 2) { _, _ -> 100.0 }
        val mb = GslRealMatrixContext.produce(2, 2) { _, _ -> 100.0 }
        val res1 = GslRealMatrixContext { ma dot mb }
        val res2 = RealMatrixContext { ma dot mb }
        println(res1.rows.asIterable().map { it.asSequence() }.flatMap(Sequence<*>::toList))
        println(res2.rows.asIterable().map { it.asSequence() }.flatMap(Sequence<*>::toList))
        assertEquals(res1, res2)
        ma.close()
        mb.close()
    }
}
