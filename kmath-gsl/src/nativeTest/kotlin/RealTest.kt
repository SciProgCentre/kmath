package kscience.kmath.gsl

import kotlinx.io.use
import kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RealTest {
    @Test
    fun testScale() = GslRealMatrixContext {
        GslRealMatrixContext.produce(10, 10) { _, _ -> 0.1 }.use { ma ->
            (ma * 20.0).use { mb -> assertEquals(mb[0, 1], 2.0) }
        }
    }
}
