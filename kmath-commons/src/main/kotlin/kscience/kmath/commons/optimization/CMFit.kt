package kscience.kmath.commons.optimization

import kscience.kmath.commons.expressions.DerivativeStructureExpression
import kscience.kmath.commons.expressions.DerivativeStructureField
import kscience.kmath.expressions.DifferentiableExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.StringSymbol
import kscience.kmath.expressions.Symbol
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.indices
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import kotlin.math.pow


public object CMFit {

    /**
     * Generate a chi squared expression from given x-y-sigma model represented by an expression. Does not provide derivatives
     * TODO move to prob/stat
     */
    public fun chiSquared(
        x: Buffer<Double>,
        y: Buffer<Double>,
        yErr: Buffer<Double>,
        model: Expression<Double>,
        xSymbol: Symbol = StringSymbol("x"),
    ): Expression<Double> {
        require(x.size == y.size) { "X and y buffers should be of the same size" }
        require(y.size == yErr.size) { "Y and yErr buffer should of the same size" }
        return Expression { arguments ->
            x.indices.sumByDouble {
                val xValue = x[it]
                val yValue = y[it]
                val yErrValue = yErr[it]
                val modifiedArgs = arguments + (xSymbol to xValue)
                val modelValue = model(modifiedArgs)
                ((yValue - modelValue) / yErrValue).pow(2)
            }
        }
    }

    /**
     * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
     */
    public fun chiSquared(
        x: Buffer<Double>,
        y: Buffer<Double>,
        yErr: Buffer<Double>,
        model: DerivativeStructureField.(x: DerivativeStructure) -> DerivativeStructure,
    ): DerivativeStructureExpression {
        require(x.size == y.size) { "X and y buffers should be of the same size" }
        require(y.size == yErr.size) { "Y and yErr buffer should of the same size" }
        return DerivativeStructureExpression {
            var sum = zero
            x.indices.forEach {
                val xValue = x[it]
                val yValue = y[it]
                val yErrValue = yErr[it]
                val modelValue = model(const(xValue))
                sum += ((yValue - modelValue) / yErrValue).pow(2)
            }
            sum
        }
    }
}

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
public fun DifferentiableExpression<Double>.optimize(
    vararg symbols: Symbol,
    configuration: CMOptimizationProblem.() -> Unit,
): OptimizationResult<Double> = optimizeWith(CMOptimizationProblem, symbols = symbols, configuration)

public fun DifferentiableExpression<Double>.minimize(
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