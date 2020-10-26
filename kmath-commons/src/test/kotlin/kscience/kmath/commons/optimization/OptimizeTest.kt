package kscience.kmath.commons.optimization

import kscience.kmath.commons.expressions.DerivativeStructureExpression
import kscience.kmath.expressions.symbol
import kscience.kmath.prob.Distribution
import kscience.kmath.prob.RandomGenerator
import kscience.kmath.prob.normal
import kscience.kmath.structures.asBuffer
import org.junit.jupiter.api.Test
import kotlin.math.pow

internal class OptimizeTest {
    val x by symbol
    val y by symbol

    val normal = DerivativeStructureExpression {
        exp(-bind(x).pow(2) / 2) + exp(-bind(y).pow(2) / 2)
    }

    @Test
    fun testGradientOptimization() {
        val result = normal.optimize(x, y) {
            initialGuess(x to 1.0, y to 1.0)
            //no need to select optimizer. Gradient optimizer is used by default because gradients are provided by function
        }
        println(result.point)
        println(result.value)
    }

    @Test
    fun testSimplexOptimization() {
        val result = normal.optimize(x, y) {
            initialGuess(x to 1.0, y to 1.0)
            simplexSteps(x to 2.0, y to 0.5)
            //this sets simplex optimizer
        }
        println(result.point)
        println(result.value)
    }

    @Test
    fun testFit() {
        val a by symbol
        val b by symbol
        val c by symbol

        val sigma = 1.0
        val generator = Distribution.normal(0.0, sigma)
        val chain = generator.sample(RandomGenerator.default(1126))
        val x = (1..100).map { it.toDouble() }
        val y = x.map { it ->
            it.pow(2) + it + 1 + chain.nextDouble()
        }
        val yErr = x.map { sigma }
        with(CMFit) {
            val chi2 = chiSquared(x.asBuffer(), y.asBuffer(), yErr.asBuffer()) { x ->
                bind(a) * x.pow(2) + bind(b) * x + bind(c)
            }

            val result = chi2.minimize(a to 1.5, b to 0.9, c to 1.0)
            println(result)
            println("Chi2/dof = ${result.value / (x.size - 3)}")
        }
    }
}