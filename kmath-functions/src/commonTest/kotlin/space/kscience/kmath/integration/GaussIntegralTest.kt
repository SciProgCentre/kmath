/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Float64Field
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(UnstableKMathAPI::class)
class GaussIntegralTest {
    @Test
    fun gaussSin() {
        val res = Float64Field.gaussIntegrator.integrate(0.0..2 * PI) { x ->
            sin(x)
        }
        assertEquals(0.0, res.value, 1e-2)
    }

    @Test
    fun gaussUniform() {
        val res = Float64Field.gaussIntegrator.integrate(35.0..100.0) { x ->
            if (x in 30.0..50.0) {
                1.0
            } else {
                0.0
            }
        }
        assertEquals(15.0, res.value, 0.5)
    }


}