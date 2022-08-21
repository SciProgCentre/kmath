/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.Algebra

/**
 * Represents expression, which structure can be differentiated.
 *
 * @param T the type this expression takes as argument and returns.
 */
public interface DifferentiableExpression<T> : Expression<T> {
    /**
     * Differentiates this expression by ordered collection of [symbols].
     *
     * @param symbols the symbols.
     * @return the derivative or `null`.
     */
    public fun derivativeOrNull(symbols: List<Symbol>): Expression<T>?
}

public fun <T> DifferentiableExpression<T>.derivative(symbols: List<Symbol>): Expression<T> =
    derivativeOrNull(symbols) ?: error("Derivative by symbols $symbols not provided")

public fun <T> DifferentiableExpression<T>.derivative(vararg symbols: Symbol): Expression<T> =
    derivative(symbols.toList())

public fun <T> DifferentiableExpression<T>.derivative(name: String): Expression<T> =
    derivative(StringSymbol(name))

/**
 * A special type of [DifferentiableExpression] which returns typed expressions as derivatives.
 *
 * @param R the type of expression this expression can be differentiated to.
 */
public interface SpecialDifferentiableExpression<T, out R : Expression<T>> : DifferentiableExpression<T> {
    override fun derivativeOrNull(symbols: List<Symbol>): R?
}

public fun <T, R : Expression<T>> SpecialDifferentiableExpression<T, R>.derivative(symbols: List<Symbol>): R =
    derivativeOrNull(symbols) ?: error("Derivative by symbols $symbols not provided")

public fun <T, R : Expression<T>> SpecialDifferentiableExpression<T, R>.derivative(vararg symbols: Symbol): R =
    derivative(symbols.toList())

public fun <T, R : Expression<T>> SpecialDifferentiableExpression<T, R>.derivative(name: String): R =
    derivative(StringSymbol(name))

/**
 * A [DifferentiableExpression] that defines only first derivatives
 */
public abstract class FirstDerivativeExpression<T> : DifferentiableExpression<T> {
    /**
     * Returns first derivative of this expression by given [symbol].
     */
    public abstract fun derivativeOrNull(symbol: Symbol): Expression<T>?

    public final override fun derivativeOrNull(symbols: List<Symbol>): Expression<T>? {
        val dSymbol = symbols.firstOrNull() ?: return null
        return derivativeOrNull(dSymbol)
    }
}

/**
 * A factory that converts an expression in autodiff variables to a [DifferentiableExpression]
 * @param T type of the constants for the expression
 * @param I type of the actual expression state
 * @param A type of expression algebra
 */
public fun interface AutoDiffProcessor<T, I, out A : Algebra<I>> {
    public fun differentiate(function: A.() -> I): DifferentiableExpression<T>
}
