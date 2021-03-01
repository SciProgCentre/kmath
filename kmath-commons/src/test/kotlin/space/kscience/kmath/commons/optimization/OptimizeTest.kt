package space.kscience.kmath.commons.optimization

import org.junit.jupiter.api.Test
import space.kscience.kmath.commons.expressions.DerivativeStructureExpression
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.stat.Distribution
import space.kscience.kmath.stat.Fitting
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.normal
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
    fun testCmFit() {
        val a by symbol
        val b by symbol
        val c by symbol

        val sigma = 1.0
        val generator = Distribution.normal(0.0, sigma)
        val chain = generator.sample(RandomGenerator.default(112667))
        val x = (1..100).map(Int::toDouble)

        val y = x.map {
            it.pow(2) + it + 1 + chain.nextDouble()
        }

        val yErr = List(x.size) { sigma }

        val chi2 = Fitting.chiSquared(x, y, yErr) { x1 ->
            val cWithDefault = bindSymbolOrNull(c) ?: one
            bind(a) * x1.pow(2) + bind(b) * x1 + cWithDefault
        }

        val result = chi2.minimize(a to 1.5, b to 0.9, c to 1.0)
        println(result)
        println("Chi2/dof = ${result.value / (x.size - 3)}")
    }

}