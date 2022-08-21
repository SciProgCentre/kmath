/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.interpolation.SplineInterpolator
import space.kscience.kmath.interpolation.interpolatePolynomials
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.plotly.Plotly
import space.kscience.plotly.UnstablePlotlyAPI
import space.kscience.plotly.makeFile
import space.kscience.plotly.models.functionXY
import space.kscience.plotly.scatter
import kotlin.math.PI
import kotlin.math.sin

@OptIn(UnstablePlotlyAPI::class)
fun main() {
    val data = (0..10).map {
        val x = it.toDouble() / 5 * PI
        x to sin(x)
    }

    val polynomial: PiecewisePolynomial<Double> = SplineInterpolator(
        DoubleField, ::DoubleBuffer
    ).interpolatePolynomials(data)

    val function = polynomial.asFunction(DoubleField, 0.0)

    val cmInterpolate = org.apache.commons.math3.analysis.interpolation.SplineInterpolator().interpolate(
        data.map { it.first }.toDoubleArray(),
        data.map { it.second }.toDoubleArray()
    )

    Plotly.plot {
        scatter {
            name = "interpolated"
            x.numbers = data.map { it.first }
            y.numbers = x.doubles.map { function(it) }
        }
        scatter {
            name = "original"
            functionXY(0.0..(2 * PI), 0.1) { sin(it) }
        }
        scatter {
            name = "cm"
            x.numbers = data.map { it.first }
            y.numbers = x.doubles.map { cmInterpolate.value(it) }
        }
    }.makeFile()
}