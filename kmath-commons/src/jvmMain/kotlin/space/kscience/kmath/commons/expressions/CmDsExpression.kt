/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("DEPRECATION")

package space.kscience.kmath.commons.expressions

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOps

/**
 * A field over commons-math [DerivativeStructure].
 *
 * @property order The derivation order.
 * @param bindings The map of bindings values. All bindings are considered free parameters
 */
@OptIn(UnstableKMathAPI::class)
@Deprecated("Use generic DSAlgebra from the core")
public class CmDsField(
    public val order: Int,
    bindings: Map<Symbol, Double>,
) : ExtendedField<DerivativeStructure>, ExpressionAlgebra<Double, DerivativeStructure>,
    NumbersAddOps<DerivativeStructure> {
    public val numberOfVariables: Int = bindings.size

    override val zero: DerivativeStructure by lazy { DerivativeStructure(numberOfVariables, order) }
    override val one: DerivativeStructure by lazy { DerivativeStructure(numberOfVariables, order, 1.0) }

    override fun number(value: Number): DerivativeStructure = const(value.toDouble())

    /**
     * A class implementing both [DerivativeStructure] and [Symbol].
     */
    public inner class DerivativeStructureSymbol(
        size: Int,
        index: Int,
        symbol: Symbol,
        value: Double,
    ) : DerivativeStructure(size, order, index, value), Symbol {
        override val identity: String = symbol.identity
        override fun toString(): String = identity
        override fun equals(other: Any?): Boolean = this.identity == (other as? Symbol)?.identity
        override fun hashCode(): Int = identity.hashCode()
    }

    /**
     * Identity-based symbol bindings map
     */
    private val variables: Map<String, DerivativeStructureSymbol> = bindings.entries.mapIndexed { index, (key, value) ->
        key.identity to DerivativeStructureSymbol(numberOfVariables, index, key, value)
    }.toMap()

    override fun const(value: Double): DerivativeStructure = DerivativeStructure(numberOfVariables, order, value)

    override fun bindSymbolOrNull(value: String): DerivativeStructureSymbol? = variables[value]
    override fun bindSymbol(value: String): DerivativeStructureSymbol = variables.getValue(value)

    public fun bindSymbolOrNull(symbol: Symbol): DerivativeStructureSymbol? = variables[symbol.identity]
    public fun bindSymbol(symbol: Symbol): DerivativeStructureSymbol = variables.getValue(symbol.identity)

    public fun DerivativeStructure.derivative(symbols: List<Symbol>): Double {
        require(symbols.size <= order) { "The order of derivative ${symbols.size} exceeds computed order $order" }
        val ordersCount = symbols.map { it.identity }.groupBy { it }.mapValues { it.value.size }
        return getPartialDerivative(*variables.keys.map { ordersCount[it] ?: 0 }.toIntArray())
    }

    public fun DerivativeStructure.derivative(vararg symbols: Symbol): Double = derivative(symbols.toList())

    override fun DerivativeStructure.unaryMinus(): DerivativeStructure = negate()

    override fun add(left: DerivativeStructure, right: DerivativeStructure): DerivativeStructure = left.add(right)

    override fun scale(a: DerivativeStructure, value: Double): DerivativeStructure = a.multiply(value)

    override fun multiply(left: DerivativeStructure, right: DerivativeStructure): DerivativeStructure = left.multiply(right)
    override fun divide(left: DerivativeStructure, right: DerivativeStructure): DerivativeStructure = left.divide(right)
    override fun sin(arg: DerivativeStructure): DerivativeStructure = arg.sin()
    override fun cos(arg: DerivativeStructure): DerivativeStructure = arg.cos()
    override fun tan(arg: DerivativeStructure): DerivativeStructure = arg.tan()
    override fun asin(arg: DerivativeStructure): DerivativeStructure = arg.asin()
    override fun acos(arg: DerivativeStructure): DerivativeStructure = arg.acos()
    override fun atan(arg: DerivativeStructure): DerivativeStructure = arg.atan()
    override fun sinh(arg: DerivativeStructure): DerivativeStructure = arg.sinh()
    override fun cosh(arg: DerivativeStructure): DerivativeStructure = arg.cosh()
    override fun tanh(arg: DerivativeStructure): DerivativeStructure = arg.tanh()
    override fun asinh(arg: DerivativeStructure): DerivativeStructure = arg.asinh()
    override fun acosh(arg: DerivativeStructure): DerivativeStructure = arg.acosh()
    override fun atanh(arg: DerivativeStructure): DerivativeStructure = arg.atanh()

    override fun power(arg: DerivativeStructure, pow: Number): DerivativeStructure = when (pow) {
        is Double -> arg.pow(pow)
        is Int -> arg.pow(pow)
        else -> arg.pow(pow.toDouble())
    }

    public fun power(arg: DerivativeStructure, pow: DerivativeStructure): DerivativeStructure = arg.pow(pow)
    override fun exp(arg: DerivativeStructure): DerivativeStructure = arg.exp()
    override fun ln(arg: DerivativeStructure): DerivativeStructure = arg.log()

    override operator fun DerivativeStructure.plus(other: Number): DerivativeStructure = add(other.toDouble())
    override operator fun DerivativeStructure.minus(other: Number): DerivativeStructure = subtract(other.toDouble())
    override operator fun Number.plus(other: DerivativeStructure): DerivativeStructure = other + this
    override operator fun Number.minus(other: DerivativeStructure): DerivativeStructure = other - this
}

/**
 * Auto-diff processor based on Commons-math [DerivativeStructure]
 */
@Deprecated("Use generic DSAlgebra from the core")
public object CmDsProcessor : AutoDiffProcessor<Double, DerivativeStructure, CmDsField> {
     override fun differentiate(
         function: CmDsField.() -> DerivativeStructure,
    ): CmDsExpression = CmDsExpression(function)
}

/**
 * A constructs that creates a derivative structure with required order on-demand
 */
@Deprecated("Use generic DSAlgebra from the core")
public class CmDsExpression(
    public val function: CmDsField.() -> DerivativeStructure,
) : DifferentiableExpression<Double> {
    override operator fun invoke(arguments: Map<Symbol, Double>): Double =
        CmDsField(0, arguments).function().value

    /**
     * Get the derivative expression with given orders
     */
    override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double> = Expression { arguments ->
        with(CmDsField(symbols.size, arguments)) { function().derivative(symbols) }
    }
}
