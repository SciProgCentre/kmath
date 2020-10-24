package kscience.kmath.commons.optimization

import kscience.kmath.expressions.*
import org.apache.commons.math3.optim.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer

public typealias ParameterSpacePoint = Map<Symbol, Double>

public class OptimizationResult(public val point: ParameterSpacePoint, public val value: Double)

public operator fun PointValuePair.component1(): DoubleArray = point
public operator fun PointValuePair.component2(): Double = value

public object Optimization {
    public const val DEFAULT_RELATIVE_TOLERANCE: Double = 1e-4
    public const val DEFAULT_ABSOLUTE_TOLERANCE: Double = 1e-4
    public const val DEFAULT_MAX_ITER: Int = 1000
}


private fun SymbolIndexer.objectiveFunction(expression: Expression<Double>) = ObjectiveFunction {
    val args = it.toMap()
    expression(args)
}

private fun SymbolIndexer.objectiveFunctionGradient(
    expression: DifferentiableExpression<Double>,
) = ObjectiveFunctionGradient {
    val args = it.toMap()
    DoubleArray(symbols.size) { index ->
        expression.derivative(symbols[index])(args)
    }
}

private fun SymbolIndexer.initialGuess(point: ParameterSpacePoint) = InitialGuess(point.toArray())

/**
 * Optimize expression without derivatives
 */
public fun Expression<Double>.optimize(
    startingPoint: ParameterSpacePoint,
    goalType: GoalType = GoalType.MAXIMIZE,
    vararg additionalArguments: OptimizationData,
    optimizerBuilder: () -> MultivariateOptimizer = {
        SimplexOptimizer(
            SimpleValueChecker(
                Optimization.DEFAULT_RELATIVE_TOLERANCE,
                Optimization.DEFAULT_ABSOLUTE_TOLERANCE,
                Optimization.DEFAULT_MAX_ITER
            )
        )
    },
): OptimizationResult = withSymbols(startingPoint.keys) {
    val optimizer = optimizerBuilder()
    val objectiveFunction = objectiveFunction(this@optimize)
    val (point, value) = optimizer.optimize(
        objectiveFunction,
        initialGuess(startingPoint),
        goalType,
        MaxEval.unlimited(),
        NelderMeadSimplex(symbols.size, 1.0),
        *additionalArguments
    )
    OptimizationResult(point.toMap(), value)
}

/**
 * Optimize differentiable expression
 */
public fun DifferentiableExpression<Double>.optimize(
    startingPoint: ParameterSpacePoint,
    goalType: GoalType = GoalType.MAXIMIZE,
    vararg additionalArguments: OptimizationData,
    optimizerBuilder: () -> NonLinearConjugateGradientOptimizer = {
        NonLinearConjugateGradientOptimizer(
            NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES,
            SimpleValueChecker(
                Optimization.DEFAULT_RELATIVE_TOLERANCE,
                Optimization.DEFAULT_ABSOLUTE_TOLERANCE,
                Optimization.DEFAULT_MAX_ITER
            )
        )
    },
): OptimizationResult = withSymbols(startingPoint.keys) {
    val optimizer = optimizerBuilder()
    val objectiveFunction = objectiveFunction(this@optimize)
    val objectiveGradient = objectiveFunctionGradient(this@optimize)
    val (point, value) = optimizer.optimize(
        objectiveFunction,
        objectiveGradient,
        initialGuess(startingPoint),
        goalType,
        MaxEval.unlimited(),
        *additionalArguments
    )
    OptimizationResult(point.toMap(), value)
}