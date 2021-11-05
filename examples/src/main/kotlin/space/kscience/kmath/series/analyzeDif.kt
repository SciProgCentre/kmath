package space.kscience.kmath.series


import net.jafama.StrictFastMath.abs
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.toList
import space.kscience.kmath.structures.Buffer
import space.kscience.plotly.Plotly
import space.kscience.plotly.makeFile
import space.kscience.plotly.scatter
import kotlin.math.PI
import kotlin.math.max

fun main() = Double.algebra.bufferAlgebra.seriesAlgebra(0..100).invoke {
    fun Buffer<Double>.plot() {
        val ls = labels
        Plotly.plot {
            scatter {
                x.numbers = ls
                y.numbers = toList()
            }
        }.makeFile()
    }


    val s1 = series(100) { sin(2 * PI * it / 100) }
    val s2 = series(100) { 1.0 }

    (s1 - s2).plot()

    // Kolmogorov-Smirnov test statistic
    val kst = (s1 - s2).fold(0.0) { sup, arg -> max(sup, abs(arg))}


}