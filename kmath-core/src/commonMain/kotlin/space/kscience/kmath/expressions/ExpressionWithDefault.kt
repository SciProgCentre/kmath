/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.attributes.SafeType

public class ExpressionWithDefault<T>(
    private val origin: Expression<T>,
    private val defaultArgs: Map<Symbol, T>,
) : Expression<T> {
    override val type: SafeType<T>
        get() = origin.type

    override fun invoke(arguments: Map<Symbol, T>): T = origin.invoke(defaultArgs + arguments)
}

public fun <T> Expression<T>.withDefaultArgs(defaultArgs: Map<Symbol, T>): ExpressionWithDefault<T> =
    ExpressionWithDefault(this, defaultArgs)


public class DiffExpressionWithDefault<T>(
    private val origin: DifferentiableExpression<T>,
    private val defaultArgs: Map<Symbol, T>,
) : DifferentiableExpression<T> {

    override val type: SafeType<T>
        get() = origin.type

    override fun invoke(arguments: Map<Symbol, T>): T = origin.invoke(defaultArgs + arguments)

    override fun derivativeOrNull(symbols: List<Symbol>): Expression<T>? =
        origin.derivativeOrNull(symbols)?.withDefaultArgs(defaultArgs)
}

public fun <T> DifferentiableExpression<T>.withDefaultArgs(defaultArgs: Map<Symbol, T>): DiffExpressionWithDefault<T> =
    DiffExpressionWithDefault(this, defaultArgs)