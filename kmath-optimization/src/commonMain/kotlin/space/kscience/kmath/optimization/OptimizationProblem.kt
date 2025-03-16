/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.attributes.*
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.Loggable
import space.kscience.kmath.named.NamedMatrix

public interface OptimizationAttribute<T> : Attribute<T>

public interface OptimizationProblem<T> : AttributeContainer, WithType<T>

public class OptimizationStartPoint<T> : OptimizationAttribute<Map<Symbol, T>>,
    PolymorphicAttribute<Map<Symbol, T>>(safeTypeOf())

/**
 * Get the starting point for optimization. Throws error if not defined.
 */
public val <T> OptimizationProblem<T>.startPoint: Map<Symbol, T>
    get() = attributes[OptimizationStartPoint()] ?: error("Starting point not defined in $this")

public fun <T> AttributesBuilder<OptimizationProblem<T>>.startAt(startingPoint: Map<Symbol, T>) {
    put(OptimizationStartPoint(), startingPoint)
}


/**
 * Covariance matrix for optimization
 */
public class OptimizationCovariance<T> : OptimizationAttribute<NamedMatrix<T>>,
    PolymorphicAttribute<NamedMatrix<T>>(safeTypeOf())

public fun <T> AttributesBuilder<OptimizationProblem<T>>.covariance(covariance: NamedMatrix<T>) {
    put(OptimizationCovariance(), covariance)
}


public class OptimizationResult<T>() : OptimizationAttribute<Map<Symbol, T>>,
    PolymorphicAttribute<Map<Symbol, T>>(safeTypeOf())

public fun <T> AttributesBuilder<OptimizationProblem<T>>.result(result: Map<Symbol, T>) {
    put(OptimizationResult(), result)
}

public val <T> OptimizationProblem<T>.resultOrNull: Map<Symbol, T>? get() = attributes[OptimizationResult()]

public val <T> OptimizationProblem<T>.result: Map<Symbol, T>
    get() = resultOrNull ?: error("Result is not present in $this")

public object OptimizationLog : OptimizationAttribute<Loggable>

/**
 * Free parameters of the optimization
 */
public object OptimizationParameters : OptimizationAttribute<List<Symbol>>

public fun AttributesBuilder<OptimizationProblem<*>>.freeParameters(vararg symbols: Symbol) {
    OptimizationParameters(symbols.asList())
}

/**
 * Maximum allowed number of iterations
 */
public object OptimizationIterations : OptimizationAttribute<Int>

public fun AttributesBuilder<OptimizationProblem<*>>.iterations(iterations: Int) {
    OptimizationIterations(iterations)
}
