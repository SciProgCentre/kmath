/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.interpolation

import space.kscience.kmath.operations.DoubleField
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SplineInterpolatorTest {
    @Test
    fun testInterpolation() {
        val data = (0..10).map {
            val x = it.toDouble() / 5 * PI
            x to sin(x)
        }

        //val polynomial: PiecewisePolynomial<Double> = DoubleField.splineInterpolator.interpolatePolynomials(data)

        val function = DoubleField.splineInterpolator.interpolate(data, Double.NaN)

        assertEquals(Double.NaN, function(-1.0))
        assertEquals(sin(0.5), function(0.5), 0.1)
        assertEquals(sin(1.5), function(1.5), 0.1)
        assertEquals(sin(2.0), function(2.0), 0.1)
    }
}
