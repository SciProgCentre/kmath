package space.kscience.kmath.series


import kotlinx.html.h1
import kotlinx.html.p
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


    fun Plot.plotSeries(name: String, buffer: Buffer<Double>) {
        scatter {
            this.name = name
            x.numbers = buffer.labels
            y.numbers = buffer.toList()
        }
    }


    val s1 = series(100) { sin(2 * PI * it / 100) + 1.0 }

    val s2 = s1.slice(20..50).moveTo(40)

    val s3: Buffer<Double> = s1.zip(s2) { l, r -> l + r } //s1 + s2
    val s4 = s3.map { ln(it) }

    val kmTest: KMComparisonResult<Double> = ksComparisonStatistic(s1, s2)

    Plotly.page {
        h1 { +"This is my plot" }
        p{
            +"Kolmogorov-smirnov test for s1 and s2: ${kmTest.value}"
        }
        plot{
            plotSeries("s1", s1)
            plotSeries("s2", s2)
            plotSeries("s3", s3)
            plotSeries("s4", s4)
            layout {
                xaxis {
                    range(0.0..100.0)
                }
            }
        }

    }.makeFile()

}