package kscience.kmath.gsl

import kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RealTest {
    @Test
    fun testScale() = GslRealMatrixContext {
        val ma = GslRealMatrixContext.produce(10, 10) { _, _ -> 0.1 }
        val mb = (ma * 20.0)
        assertEquals(mb[0, 1], 2.0)
        mb.close()
        ma.close()
    }
}
