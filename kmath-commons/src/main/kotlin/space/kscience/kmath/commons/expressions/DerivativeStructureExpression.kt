package space.kscience.kmath.commons.expressions

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations

/**
 * A field over commons-math [DerivativeStructure].
 *
 * @property order The derivation order.
 * @property bindings The map of bindings values. All bindings are considered free parameters
 */
@OptIn(UnstableKMathAPI::class)
public class DerivativeStructureField(
    public val order: Int,
    bindings: Map<Symbol, Double>,
) : ExtendedField<DerivativeStructure>, ExpressionAlgebra<Double, DerivativeStructure>,
    NumbersAddOperations<DerivativeStructure> {
    public val numberOfVariables: Int = bindings.size

    public override val zero: DerivativeStructure by lazy { DerivativeStructure(numberOfVariables, order) }
    public override val one: DerivativeStructure by lazy { DerivativeStructure(numberOfVariables, order, 1.0) }

    override fun number(value: Number): DerivativeStructure = const(value.toDouble())

    /**
     * A class that implements both [DerivativeStructure] and a [Symbol]
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

    public override fun bindSymbolOrNull(symbol: Symbol): DerivativeStructureSymbol? = variables[symbol.identity]

    public fun bind(symbol: Symbol): DerivativeStructureSymbol = variables.getValue(symbol.identity)

    override fun bindSymbol(value: String): DerivativeStructureSymbol = bind(StringSymbol(value))

    public fun DerivativeStructure.derivative(symbols: List<Symbol>): Double {
        require(symbols.size <= order) { "The order of derivative ${symbols.size} exceeds computed order $order" }
        val ordersCount = symbols.map { it.identity }.groupBy { it }.mapValues { it.value.size }
        return getPartialDerivative(*variables.keys.map { ordersCount[it] ?: 0 }.toIntArray())
    }

    public fun DerivativeStructure.derivative(vararg symbols: Symbol): Double = derivative(symbols.toList())

    override fun DerivativeStructure.unaryMinus(): DerivativeStructure = negate()

    public override fun add(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.add(b)

    public override fun scale(a: DerivativeStructure, value: Double): DerivativeStructure = a.multiply(value)

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

    public companion object :
        AutoDiffProcessor<Double, DerivativeStructure, DerivativeStructureField, Expression<Double>> {
        public override fun process(function: DerivativeStructureField.() -> DerivativeStructure): DifferentiableExpression<Double, Expression<Double>> =
            DerivativeStructureExpression(function)
    }
}


/**
 * A constructs that creates a derivative structure with required order on-demand
 */
public class DerivativeStructureExpression(
    public val function: DerivativeStructureField.() -> DerivativeStructure,
) : DifferentiableExpression<Double, Expression<Double>> {
    public override operator fun invoke(arguments: Map<Symbol, Double>): Double =
        DerivativeStructureField(0, arguments).function().value

    /**
     * Get the derivative expression with given orders
     */
    public override fun derivativeOrNull(symbols: List<Symbol>): Expression<Double> = Expression { arguments ->
        with(DerivativeStructureField(symbols.size, arguments)) { function().derivative(symbols) }
    }
}
