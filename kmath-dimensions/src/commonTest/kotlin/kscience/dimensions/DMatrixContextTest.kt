package kscience.dimensions

import kscience.kmath.dimensions.D2
import kscience.kmath.dimensions.D3
import kscience.kmath.dimensions.DMatrixContext
import kscience.kmath.dimensions.one
import kotlin.test.Test

internal class DMatrixContextTest {
    @Test
    fun testDimensionSafeMatrix() {
        val res = with(DMatrixContext.real) {
            val m = produce<D2, D2> { i, j -> (i + j).toDouble() }

            //The dimension of `one()` is inferred from type
            (m + one())
        }
    }

    @Test
    fun testTypeCheck() {
        val res = with(DMatrixContext.real) {
            val m1 = produce<D2, D3> { i, j -> (i + j).toDouble() }
            val m2 = produce<D3, D2> { i, j -> (i + j).toDouble() }

            //Dimension-safe addition
            m1.transpose() + m2
        }
    }
}
