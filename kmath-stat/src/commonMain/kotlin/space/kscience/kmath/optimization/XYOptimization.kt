/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.optimization

import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.data.indices
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.UnstableKMathAPI
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Specify the way to compute distance from point to the curve as DifferentiableExpression
 */
public interface PointToCurveDistance : OptimizationFeature {
    public fun distance(problem: XYOptimization, index: Int): DifferentiableExpression<Double>

    public companion object {
        public val byY: PointToCurveDistance = object : PointToCurveDistance {
            override fun distance(problem: XYOptimization, index: Int): DifferentiableExpression<Double> {

                val x = problem.data.x[index]
                val y = problem.data.y[index]
                return object : DifferentiableExpression<Double> {
                    override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double>? =
                        problem.model.derivativeOrNull(symbols)

                    override fun invoke(arguments: Map<Symbol, Double>): Double =
                        problem.model(arguments + (Symbol.x to x)) - y
                }
            }

            override fun toString(): String = "PointToCurveDistanceByY"
        }
    }
}

/**
 * Compute a wight of the point. The more the weight, the more impact this point will have on the fit.
 * By default uses Dispersion^-1
 */
public interface PointWeight : OptimizationFeature {
    public fun weight(problem: XYOptimization, index: Int): DifferentiableExpression<Double>

    public companion object {
        public fun bySigma(sigmaSymbol: Symbol): PointWeight = object : PointWeight {
            override fun weight(problem: XYOptimization, index: Int): DifferentiableExpression<Double> =
                object : DifferentiableExpression<Double> {
                    override fun invoke(arguments: Map<Symbol, Double>): Double {
                        return problem.data[sigmaSymbol]?.get(index)?.pow(-2) ?: 1.0
                    }

                    override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double> = Expression { 0.0 }
                }

            override fun toString(): String = "PointWeightBySigma($sigmaSymbol)"

        }

        public val byYSigma: PointWeight = bySigma(Symbol.yError)
    }
}

/**
 * An optimization for XY data.
 */
public class XYOptimization(
    override val features: FeatureSet<OptimizationFeature>,
    public val data: XYColumnarData<Double, Double, Double>,
    public val model: DifferentiableExpression<Double>,
    internal val pointToCurveDistance: PointToCurveDistance = PointToCurveDistance.byY,
    internal val pointWeight: PointWeight = PointWeight.byYSigma,
) : OptimizationProblem<Double> {
    public fun distance(index: Int): DifferentiableExpression<Double> = pointToCurveDistance.distance(this, index)

    public fun weight(index: Int): DifferentiableExpression<Double> = pointWeight.weight(this, index)
}

public fun XYOptimization.withFeature(vararg features: OptimizationFeature): XYOptimization {
    return XYOptimization(this.features.with(*features), data, model, pointToCurveDistance, pointWeight)
}

private val oneOver2Pi = 1.0 / sqrt(2 * PI)

internal fun XYOptimization.likelihood(): DifferentiableExpression<Double> = object : DifferentiableExpression<Double> {
    override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double> = Expression { arguments ->
        data.indices.sumOf { index ->

            val d = distance(index)(arguments)
            val weight = weight(index)(arguments)
            val weightDerivative = weight(index)(arguments)

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
public suspend fun Optimizer<Double, FunctionOptimization<Double>>.maximumLogLikelihood(problem: XYOptimization): XYOptimization {
    val functionOptimization = FunctionOptimization(problem.features, problem.likelihood())
    val result = optimize(functionOptimization.withFeatures(FunctionOptimizationTarget.MAXIMIZE))
    return XYOptimization(result.features, problem.data, problem.model)
}

public suspend fun Optimizer<Double, FunctionOptimization<Double>>.maximumLogLikelihood(
    data: XYColumnarData<Double, Double, Double>,
    model: DifferentiableExpression<Double>,
    builder: XYOptimizationBuilder.() -> Unit,
): XYOptimization = maximumLogLikelihood(XYOptimization(data, model, builder))

//public suspend fun XYColumnarData<Double, Double, Double>.fitWith(
//    optimizer: XYOptimization,
//    problemBuilder: XYOptimizationBuilder.() -> Unit = {},
//
//)


//
//@UnstableKMathAPI
//public interface XYFit<T> : OptimizationProblem {
//
//    public val algebra: Field<T>
//
//    /**
//     * Set X-Y data for this fit optionally including x and y errors
//     */
//    public fun data(
//        dataSet: ColumnarData<T>,
//        xSymbol: Symbol,
//        ySymbol: Symbol,
//        xErrSymbol: Symbol? = null,
//        yErrSymbol: Symbol? = null,
//    )
//
//    public fun model(model: (T) -> DifferentiableExpression<T, *>)
//
//    /**
//     * Set the differentiable model for this fit
//     */
//    public fun <I : Any, A> model(
//        autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
//        modelFunction: A.(I) -> I,
//    ): Unit where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> = model { arg ->
//        autoDiff.process { modelFunction(const(arg)) }
//    }
//}

//
///**
// * Define a chi-squared-based objective function
// */
//public fun <T : Any, I : Any, A> FunctionOptimization<T>.chiSquared(
//    autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
//    x: Buffer<T>,
//    y: Buffer<T>,
//    yErr: Buffer<T>,
//    model: A.(I) -> I,
//) where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> {
//    val chiSquared = FunctionOptimization.chiSquared(autoDiff, x, y, yErr, model)
//    function(chiSquared)
//    maximize = false
//}