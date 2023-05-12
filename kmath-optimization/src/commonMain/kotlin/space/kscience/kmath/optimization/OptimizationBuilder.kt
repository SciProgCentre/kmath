/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.FeatureSet

public abstract class OptimizationBuilder<T, R : OptimizationProblem<T>> {
    public val features: MutableList<OptimizationFeature> = ArrayList()

    public fun addFeature(feature: OptimizationFeature) {
        features.add(feature)
    }

    public inline fun <reified T : OptimizationFeature> updateFeature(update: (T?) -> T) {
        val existing = features.find { it.key == T::class } as? T
        val new = update(existing)
        if (existing != null) {
            features.remove(existing)
        }
        addFeature(new)
    }

    public abstract fun build(): R
}

public fun <T> OptimizationBuilder<T, *>.startAt(startingPoint: Map<Symbol, T>) {
    addFeature(OptimizationStartPoint(startingPoint))
}

public class FunctionOptimizationBuilder<T>(
    private val expression: DifferentiableExpression<T>,
) : OptimizationBuilder<T, FunctionOptimization<T>>() {
    override fun build(): FunctionOptimization<T> = FunctionOptimization(FeatureSet.of(features), expression)
}

public fun <T> FunctionOptimization(
    expression: DifferentiableExpression<T>,
    builder: FunctionOptimizationBuilder<T>.() -> Unit,
): FunctionOptimization<T> = FunctionOptimizationBuilder(expression).apply(builder).build()

public suspend fun <T> DifferentiableExpression<T>.optimizeWith(
    optimizer: Optimizer<T, FunctionOptimization<T>>,
    startingPoint: Map<Symbol, T>,
    builder: FunctionOptimizationBuilder<T>.() -> Unit = {},
): FunctionOptimization<T> {
    val problem = FunctionOptimization<T>(this) {
        startAt(startingPoint)
        builder()
    }
    return optimizer.optimize(problem)
}

public suspend fun <T> DifferentiableExpression<T>.optimizeWith(
    optimizer: Optimizer<T, FunctionOptimization<T>>,
    vararg startingPoint: Pair<Symbol, T>,
    builder: FunctionOptimizationBuilder<T>.() -> Unit = {},
): FunctionOptimization<T> {
    val problem = FunctionOptimization<T>(this) {
        startAt(mapOf(*startingPoint))
        builder()
    }
    return optimizer.optimize(problem)
}


@OptIn(UnstableKMathAPI::class)
public class XYOptimizationBuilder(
    public val data: XYColumnarData<Double, Double, Double>,
    public val model: DifferentiableExpression<Double>,
) : OptimizationBuilder<Double, XYFit>() {

    public var pointToCurveDistance: PointToCurveDistance = PointToCurveDistance.byY
    public var pointWeight: PointWeight = PointWeight.byYSigma

    override fun build(): XYFit = XYFit(
        data,
        model,
        FeatureSet.of(features),
        pointToCurveDistance,
        pointWeight
    )
}

@OptIn(UnstableKMathAPI::class)
public fun XYOptimization(
    data: XYColumnarData<Double, Double, Double>,
    model: DifferentiableExpression<Double>,
    builder: XYOptimizationBuilder.() -> Unit,
): XYFit = XYOptimizationBuilder(data, model).apply(builder).build()