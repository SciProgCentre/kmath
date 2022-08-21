/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.interpolation.interpolatePolynomials
import space.kscience.kmath.interpolation.splineInterpolator
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.real.map
import space.kscience.kmath.real.step
import space.kscience.plotly.Plotly
import space.kscience.plotly.UnstablePlotlyAPI
import space.kscience.plotly.makeFile
import space.kscience.plotly.models.functionXY
import space.kscience.plotly.scatter

@OptIn(UnstablePlotlyAPI::class)
fun main() {
    val function: Function1D<Double> = { x ->
        if (x in 30.0..50.0) {
            1.0
        } else {
            0.0
        }
    }
    val xs = 0.0..100.0 step 0.5
    val ys = xs.map(function)

    val polynomial: PiecewisePolynomial<Double> = DoubleField.splineInterpolator.interpolatePolynomials(xs, ys)

    val polyFunction = polynomial.asFunction(DoubleField, 0.0)

    Plotly.plot {
        scatter {
            name = "interpolated"
            functionXY(25.0..55.0, 0.1) { polyFunction(it) }
        }
        scatter {
            name = "original"
            functionXY(25.0..55.0, 0.1) { function(it) }
        }
    }.makeFile()
}