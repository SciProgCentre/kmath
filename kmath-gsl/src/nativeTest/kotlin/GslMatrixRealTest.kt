package kscience.kmath.gsl

import kotlin.test.Test
import kotlin.test.assertEquals

internal class GslMatrixRealTest {
    @Test
    fun dimensions() = GslRealMatrixContext {
        val mat = produce(42, 24) { _, _ -> 0.0 }
        assertEquals(42, mat.rowNum)
        assertEquals(24, mat.colNum)
    }

    @Test
    fun get() = GslRealMatrixContext {
        val mat = produce(1, 1) { _, _ -> 42.0 }
        assertEquals(42.0, mat[0, 0])
    }
}
