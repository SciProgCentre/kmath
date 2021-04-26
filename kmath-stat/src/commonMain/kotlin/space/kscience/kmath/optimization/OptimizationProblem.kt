/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.Featured
import space.kscience.kmath.misc.Loggable
import space.kscience.kmath.misc.Symbol
import kotlin.reflect.KClass

public interface OptimizationFeature

public interface OptimizationProblem : Featured<OptimizationFeature> {
    public val features: FeatureSet<OptimizationFeature>
    override fun <T : OptimizationFeature> getFeature(type: KClass<out T>): T? = features.getFeature(type)
}

public inline fun <reified T : OptimizationFeature> OptimizationProblem.getFeature(): T? = getFeature(T::class)

public open class OptimizationResult<T>(public val point: Map<Symbol, T>) : OptimizationFeature

public class OptimizationLog(private val loggable: Loggable) : Loggable by loggable, OptimizationFeature

//public class OptimizationResult<T>(
//    public val point: Map<Symbol, T>,
//    public val value: T,
//    public val features: Set<OptimizationFeature> = emptySet(),
//) {
//    override fun toString(): String {
//        return "OptimizationResult(point=$point, value=$value)"
//    }
//}
//
//public operator fun <T> OptimizationResult<T>.plus(
//    feature: OptimizationFeature,
//): OptimizationResult<T> = OptimizationResult(point, value, features + feature)
//public fun interface OptimizationProblemFactory<T : Any, out P : OptimizationProblem<T>> {
//    public fun build(symbols: List<Symbol>): P
//}
//
//public operator fun <T : Any, P : OptimizationProblem<T>> OptimizationProblemFactory<T, P>.invoke(
//    symbols: List<Symbol>,
//    block: P.() -> Unit,
//): P = build(symbols).apply(block)

public interface Optimizer<P : OptimizationProblem> {
    public suspend fun process(problem: P): P
}

