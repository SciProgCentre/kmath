package space.kscience.kmath.series


import kotlinx.html.FlowContent
import kotlinx.html.h1
import space.kscience.kmath.operations.DoubleBufferOps
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.operations.toList
import space.kscience.kmath.stat.KMComparisonResult
import space.kscience.kmath.stat.ksComparisonStatistic
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.slice
import space.kscience.plotly.*
import kotlin.math.PI

fun main() = with(Double.algebra.bufferAlgebra.seriesAlgebra()) {
    fun FlowContent.plotSeries(buffer: Buffer<Double>) {
        val ls = buffer.labels
        plot {
            scatter {
                x.numbers = ls
                y.numbers = buffer.toList()
            }
            layout {
                xaxis {
                    range(0.0..100.0)
                }
            }
        }
    }


    val s1 = series(100) { sin(2 * PI * it / 100) + 1.0 }
    val s2 = s1.slice(20..50).moveTo(40)

    val s3: Buffer<Double> = s1.zip(s2) { l, r -> l + r } //s1 + s2
    val s4 = DoubleBufferOps.ln(s3)

    val kmTest: KMComparisonResult<Double> = ksComparisonStatistic(s1, s2)

    Plotly.page {
        h1 { +"This is my plot" }
        plotSeries(s1)
        plotSeries(s2)
        plotSeries(s4)
    }.makeFile()

}