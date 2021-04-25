/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.optimization

import org.apache.commons.math3.optim.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.optimization.*
import kotlin.reflect.KClass

public operator fun PointValuePair.component1(): DoubleArray = point
public operator fun PointValuePair.component2(): Double = value

public class CMOptimizerFactory(public val optimizerBuilder: () -> MultivariateOptimizer) : OptimizationFeature
//public class CMOptimizerData(public val )

@OptIn(UnstableKMathAPI::class)
public class CMOptimization : Optimizer<FunctionOptimization<Double>> {

    override suspend fun process(
        problem: FunctionOptimization<Double>
    ): FunctionOptimization<Double> = withSymbols(problem.parameters){
        val cmOptimizer: MultivariateOptimizer =
            problem.getFeature<CMOptimizerFactory>()?.optimizerBuilder?.invoke() ?: SimplexOptimizer()

        val convergenceChecker: ConvergenceChecker<PointValuePair> = SimpleValueChecker(
            DEFAULT_RELATIVE_TOLERANCE,
            DEFAULT_ABSOLUTE_TOLERANCE,
            DEFAULT_MAX_ITER
        )

        val optimizationData: HashMap<KClass<out OptimizationData>, OptimizationData> = HashMap()

        fun addOptimizationData(data: OptimizationData) {
            optimizationData[data::class] = data
        }
        addOptimizationData(MaxEval.unlimited())
        addOptimizationData(InitialGuess(problem.initialGuess.toDoubleArray()))

        fun exportOptimizationData(): List<OptimizationData> = optimizationData.values.toList()


        /**
         * Register no-deriv function instead of  differentiable function
         */
        /**
         * Register no-deriv function instead of  differentiable function
         */
        fun noDerivFunction(expression: Expression<Double>): Unit {
            val objectiveFunction = ObjectiveFunction {
                val args = problem.initialGuess + it.toMap()
                expression(args)
            }
            addOptimizationData(objectiveFunction)
        }

        public override fun function(expression: DifferentiableExpression<Double, Expression<Double>>) {
            noDerivFunction(expression)
            val gradientFunction = ObjectiveFunctionGradient {
                val args = startingPoint + it.toMap()
                DoubleArray(symbols.size) { index ->
                    expression.derivative(symbols[index])(args)
                }
            }
            addOptimizationData(gradientFunction)
            if (optimizerBuilder == null) {
                optimizerBuilder = {
                    NonLinearConjugateGradientOptimizer(
                        NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES,
                        convergenceChecker
                    )
                }
            }
        }

        public fun simplex(simplex: AbstractSimplex) {
            addOptimizationData(simplex)
            //Set optimization builder to simplex if it is not present
            if (optimizerBuilder == null) {
                optimizerBuilder = { SimplexOptimizer(convergenceChecker) }
            }
        }

        public fun simplexSteps(steps: Map<Symbol, Double>) {
            simplex(NelderMeadSimplex(steps.toDoubleArray()))
        }

        public fun goal(goalType: GoalType) {
            addOptimizationData(goalType)
        }

        public fun optimizer(block: () -> MultivariateOptimizer) {
            optimizerBuilder = block
        }

        override fun update(result: OptimizationResult<Double>) {
            initialGuess(result.point)
        }

        override suspend fun optimize(): OptimizationResult<Double> {
            val optimizer = optimizerBuilder?.invoke() ?: error("Optimizer not defined")
            val (point, value) = optimizer.optimize(*optimizationData.values.toTypedArray())
            return OptimizationResult(point.toMap(), value)
        }
        return@withSymbols TODO()
    }

    public companion object : OptimizationProblemFactory<Double, CMOptimization> {
        public const val DEFAULT_RELATIVE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_ABSOLUTE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_MAX_ITER: Int = 1000

        override fun build(symbols: List<Symbol>): CMOptimization = CMOptimization(symbols)
    }
}

public fun CMOptimization.initialGuess(vararg pairs: Pair<Symbol, Double>): Unit = initialGuess(pairs.toMap())
public fun CMOptimization.simplexSteps(vararg pairs: Pair<Symbol, Double>): Unit = simplexSteps(pairs.toMap())
