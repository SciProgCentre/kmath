package space.kscience.kmath.commons.optimization

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import space.kscience.kmath.commons.expressions.DerivativeStructureField
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.optimization.FunctionOptimization
import space.kscience.kmath.optimization.OptimizationResult
import space.kscience.kmath.optimization.noDerivOptimizeWith
import space.kscience.kmath.optimization.optimizeWith
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer

/**
 * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
 */
public fun FunctionOptimization.Companion.chiSquared(
    x: Buffer<Double>,
    y: Buffer<Double>,
    yErr: Buffer<Double>,
    model: DerivativeStructureField.(x: DerivativeStructure) -> DerivativeStructure,
): DifferentiableExpression<Double, Expression<Double>> = chiSquared(DerivativeStructureField, x, y, yErr, model)

/**
 * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
 */
public fun FunctionOptimization.Companion.chiSquared(
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
    configuration: CMOptimization.() -> Unit,
): OptimizationResult<Double> = noDerivOptimizeWith(CMOptimization, symbols = symbols, configuration)

/**
 * Optimize differentiable expression
 */
public fun DifferentiableExpression<Double, Expression<Double>>.optimize(
    vararg symbols: Symbol,
    configuration: CMOptimization.() -> Unit,
): OptimizationResult<Double> = optimizeWith(CMOptimization, symbols = symbols, configuration)

public fun DifferentiableExpression<Double, Expression<Double>>.minimize(
    vararg startPoint: Pair<Symbol, Double>,
    configuration: CMOptimization.() -> Unit = {},
): OptimizationResult<Double> {
    val symbols = startPoint.map { it.first }.toTypedArray()
    return optimize(*symbols){
        maximize = false
        initialGuess(startPoint.toMap())
        diffFunction(this@minimize)
        configuration()
    }
}