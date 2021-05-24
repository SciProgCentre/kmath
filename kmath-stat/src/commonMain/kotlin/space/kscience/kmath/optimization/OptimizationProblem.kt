/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.Featured
import space.kscience.kmath.misc.Loggable
import kotlin.reflect.KClass

public interface OptimizationFeature {
    override fun toString(): String
}

public interface OptimizationProblem : Featured<OptimizationFeature> {
    public val features: FeatureSet<OptimizationFeature>
    override fun <T : OptimizationFeature> getFeature(type: KClass<out T>): T? = features.getFeature(type)
}

public inline fun <reified T : OptimizationFeature> OptimizationProblem.getFeature(): T? = getFeature(T::class)

public open class OptimizationStartPoint<T>(public val point: Map<Symbol, T>) : OptimizationFeature {
    override fun toString(): String = "StartPoint($point)"
}

public open class OptimizationResult<T>(public val point: Map<Symbol, T>) : OptimizationFeature {
    override fun toString(): String = "Result($point)"
}

public class OptimizationLog(private val loggable: Loggable) : Loggable by loggable, OptimizationFeature {
    override fun toString(): String = "Log($loggable)"
}

public class OptimizationParameters(public val symbols: List<Symbol>): OptimizationFeature{
    override fun toString(): String = "Parameters($symbols)"
}


public interface Optimizer<P : OptimizationProblem> {
    public suspend fun process(problem: P): P
}

