/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.attributes.AttributesBuilder
import space.kscience.attributes.SafeType
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.data.indices
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt


private val oneOver2Pi = 1.0 / sqrt(2 * PI)

@UnstableKMathAPI
internal fun XYFit.logLikelihood(): DifferentiableExpression<Float64> = object : DifferentiableExpression<Float64> {
    override val type: SafeType<Float64> get() = Float64Field.type

    override fun derivativeOrNull(symbols: List<Symbol>): Expression<Float64> = Expression(type) { arguments ->
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
public suspend fun Optimizer<Double, FunctionOptimization<Float64>>.maximumLogLikelihood(problem: XYFit): XYFit {
    val functionOptimization = FunctionOptimization(problem.logLikelihood(), problem.attributes)
    val result = optimize(
        functionOptimization.withAttributes {
            FunctionOptimizationTarget(OptimizationDirection.MAXIMIZE)
        }
    )
    return XYFit(problem.data, problem.model, result.attributes)
}

@UnstableKMathAPI
public suspend fun Optimizer<Double, FunctionOptimization<Float64>>.maximumLogLikelihood(
    data: XYColumnarData<Double, Double, Double>,
    model: DifferentiableExpression<Float64>,
    builder: AttributesBuilder<XYFit>.() -> Unit,
): XYFit = maximumLogLikelihood(XYOptimization(data, model, builder))
