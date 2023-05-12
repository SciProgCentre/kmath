/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.data.indices
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.derivative
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt


private val oneOver2Pi = 1.0 / sqrt(2 * PI)

@UnstableKMathAPI
internal fun XYFit.logLikelihood(): DifferentiableExpression<Double> = object : DifferentiableExpression<Double> {
    override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double> = Expression { arguments ->
        data.indices.sumOf { index ->
            val d = distance(index)(arguments)
            val weight = weight(index)(arguments)
            val weightDerivative = weight(index).derivative(symbols)(arguments)

            //  -1 / (sqrt(2 PI) * sigma) + 2 (x-mu)/ 2 sigma^2 * d mu/ d theta - (x-mu)^2 / 2 * d w/ d theta
            return@sumOf -oneOver2Pi * sqrt(weight) + //offset derivative
                    d * model.derivative(symbols)(arguments) * weight - //model derivative
                    d.pow(2) * weightDerivative / 2 //weight derivative
        }
    }

    override fun invoke(arguments: Map<Symbol, Double>): Double {
        return data.indices.sumOf { index ->
            val d = distance(index)(arguments)
            val weight = weight(index)(arguments)
            //1/sqrt(2 PI sigma^2) - (x-mu)^2/ (2 * sigma^2)
            oneOver2Pi * ln(weight) - d.pow(2) * weight
        } / 2
    }

}

/**
 * Optimize given XY (least squares) [problem] using this function [Optimizer].
 * The problem is treated as maximum likelihood problem and is done via maximizing logarithmic likelihood, respecting
 * possible weight dependency on the model and parameters.
 */
@UnstableKMathAPI
public suspend fun Optimizer<Double, FunctionOptimization<Double>>.maximumLogLikelihood(problem: XYFit): XYFit {
    val functionOptimization = FunctionOptimization(problem.features, problem.logLikelihood())
    val result = optimize(functionOptimization.withFeatures(FunctionOptimizationTarget.MAXIMIZE))
    return XYFit(problem.data, problem.model, result.features)
}

@UnstableKMathAPI
public suspend fun Optimizer<Double, FunctionOptimization<Double>>.maximumLogLikelihood(
    data: XYColumnarData<Double, Double, Double>,
    model: DifferentiableExpression<Double>,
    builder: XYOptimizationBuilder.() -> Unit,
): XYFit = maximumLogLikelihood(XYOptimization(data, model, builder))
