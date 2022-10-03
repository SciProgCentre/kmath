/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.FeatureSet

public class OptimizationValue<T>(public val value: T) : OptimizationFeature {
    override fun toString(): String = "Value($value)"
}

public enum class FunctionOptimizationTarget : OptimizationFeature {
    MAXIMIZE,
    MINIMIZE
}

public class FunctionOptimization<T>(
    override val features: FeatureSet<OptimizationFeature>,
    public val expression: DifferentiableExpression<T>,
) : OptimizationProblem<T> {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FunctionOptimization<*>

        if (features != other.features) return false
        if (expression != other.expression) return false

        return true
    }

    override fun hashCode(): Int {
        var result = features.hashCode()
        result = 31 * result + expression.hashCode()
        return result
    }

    override fun toString(): String = "FunctionOptimization(features=$features)"
}

public fun <T> FunctionOptimization<T>.withFeatures(
    vararg newFeature: OptimizationFeature,
): FunctionOptimization<T> = FunctionOptimization(
    features.with(*newFeature),
    expression,
)

/**
 * Optimizes differentiable expression using specific [optimizer] form given [startingPoint].
 */
public suspend fun <T : Any> DifferentiableExpression<T>.optimizeWith(
    optimizer: Optimizer<T, FunctionOptimization<T>>,
    startingPoint: Map<Symbol, T>,
    vararg features: OptimizationFeature,
): FunctionOptimization<T> {
    val problem = FunctionOptimization<T>(FeatureSet.of(OptimizationStartPoint(startingPoint), *features), this)
    return optimizer.optimize(problem)
}

public val <T> FunctionOptimization<T>.resultValueOrNull: T?
    get() = getFeature<OptimizationResult<T>>()?.point?.let { expression(it) }

public val <T> FunctionOptimization<T>.resultValue: T
    get() = resultValueOrNull ?: error("Result is not present in $this")