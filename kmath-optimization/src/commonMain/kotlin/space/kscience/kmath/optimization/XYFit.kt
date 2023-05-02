/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.optimization

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.data.indices
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.Loggable
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.bindSymbol
import kotlin.math.pow

/**
 * Specify the way to compute distance from point to the curve as DifferentiableExpression
 */
public interface PointToCurveDistance : OptimizationFeature {
    public fun distance(problem: XYFit, index: Int): DifferentiableExpression<Double>

    public companion object {
        public val byY: PointToCurveDistance = object : PointToCurveDistance {
            override fun distance(problem: XYFit, index: Int): DifferentiableExpression<Double> {
                val x = problem.data.x[index]
                val y = problem.data.y[index]

                return object : DifferentiableExpression<Double> {
                    override fun derivativeOrNull(
                        symbols: List<Symbol>,
                    ): Expression<Double>? = problem.model.derivativeOrNull(symbols)?.let { derivExpression ->
                        Expression { arguments ->
                            derivExpression.invoke(arguments + (Symbol.x to x))
                        }
                    }

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
 * By default, uses Dispersion^-1
 */
public interface PointWeight : OptimizationFeature {
    public fun weight(problem: XYFit, index: Int): DifferentiableExpression<Double>

    public companion object {
        public fun bySigma(sigmaSymbol: Symbol): PointWeight = object : PointWeight {
            override fun weight(problem: XYFit, index: Int): DifferentiableExpression<Double> =
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
 * A fit problem for X-Y-Yerr data. Also known as "least-squares" problem.
 */
public class XYFit(
    public val data: XYColumnarData<Double, Double, Double>,
    public val model: DifferentiableExpression<Double>,
    override val features: FeatureSet<OptimizationFeature>,
    internal val pointToCurveDistance: PointToCurveDistance = PointToCurveDistance.byY,
    internal val pointWeight: PointWeight = PointWeight.byYSigma,
    public val xSymbol: Symbol = Symbol.x,
) : OptimizationProblem<Double> {
    public fun distance(index: Int): DifferentiableExpression<Double> = pointToCurveDistance.distance(this, index)

    public fun weight(index: Int): DifferentiableExpression<Double> = pointWeight.weight(this, index)
}

public fun XYFit.withFeature(vararg features: OptimizationFeature): XYFit {
    return XYFit(data, model, this.features.with(*features), pointToCurveDistance, pointWeight)
}

public suspend fun XYColumnarData<Double, Double, Double>.fitWith(
    optimizer: Optimizer<Double, XYFit>,
    modelExpression: DifferentiableExpression<Double>,
    startingPoint: Map<Symbol, Double>,
    vararg features: OptimizationFeature = emptyArray(),
    xSymbol: Symbol = Symbol.x,
    pointToCurveDistance: PointToCurveDistance = PointToCurveDistance.byY,
    pointWeight: PointWeight = PointWeight.byYSigma,
): XYFit {
    var actualFeatures = FeatureSet.of(*features, OptimizationStartPoint(startingPoint))

    if (actualFeatures.getFeature<OptimizationLog>() == null) {
        actualFeatures = actualFeatures.with(OptimizationLog(Loggable.console))
    }
    val problem = XYFit(
        this,
        modelExpression,
        actualFeatures,
        pointToCurveDistance,
        pointWeight,
        xSymbol
    )
    return optimizer.optimize(problem)
}

/**
 * Fit given data with a model provided as an expression
 */
public suspend fun <I : Any, A> XYColumnarData<Double, Double, Double>.fitWith(
    optimizer: Optimizer<Double, XYFit>,
    processor: AutoDiffProcessor<Double, I, A>,
    startingPoint: Map<Symbol, Double>,
    vararg features: OptimizationFeature = emptyArray(),
    xSymbol: Symbol = Symbol.x,
    pointToCurveDistance: PointToCurveDistance = PointToCurveDistance.byY,
    pointWeight: PointWeight = PointWeight.byYSigma,
    model: A.(I) -> I,
): XYFit where A : ExtendedField<I>, A : ExpressionAlgebra<Double, I> {
    val modelExpression: DifferentiableExpression<Double> = processor.differentiate {
        val x = bindSymbol(xSymbol)
        model(x)
    }

    return fitWith(
        optimizer = optimizer,
        modelExpression = modelExpression,
        startingPoint = startingPoint,
        features = features,
        xSymbol = xSymbol,
        pointToCurveDistance = pointToCurveDistance,
        pointWeight = pointWeight
    )
}

/**
 * Compute chi squared value for completed fit. Return null for incomplete fit
 */
public val XYFit.chiSquaredOrNull: Double?
    get() {
        val result = startPoint + (resultPointOrNull ?: return null)

        return data.indices.sumOf { index ->

            val x = data.x[index]
            val y = data.y[index]
            val yErr = data[Symbol.yError]?.get(index) ?: 1.0

            val mu = model.invoke(result + (xSymbol to x))

            ((y - mu) / yErr).pow(2)
        }
    }

public val XYFit.dof: Int
    get() = data.size - (getFeature<OptimizationParameters>()?.symbols?.size ?: startPoint.size)