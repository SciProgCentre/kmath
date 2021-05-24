/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.data.ColumnarData
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.Field

@UnstableKMathAPI
public interface XYFit<T> : OptimizationProblem {

    public val algebra: Field<T>

    /**
     * Set X-Y data for this fit optionally including x and y errors
     */
    public fun data(
        dataSet: ColumnarData<T>,
        xSymbol: Symbol,
        ySymbol: Symbol,
        xErrSymbol: Symbol? = null,
        yErrSymbol: Symbol? = null,
    )

    public fun model(model: (T) -> DifferentiableExpression<T, *>)

    /**
     * Set the differentiable model for this fit
     */
    public fun <I : Any, A> model(
        autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
        modelFunction: A.(I) -> I,
    ): Unit where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> = model { arg ->
        autoDiff.process { modelFunction(const(arg)) }
    }
}

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

/**
 * Optimize differentiable expression using specific [Optimizer]
 */
public suspend fun <T : Any, F : FunctionOptimization<T>> DifferentiableExpression<T, Expression<T>>.optimizeWith(
    optimizer: Optimizer<F>,
    startingPoint: Map<Symbol,T>,
    vararg features: OptimizationFeature
): OptimizationProblem {
//    require(startingPoint.isNotEmpty()) { "Must provide a list of symbols for optimization" }
//    val problem = factory(symbols.toList(), configuration)
//    problem.function(this)
//    return problem.optimize()
    val problem = FunctionOptimization<T>()
}