/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

public class OptimizationValue<T>(public val value: T) : OptimizationFeature{
    override fun toString(): String = "Value($value)"
}

public enum class FunctionOptimizationTarget : OptimizationFeature {
    MAXIMIZE,
    MINIMIZE
}

public class FunctionOptimization<T>(
    override val features: FeatureSet<OptimizationFeature>,
    public val expression: DifferentiableExpression<T>,
) : OptimizationProblem{

    public companion object{
        /**
         * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
         */
        public fun <T : Any, I : Any, A> chiSquaredExpression(
            autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
            x: Buffer<T>,
            y: Buffer<T>,
            yErr: Buffer<T>,
            model: A.(I) -> I,
        ): DifferentiableExpression<T> where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> {
            require(x.size == y.size) { "X and y buffers should be of the same size" }
            require(y.size == yErr.size) { "Y and yErr buffer should of the same size" }

            return autoDiff.process {
                var sum = zero

                x.indices.forEach {
                    val xValue = const(x[it])
                    val yValue = const(y[it])
                    val yErrValue = const(yErr[it])
                    val modelValue = model(xValue)
                    sum += ((yValue - modelValue) / yErrValue).pow(2)
                }

                sum
            }
        }
    }
}


public fun <T> FunctionOptimization<T>.withFeatures(
    vararg newFeature: OptimizationFeature,
): FunctionOptimization<T> = FunctionOptimization(
    features.with(*newFeature),
    expression,
)

/**
 * Optimize differentiable expression using specific [optimizer] form given [startingPoint]
 */
public suspend fun <T : Any> DifferentiableExpression<T, Expression<T>>.optimizeWith(
    optimizer: Optimizer<FunctionOptimization<T>>,
    startingPoint: Map<Symbol, T>,
    vararg features: OptimizationFeature,
): FunctionOptimization<T> {
    val problem = FunctionOptimization<T>(FeatureSet.of(OptimizationStartPoint(startingPoint), *features), this)
    return optimizer.process(problem)
}

