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
) : OptimizationProblem<T>{

    public companion object
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

public val <T> FunctionOptimization<T>.resultValueOrNull:T?
    get() = getFeature<OptimizationResult<T>>()?.point?.let { expression(it) }

public val <T> FunctionOptimization<T>.resultValue: T
    get() = resultValueOrNull ?: error("Result is not present in $this")