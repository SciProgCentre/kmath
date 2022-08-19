/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.interpolation

import space.kscience.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LinearInterpolatorTest {
    @Test
    fun testInterpolation() {
        val data = listOf(
            0.0 to 0.0,
            1.0 to 1.0,
            2.0 to 3.0,
            3.0 to 4.0
        )

        //val polynomial: PiecewisePolynomial<Double> = DoubleField.linearInterpolator.interpolatePolynomials(data)
        val function = DoubleField.linearInterpolator.interpolate(data)
        assertEquals(null, function(-1.0))
        assertEquals(0.5, function(0.5))
        assertEquals(2.0, function(1.5))
        assertEquals(3.0, function(2.0))
    }
}
