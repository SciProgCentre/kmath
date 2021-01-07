package kscience.kmath.gsl

import org.gnu.gsl.gsl_block_calloc
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ErrorsHandlingTest {
    @Test
    fun blockAllocation() {
        assertFailsWith<GslException> {
            ensureHasGslErrorHandler()
            gsl_block_calloc(ULong.MAX_VALUE)
        }
    }

    @Test
    fun matrixAllocation() {
        assertFailsWith<GslException> {
            GslRealMatrixContext { produce(Int.MAX_VALUE, Int.MAX_VALUE) { _, _ -> 0.0 } }
        }
    }

    @Test
    fun useOfClosedObject() {
        val mat = GslRealMatrixContext { produce(1, 1) { _, _ -> 0.0 } }
        assertFailsWith<IllegalStateException> { mat.colNum }
        assertFailsWith<IllegalStateException> { mat.rowNum }
        assertFailsWith<IllegalStateException> { mat[0, 0] }
        assertFailsWith<IllegalStateException> { mat.copy() }
        assertFailsWith<IllegalStateException> { println(mat == mat) }
    }
}
