/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(UnstableKMathAPI::class)
package space.kscience.kmath.commons.optimization

import org.apache.commons.math3.optim.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.SymbolIndexer
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.expressions.withSymbols
import space.kscience.kmath.misc.log
import space.kscience.kmath.optimization.*
import kotlin.collections.set
import kotlin.reflect.KClass

public operator fun PointValuePair.component1(): DoubleArray = point
public operator fun PointValuePair.component2(): Double = value

public class CMOptimizerEngine(public val optimizerBuilder: () -> MultivariateOptimizer) : OptimizationFeature {
    override fun toString(): String = "CMOptimizer($optimizerBuilder)"
}

/**
 * Specify a Commons-maths optimization engine
 */
public fun FunctionOptimizationBuilder<Double>.cmEngine(optimizerBuilder: () -> MultivariateOptimizer) {
    addFeature(CMOptimizerEngine(optimizerBuilder))
}

public class CMOptimizerData(public val data: List<SymbolIndexer.() -> OptimizationData>) : OptimizationFeature {
    public constructor(vararg data: (SymbolIndexer.() -> OptimizationData)) : this(data.toList())

    override fun toString(): String = "CMOptimizerData($data)"
}

/**
 * Specify Commons-maths optimization data.
 */
public fun FunctionOptimizationBuilder<Double>.cmOptimizationData(data: SymbolIndexer.() -> OptimizationData) {
    updateFeature<CMOptimizerData> {
        val newData = (it?.data ?: emptyList()) + data
        CMOptimizerData(newData)
    }
}

public fun FunctionOptimizationBuilder<Double>.simplexSteps(vararg steps: Pair<Symbol, Double>) {
    //TODO use convergence checker from features
    cmEngine { SimplexOptimizer(CMOptimizer.defaultConvergenceChecker) }
    cmOptimizationData { NelderMeadSimplex(mapOf(*steps).toDoubleArray()) }
}

@OptIn(UnstableKMathAPI::class)
public object CMOptimizer : Optimizer<Double, FunctionOptimization<Double>> {

    public const val DEFAULT_RELATIVE_TOLERANCE: Double = 1e-4
    public const val DEFAULT_ABSOLUTE_TOLERANCE: Double = 1e-4
    public const val DEFAULT_MAX_ITER: Int = 1000

    public val defaultConvergenceChecker: SimpleValueChecker = SimpleValueChecker(
        DEFAULT_RELATIVE_TOLERANCE,
        DEFAULT_ABSOLUTE_TOLERANCE,
        DEFAULT_MAX_ITER
    )


    override suspend fun optimize(
        problem: FunctionOptimization<Double>,
    ): FunctionOptimization<Double> {
        val startPoint = problem.startPoint

        val parameters = problem.getFeature<OptimizationParameters>()?.symbols
            ?: problem.getFeature<OptimizationStartPoint<Double>>()?.point?.keys
            ?: startPoint.keys


        withSymbols(parameters) {
            val convergenceChecker: ConvergenceChecker<PointValuePair> = SimpleValueChecker(
                DEFAULT_RELATIVE_TOLERANCE,
                DEFAULT_ABSOLUTE_TOLERANCE,
                DEFAULT_MAX_ITER
            )

            val cmOptimizer: MultivariateOptimizer = problem.getFeature<CMOptimizerEngine>()?.optimizerBuilder?.invoke()
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

            //fun exportOptimizationData(): List<OptimizationData> = optimizationData.values.toList()

            val objectiveFunction = ObjectiveFunction {
                val args = startPoint + it.toMap()
                val res = problem.expression(args)
                res
            }
            addOptimizationData(objectiveFunction)

            val gradientFunction = ObjectiveFunctionGradient {
                val args = startPoint + it.toMap()
                val res = DoubleArray(symbols.size) { index ->
                    problem.expression.derivative(symbols[index])(args)
                }
                res
            }
            addOptimizationData(gradientFunction)

            val logger = problem.getFeature<OptimizationLog>()

            for (feature in problem.features) {
                when (feature) {
                    is CMOptimizerData -> feature.data.forEach { dataBuilder ->
                        addOptimizationData(dataBuilder())
                    }
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
}
