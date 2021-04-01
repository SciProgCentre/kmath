package space.kscience.kmath.commons.optimization

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.commons.expressions.DerivativeStructureExpression
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.optimization.FunctionOptimization
import space.kscience.kmath.stat.RandomGenerator
import kotlin.math.pow
import kotlin.test.Test

internal class OptimizeTest {
    val x by symbol
    val y by symbol

    val normal = DerivativeStructureExpression {
        exp(-bindSymbol(x).pow(2) / 2) + exp(-bindSymbol(y)
            .pow(2) / 2)
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
    fun testCmFit() = runBlocking {
        val a by symbol
        val b by symbol
        val c by symbol

        val sigma = 1.0
        val generator = NormalDistribution(0.0, sigma)
        val chain = generator.sample(RandomGenerator.default(112667))
        val x = (1..100).map(Int::toDouble)

        val y = x.map {
            it.pow(2) + it + 1 + chain.next()
        }

        val yErr = List(x.size) { sigma }

        val chi2 = FunctionOptimization.chiSquared(x, y, yErr) { x1 ->
            val cWithDefault = bindSymbolOrNull(c) ?: one
            bindSymbol(a) * x1.pow(2) + bindSymbol(b) * x1 + cWithDefault
        }

        val result = chi2.minimize(a to 1.5, b to 0.9, c to 1.0)
        println(result)
        println("Chi2/dof = ${result.value / (x.size - 3)}")
    }
}
