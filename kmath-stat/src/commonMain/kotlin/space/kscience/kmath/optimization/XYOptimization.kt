/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.optimization

import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.UnstableKMathAPI
import kotlin.math.pow

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

public class XYOptimization(
    override val features: FeatureSet<OptimizationFeature>,
    public val data: XYColumnarData<Double, Double, Double>,
    public val model: DifferentiableExpression<Double>,
) : OptimizationProblem


public suspend fun Optimizer<FunctionOptimization<Double>>.maximumLogLikelihood(problem: XYOptimization): XYOptimization {
    val distanceBuilder = problem.getFeature() ?: PointToCurveDistance.byY
    val likelihood: DifferentiableExpression<Double> = object : DifferentiableExpression<Double> {
        override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double>? {
            TODO("Not yet implemented")
        }

        override fun invoke(arguments: Map<Symbol, Double>): Double {
            var res = 0.0
            for (index in 0 until problem.data.size) {
                val d = distanceBuilder.distance(problem, index).invoke(arguments)
                val sigma: Double = TODO()
                res -= (d / sigma).pow(2)
            }
            return res
        }

    }
    val functionOptimization = FunctionOptimization(problem.features, likelihood)
    val result = optimize(functionOptimization)
    return XYOptimization(result.features, problem.data, problem.model)
}

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