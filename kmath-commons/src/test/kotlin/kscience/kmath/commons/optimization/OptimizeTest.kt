package kscience.kmath.commons.optimization

import kscience.kmath.commons.expressions.DerivativeStructureExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.Symbol
import kscience.kmath.expressions.symbol
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import org.junit.jupiter.api.Test

internal class OptimizeTest {
    val x by symbol
    val y by symbol

    val normal = DerivativeStructureExpression {
        val x = bind(x)
        val y = bind(y)
        exp(-x.pow(2)/2) + exp(-y.pow(2)/2)
    }

    val startingPoint: Map<Symbol, Double> = mapOf(x to 1.0, y to 1.0)

    @Test
    fun testOptimization() {
        val result = normal.optimize(startingPoint)
        println(result.point)
        println(result.value)
    }

    @Test
    fun testSimplexOptimization() {
        val result = (normal as Expression<Double>).optimize(startingPoint){
            SimplexOptimizer(1e-4,1e-4)
        }
        println(result.point)
        println(result.value)
    }
}