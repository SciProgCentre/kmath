/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.NamedMatrix
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.*
import kotlin.reflect.KClass

public interface OptimizationFeature : Feature<OptimizationFeature> {
    // enforce toString override
    override fun toString(): String
}

public interface OptimizationProblem<T> : Featured<OptimizationFeature> {
    public val features: FeatureSet<OptimizationFeature>
    override fun <F : OptimizationFeature> getFeature(type: KClass<out F>): F? = features.getFeature(type)
}

public inline fun <reified F : OptimizationFeature> OptimizationProblem<*>.getFeature(): F? = getFeature(F::class)

public open class OptimizationStartPoint<T>(public val point: Map<Symbol, T>) : OptimizationFeature {
    override fun toString(): String = "StartPoint($point)"
}


public interface OptimizationPrior<T> : OptimizationFeature, DifferentiableExpression<T> {
    override val key: FeatureKey<OptimizationFeature> get() = OptimizationPrior::class
}

/**
 * Covariance matrix for
 */
public class OptimizationCovariance<T>(public val covariance: NamedMatrix<T>) : OptimizationFeature {
    override fun toString(): String = "Covariance($covariance)"
}

/**
 * Get the starting point for optimization. Throws error if not defined.
 */
public val <T> OptimizationProblem<T>.startPoint: Map<Symbol, T>
    get() = getFeature<OptimizationStartPoint<T>>()?.point
        ?: error("Starting point not defined in $this")

public open class OptimizationResult<T>(public val point: Map<Symbol, T>) : OptimizationFeature {
    override fun toString(): String = "Result($point)"
}

public val <T> OptimizationProblem<T>.resultPointOrNull: Map<Symbol, T>?
    get() = getFeature<OptimizationResult<T>>()?.point

public val <T> OptimizationProblem<T>.resultPoint: Map<Symbol, T>
    get() = resultPointOrNull ?: error("Result is not present in $this")

public class OptimizationLog(private val loggable: Loggable) : Loggable by loggable, OptimizationFeature {
    override fun toString(): String = "Log($loggable)"
}

/**
 * Free parameters of the optimization
 */
public class OptimizationParameters(public val symbols: List<Symbol>) : OptimizationFeature {
    public constructor(vararg symbols: Symbol) : this(listOf(*symbols))

    override fun toString(): String = "Parameters($symbols)"
}

/**
 * Maximum allowed number of iterations
 */
public class OptimizationIterations(public val maxIterations: Int) : OptimizationFeature {
    override fun toString(): String = "Iterations($maxIterations)"
}


