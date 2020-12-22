package kscience.kmath.commons.optimization

import kscience.kmath.commons.expressions.DerivativeStructureField
import kscience.kmath.expressions.DifferentiableExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.Symbol
import kscience.kmath.stat.Fitting
import kscience.kmath.stat.OptimizationResult
import kscience.kmath.stat.optimizeWith
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asBuffer
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType

/**
 * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
 */
public fun Fitting.chiSquared(
    x: Buffer<Double>,
    y: Buffer<Double>,
    yErr: Buffer<Double>,
    model: DerivativeStructureField.(x: DerivativeStructure) -> DerivativeStructure,
): DifferentiableExpression<Double, Expression<Double>> = chiSquared(DerivativeStructureField, x, y, yErr, model)

/**
 * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
 */
public fun Fitting.chiSquared(
    x: Iterable<Double>,
    y: Iterable<Double>,
    yErr: Iterable<Double>,
    model: DerivativeStructureField.(x: DerivativeStructure) -> DerivativeStructure,
): DifferentiableExpression<Double, Expression<Double>> = chiSquared(
    DerivativeStructureField,
    x.toList().asBuffer(),
    y.toList().asBuffer(),
    yErr.toList().asBuffer(),
    model
)

/**
 * Optimize expression without derivatives
 */
public fun Expression<Double>.optimize(
    vararg symbols: Symbol,
    configuration: CMOptimizationProblem.() -> Unit,
): OptimizationResult<Double> = optimizeWith(CMOptimizationProblem, symbols = symbols, configuration)

/**
 * Optimize differentiable expression
 */
public fun DifferentiableExpression<Double, Expression<Double>>.optimize(
    vararg symbols: Symbol,
    configuration: CMOptimizationProblem.() -> Unit,
): OptimizationResult<Double> = optimizeWith(CMOptimizationProblem, symbols = symbols, configuration)

public fun DifferentiableExpression<Double, Expression<Double>>.minimize(
    vararg startPoint: Pair<Symbol, Double>,
    configuration: CMOptimizationProblem.() -> Unit = {},
): OptimizationResult<Double> {
    require(startPoint.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = CMOptimizationProblem(startPoint.map { it.first }).apply(configuration)
    problem.diffExpression(this)
    problem.initialGuess(startPoint.toMap())
    problem.goal(GoalType.MINIMIZE)
    return problem.optimize()
}