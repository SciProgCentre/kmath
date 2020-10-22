package kscience.kmath.commons.expressions

import kscience.kmath.expressions.DifferentiableExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.ExpressionAlgebra
import kscience.kmath.expressions.Symbol
import kscience.kmath.operations.ExtendedField
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure

/**
 * A field over commons-math [DerivativeStructure].
 *
 * @property order The derivation order.
 * @property bindings The map of bindings values. All bindings are considered free parameters
 */
public class DerivativeStructureField(
    public val order: Int,
    private val bindings: Map<Symbol, Double>
) : ExtendedField<DerivativeStructure>, ExpressionAlgebra<Double, DerivativeStructure> {
    public override val zero: DerivativeStructure by lazy { DerivativeStructure(bindings.size, order) }
    public override val one: DerivativeStructure by lazy { DerivativeStructure(bindings.size, order, 1.0) }

    /**
     * A class that implements both [DerivativeStructure] and a [Symbol]
     */
    public inner class DerivativeStructureSymbol(symbol: Symbol, value: Double) :
        DerivativeStructure(bindings.size, order, bindings.keys.indexOf(symbol), value), Symbol {
        override val identity: String = symbol.identity
        override fun toString(): String = identity
        override fun equals(other: Any?): Boolean = this.identity == (other as? Symbol)?.identity
        override fun hashCode(): Int = identity.hashCode()
    }

    /**
     * Identity-based symbol bindings map
     */
    private val variables: Map<String, DerivativeStructureSymbol> = bindings.entries.associate { (key, value) ->
        key.identity to DerivativeStructureSymbol(key, value)
    }

    override fun const(value: Double): DerivativeStructure = DerivativeStructure(order, bindings.size, value)

    public override fun bindOrNull(symbol: Symbol): DerivativeStructureSymbol? = variables[symbol.identity]

    public fun bind(symbol: Symbol): DerivativeStructureSymbol = variables.getValue(symbol.identity)

    public fun Number.const(): DerivativeStructure = const(toDouble())

    public fun DerivativeStructure.derivative(parameter: Symbol, order: Int = 1): Double {
        return derivative(mapOf(parameter to order))
    }

    public fun DerivativeStructure.derivative(orders: Map<Symbol, Int>): Double {
        return getPartialDerivative(*bindings.keys.map { orders[it] ?: 0 }.toIntArray())
    }

    public fun DerivativeStructure.derivative(vararg orders: Pair<Symbol, Int>): Double = derivative(mapOf(*orders))
    public override fun add(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.add(b)

    public override fun multiply(a: DerivativeStructure, k: Number): DerivativeStructure = when (k) {
        is Double -> a.multiply(k)
        is Int -> a.multiply(k)
        else -> a.multiply(k.toDouble())
    }

    public override fun multiply(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.multiply(b)
    public override fun divide(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.divide(b)
    public override fun sin(arg: DerivativeStructure): DerivativeStructure = arg.sin()
    public override fun cos(arg: DerivativeStructure): DerivativeStructure = arg.cos()
    public override fun tan(arg: DerivativeStructure): DerivativeStructure = arg.tan()
    public override fun asin(arg: DerivativeStructure): DerivativeStructure = arg.asin()
    public override fun acos(arg: DerivativeStructure): DerivativeStructure = arg.acos()
    public override fun atan(arg: DerivativeStructure): DerivativeStructure = arg.atan()
    public override fun sinh(arg: DerivativeStructure): DerivativeStructure = arg.sinh()
    public override fun cosh(arg: DerivativeStructure): DerivativeStructure = arg.cosh()
    public override fun tanh(arg: DerivativeStructure): DerivativeStructure = arg.tanh()
    public override fun asinh(arg: DerivativeStructure): DerivativeStructure = arg.asinh()
    public override fun acosh(arg: DerivativeStructure): DerivativeStructure = arg.acosh()
    public override fun atanh(arg: DerivativeStructure): DerivativeStructure = arg.atanh()

    public override fun power(arg: DerivativeStructure, pow: Number): DerivativeStructure = when (pow) {
        is Double -> arg.pow(pow)
        is Int -> arg.pow(pow)
        else -> arg.pow(pow.toDouble())
    }

    public fun power(arg: DerivativeStructure, pow: DerivativeStructure): DerivativeStructure = arg.pow(pow)
    public override fun exp(arg: DerivativeStructure): DerivativeStructure = arg.exp()
    public override fun ln(arg: DerivativeStructure): DerivativeStructure = arg.log()

    public override operator fun DerivativeStructure.plus(b: Number): DerivativeStructure = add(b.toDouble())
    public override operator fun DerivativeStructure.minus(b: Number): DerivativeStructure = subtract(b.toDouble())
    public override operator fun Number.plus(b: DerivativeStructure): DerivativeStructure = b + this
    public override operator fun Number.minus(b: DerivativeStructure): DerivativeStructure = b - this
}

/**
 * A constructs that creates a derivative structure with required order on-demand
 */
public class DerivativeStructureExpression(
    public val function: DerivativeStructureField.() -> DerivativeStructure,
) : DifferentiableExpression<Double> {
    public override operator fun invoke(arguments: Map<Symbol, Double>): Double =
        DerivativeStructureField(0, arguments).function().value

    /**
     * Get the derivative expression with given orders
     */
    public override fun derivative(orders: Map<Symbol, Int>): Expression<Double> = Expression { arguments ->
        with(DerivativeStructureField(orders.values.maxOrNull() ?: 0, arguments)) { function().derivative(orders) }
    }
}
