package space.kscience.kmath.commons.fit

import kotlinx.html.br
import kotlinx.html.h3
import kscience.plotly.*
import kscience.plotly.models.ScatterMode
import kscience.plotly.models.TraceValues
import space.kscience.kmath.commons.optimization.chiSquared
import space.kscience.kmath.commons.optimization.minimize
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.optimization.FunctionOptimization
import space.kscience.kmath.optimization.OptimizationResult
import space.kscience.kmath.real.DoubleVector
import space.kscience.kmath.real.map
import space.kscience.kmath.real.step
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.structures.asIterable
import space.kscience.kmath.structures.toList
import kotlin.math.pow
import kotlin.math.sqrt

//Forward declaration of symbols that will be used in expressions.
// This declaration is required for
private val a by symbol
private val b by symbol
private val c by symbol

/**
 * Shortcut to use buffers in plotly
 */
operator fun TraceValues.invoke(vector: DoubleVector) {
    numbers = vector.asIterable()
}

/**
 * Least squares fie with auto-differentiation. Uses `kmath-commons` and `kmath-for-real` modules.
 */
suspend fun main() {
    //A generator for a normally distributed values
    val generator = NormalDistribution(2.0, 7.0)

    //A chain/flow of random values with the given seed
    val chain = generator.sample(RandomGenerator.default(112667))


    //Create a uniformly distributed x values like numpy.arrange
    val x = 1.0..100.0 step 1.0


    //Perform an operation on each x value (much more effective, than numpy)
    val y = x.map {
        val value = it.pow(2) + it + 1
        value + chain.next() * sqrt(value)
    }
    // this will also work, but less effective:
    // val y = x.pow(2)+ x + 1 + chain.nextDouble()

    // create same errors for all xs
    val yErr = y.map { sqrt(it) }//RealVector.same(x.size, sigma)

    // compute differentiable chi^2 sum for given model ax^2 + bx + c
    val chi2 = FunctionOptimization.chiSquared(x, y, yErr) { x1 ->
        //bind variables to autodiff context
        val a = bindSymbol(a)
        val b = bindSymbol(b)
        //Include default value for c if it is not provided as a parameter
        val c = bindSymbolOrNull(c) ?: one
        a * x1.pow(2) + b * x1 + c
    }

    //minimize the chi^2 in given starting point. Derivatives are not required, they are already included.
    val result: OptimizationResult<Double> = chi2.minimize(a to 1.5, b to 0.9, c to 1.0)

    //display a page with plot and numerical results
    val page = Plotly.page {
        plot {
            scatter {
                mode = ScatterMode.markers
                x(x)
                y(y)
                error_y {
                    array = yErr.toList()
                }
                name = "data"
            }
            scatter {
                mode = ScatterMode.lines
                x(x)
                y(x.map { result.point[a]!! * it.pow(2) + result.point[b]!! * it + 1 })
                name = "fit"
            }
        }
        br()
        h3 {
            +"Fit result: $result"
        }
        h3 {
            +"Chi2/dof = ${result.value / (x.size - 3)}"
        }
    }

    page.makeFile()
}
