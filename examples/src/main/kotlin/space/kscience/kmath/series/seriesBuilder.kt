/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series


import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer
import space.kscience.kmath.structures.toDoubleArray
import space.kscience.plotly.*
import space.kscience.plotly.models.Scatter
import space.kscience.plotly.models.ScatterMode
import kotlin.random.Random

fun main(): Unit = with(Double.seriesAlgebra()) {

    val random = Random(1234)

    val series1: Series<Float64> = Float64Buffer(20) { random.nextDouble() }.asSeries()
    val series2: Series<Float64> = series1.moveBy(3)

    val res = series2 - series1

    println(res.size)

    println(res)

    fun Plot.series(name: String, series: Series<Float64>, block: Scatter.() -> Unit = {}) {
        scatter {
            this.name = name
            x.numbers = series.indices
            y.doubles = series.origin.toDoubleArray()
            block()
        }
    }

    Plotly.plot {
        series("series1", series1)
        series("series2", series2)
        series("dif", res) {
            mode = ScatterMode.lines
            line.color("magenta")
        }
    }.makeFile(resourceLocation = ResourceLocation.REMOTE)
}