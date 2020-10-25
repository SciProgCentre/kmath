package kscience.kmath.commons.optimization

import kscience.kmath.expressions.DifferentiableExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.Symbol


/**
 * Optimize expression without derivatives
 */
public fun Expression<Double>.optimize(
    vararg symbols: Symbol,
    configuration: CMOptimizationProblem.() -> Unit,
): OptimizationResult<Double> {
    require(symbols.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = CMOptimizationProblem(symbols.toList()).apply(configuration)
    problem.expression(this)
    return problem.optimize()
}

/**
 * Optimize differentiable expression
 */
public fun DifferentiableExpression<Double>.optimize(
    vararg symbols: Symbol,
    configuration: CMOptimizationProblem.() -> Unit,
): OptimizationResult<Double> {
    require(symbols.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = CMOptimizationProblem(symbols.toList()).apply(configuration)
    problem.diffExpression(this)
    return problem.optimize()
}