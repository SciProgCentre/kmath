/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.dimensions

import space.kscience.kmath.dimensions.D2
import space.kscience.kmath.dimensions.D3
import space.kscience.kmath.dimensions.DMatrixContext
import space.kscience.kmath.dimensions.one
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE")
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
