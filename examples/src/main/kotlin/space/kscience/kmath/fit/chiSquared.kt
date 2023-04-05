/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.fit

import kotlinx.html.br
import kotlinx.html.h3
import space.kscience.kmath.commons.optimization.CMOptimizer
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.expressions.autodiff
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.asIterable
import space.kscience.kmath.operations.toList
import space.kscience.kmath.optimization.FunctionOptimizationTarget
import space.kscience.kmath.optimization.optimizeWith
import space.kscience.kmath.optimization.resultPoint
import space.kscience.kmath.optimization.resultValue
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.real.DoubleVector
import space.kscience.kmath.real.map
import space.kscience.kmath.real.step
import space.kscience.kmath.stat.chiSquaredExpression
import space.kscience.plotly.*
import space.kscience.plotly.models.ScatterMode
import space.kscience.plotly.models.TraceValues
import kotlin.math.pow
import kotlin.math.sqrt

// Forward declaration of symbols that will be used in expressions.
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
    val generator = NormalDistribution(0.0, 1.0)

    //A chain/flow of random values with the given seed
    val chain = generator.sample(RandomGenerator.default(112667))


    //Create a uniformly distributed x values like numpy.arrange
    val x = 1.0..100.0 step 1.0


    //Perform an operation on each x value (much more effective, than numpy)
    val y = x.map { it ->
        val value = it.pow(2) + it + 1
        value + chain.next() * sqrt(value)
    }
    // this will also work, but less effective:
    // val y = x.pow(2)+ x + 1 + chain.nextDouble()

    // create same errors for all xs
    val yErr = y.map { sqrt(it) }//RealVector.same(x.size, sigma)

    // compute differentiable chi^2 sum for given model ax^2 + bx + c
    val chi2 = Double.autodiff.chiSquaredExpression(x, y, yErr) { arg ->
        //bind variables to autodiff context
        val a = bindSymbol(a)
        val b = bindSymbol(b)
        //Include default value for c if it is not provided as a parameter
        val c = bindSymbolOrNull(c) ?: one
        a * arg.pow(2) + b * arg + c
    }

    //minimize the chi^2 in given starting point. Derivatives are not required, they are already included.
    val result = chi2.optimizeWith(
        CMOptimizer,
        mapOf(a to 1.5, b to 0.9, c to 1.0),
        FunctionOptimizationTarget.MINIMIZE
    )

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
                y(x.map { result.resultPoint[a]!! * it.pow(2) + result.resultPoint[b]!! * it + 1 })
                name = "fit"
            }
        }
        br()
        h3 {
            +"Fit result: $result"
        }
        h3 {
            +"Chi2/dof = ${result.resultValue / (x.size - 3)}"
        }
    }

    page.makeFile()
}
