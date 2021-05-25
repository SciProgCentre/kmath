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
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.expressions.withSymbols
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.optimization.*
import kotlin.collections.set
import kotlin.reflect.KClass

public operator fun PointValuePair.component1(): DoubleArray = point
public operator fun PointValuePair.component2(): Double = value

public class CMOptimizer(public val optimizerBuilder: () -> MultivariateOptimizer): OptimizationFeature{
    override fun toString(): String = "CMOptimizer($optimizerBuilder)"
}

public class CMOptimizerData(public val data: List<OptimizationData>) : OptimizationFeature {
    public constructor(vararg data: OptimizationData) : this(data.toList())

    override fun toString(): String = "CMOptimizerData($data)"

}

@OptIn(UnstableKMathAPI::class)
public class CMOptimization : Optimizer<FunctionOptimization<Double>> {

    override suspend fun optimize(
        problem: FunctionOptimization<Double>,
    ): FunctionOptimization<Double> {
        val startPoint = problem.getFeature<OptimizationStartPoint<Double>>()?.point
            ?: error("Starting point not defined in $problem")

        val parameters = problem.getFeature<OptimizationParameters>()?.symbols
            ?: problem.getFeature<OptimizationStartPoint<Double>>()?.point?.keys
            ?:startPoint.keys


        withSymbols(parameters) {
            val convergenceChecker: ConvergenceChecker<PointValuePair> = SimpleValueChecker(
                DEFAULT_RELATIVE_TOLERANCE,
                DEFAULT_ABSOLUTE_TOLERANCE,
                DEFAULT_MAX_ITER
            )

            val cmOptimizer: MultivariateOptimizer = problem.getFeature<CMOptimizer>()?.optimizerBuilder?.invoke()
                ?: NonLinearConjugateGradientOptimizer(
                    NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES,
                    convergenceChecker
                )

            val optimizationData: HashMap<KClass<out OptimizationData>, OptimizationData> = HashMap()

            fun addOptimizationData(data: OptimizationData) {
                optimizationData[data::class] = data
            }

            addOptimizationData(MaxEval.unlimited())
            addOptimizationData(InitialGuess(startPoint.toDoubleArray()))

            fun exportOptimizationData(): List<OptimizationData> = optimizationData.values.toList()

            val objectiveFunction = ObjectiveFunction {
                val args = startPoint + it.toMap()
                problem.expression(args)
            }
            addOptimizationData(objectiveFunction)

            val gradientFunction = ObjectiveFunctionGradient {
                val args = startPoint + it.toMap()
                DoubleArray(symbols.size) { index ->
                    problem.expression.derivative(symbols[index])(args)
                }
            }
            addOptimizationData(gradientFunction)

            val logger = problem.getFeature<OptimizationLog>()

            for (feature in problem.features) {
                when (feature) {
                    is CMOptimizerData -> feature.data.forEach { addOptimizationData(it) }
                    is FunctionOptimizationTarget -> when (feature) {
                        FunctionOptimizationTarget.MAXIMIZE -> addOptimizationData(GoalType.MAXIMIZE)
                        FunctionOptimizationTarget.MINIMIZE -> addOptimizationData(GoalType.MINIMIZE)
                    }
                    else -> logger?.log { "The feature $feature is unused in optimization" }
                }
            }

            val (point, value) = cmOptimizer.optimize(*optimizationData.values.toTypedArray())
            return problem.withFeatures(OptimizationResult(point.toMap()), OptimizationValue(value))
        }
    }

    public companion object {
        public const val DEFAULT_RELATIVE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_ABSOLUTE_TOLERANCE: Double = 1e-4
        public const val DEFAULT_MAX_ITER: Int = 1000
    }
}
