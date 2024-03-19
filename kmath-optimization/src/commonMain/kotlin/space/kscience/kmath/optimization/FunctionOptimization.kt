/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.attributes.*
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Symbol

public class OptimizationValue<V>(type: SafeType<V>) : PolymorphicAttribute<V>(type)

public inline fun <reified T> AttributesBuilder<FunctionOptimization<T>>.value(value: T) {
    set(OptimizationValue(safeTypeOf<T>()), value)
}

public enum class OptimizationDirection {
    MAXIMIZE,
    MINIMIZE
}

public object FunctionOptimizationTarget: OptimizationAttribute<OptimizationDirection>

public class FunctionOptimization<T>(
    public val expression: DifferentiableExpression<T>,
    override val attributes: Attributes,
) : OptimizationProblem<T> {

    override val type: SafeType<T> get() = expression.type

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FunctionOptimization<*>

        if (attributes != other.attributes) return false
        if (expression != other.expression) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attributes.hashCode()
        result = 31 * result + expression.hashCode()
        return result
    }

    override fun toString(): String = "FunctionOptimization(features=$attributes)"

    public companion object
}

public fun <T> FunctionOptimization(
    expression: DifferentiableExpression<T>,
    attributeBuilder: AttributesBuilder<FunctionOptimization<T>>.() -> Unit,
): FunctionOptimization<T> = FunctionOptimization(expression, Attributes(attributeBuilder))

public class OptimizationPrior<T> :
    PolymorphicAttribute<DifferentiableExpression<T>>(safeTypeOf()),
    Attribute<DifferentiableExpression<T>>

public fun <T> FunctionOptimization<T>.withAttributes(
    modifier: AttributesBuilder<FunctionOptimization<T>>.() -> Unit,
): FunctionOptimization<T> = FunctionOptimization(
    expression,
    attributes.modify(modifier),
)

/**
 * Optimizes differentiable expression using specific [optimizer] form given [startingPoint].
 */
public suspend fun <T> DifferentiableExpression<T>.optimizeWith(
    optimizer: Optimizer<T, FunctionOptimization<T>>,
    startingPoint: Map<Symbol, T>,
    modifier: AttributesBuilder<FunctionOptimization<T>>.() -> Unit = {},
): FunctionOptimization<T> {
    val problem = FunctionOptimization(this){
        startAt(startingPoint)
        modifier()
    }
    return optimizer.optimize(problem)
}

public val <T> FunctionOptimization<T>.resultValueOrNull: T?
    get() = attributes[OptimizationResult<T>()]?.let { expression(it) }

public val <T> FunctionOptimization<T>.resultValue: T
    get() = resultValueOrNull ?: error("Result is not present in $this")


public suspend fun <T> DifferentiableExpression<T>.optimizeWith(
    optimizer: Optimizer<T, FunctionOptimization<T>>,
    vararg startingPoint: Pair<Symbol, T>,
    builder: AttributesBuilder<FunctionOptimization<T>>.() -> Unit = {},
): FunctionOptimization<T> {
    val problem = FunctionOptimization<T>(this) {
        startAt(mapOf(*startingPoint))
        builder()
    }
    return optimizer.optimize(problem)
}
