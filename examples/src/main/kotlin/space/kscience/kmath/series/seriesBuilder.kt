/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series


import space.kscience.kmath.structures.*
import space.kscience.plotly.*
import space.kscience.plotly.models.Scatter
import space.kscience.plotly.models.ScatterMode
import kotlin.random.Random

fun main(): Unit = with(Double.seriesAlgebra()) {

    val random = Random(1234)

    val arrayOfRandoms = DoubleArray(20) { random.nextDouble() }

    val series1: Float64Buffer = arrayOfRandoms.asBuffer()
    val series2: Series<Float64> = series1.moveBy(3)

    val res = series2 - series1

    println(res.size)

    println(res)

    fun Plot.series(name: String, buffer: Buffer<Float64>, block: Scatter.() -> Unit = {}) {
        scatter {
            this.name = name
            x.numbers = buffer.offsetIndices
            y.doubles = buffer.toDoubleArray()
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