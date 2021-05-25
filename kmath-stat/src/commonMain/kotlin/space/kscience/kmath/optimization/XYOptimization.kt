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
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.invoke

public fun interface PointToCurveDistance<T> {
    public fun distance(problem: XYOptimization<T>, index: Int): DifferentiableExpression<T, Expression<T>>

    public companion object {
        public fun <T> byY(
            algebra: Field<T>,
        ): PointToCurveDistance<T> = PointToCurveDistance { problem, index ->
            algebra {
                val x = problem.data.x[index]
                val y = problem.data.y[index]
                
                val model = problem.model(args + (Symbol.x to x))
                model - y
            }
        }


//        val default = PointToCurveDistance<Double>{args, data, index ->
//
//        }
    }
}


public class XYOptimization<T>(
    override val features: FeatureSet<OptimizationFeature>,
    public val data: XYColumnarData<T, T, T>,
    public val model: DifferentiableExpression<T, Expression<T>>,
) : OptimizationProblem

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