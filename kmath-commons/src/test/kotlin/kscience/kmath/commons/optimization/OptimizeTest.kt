package kscience.kmath.commons.optimization

import kscience.kmath.commons.expressions.DerivativeStructureExpression
import kscience.kmath.expressions.symbol
import org.junit.jupiter.api.Test

internal class OptimizeTest {
    val x by symbol
    val y by symbol

    val normal = DerivativeStructureExpression {
        exp(-bind(x).pow(2) / 2) + exp(- bind(y).pow(2) / 2)
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
}