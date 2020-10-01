package kscience.kmath.commons.expressions

import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.ExpressionAlgebra
import kscience.kmath.operations.ExtendedField
import kscience.kmath.operations.Field
import kscience.kmath.operations.invoke
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import kotlin.properties.ReadOnlyProperty

/**
 * A field over commons-math [DerivativeStructure].
 *
 * @property order The derivation order.
 * @property parameters The map of free parameters.
 */
public class DerivativeStructureField(
    public val order: Int,
    public val parameters: Map<String, Double>
) : ExtendedField<DerivativeStructure> {
    public override val zero: DerivativeStructure by lazy { DerivativeStructure(parameters.size, order) }
    public override val one: DerivativeStructure by lazy { DerivativeStructure(parameters.size, order, 1.0) }

    private val variables: Map<String, DerivativeStructure> = parameters.mapValues { (key, value) ->
        DerivativeStructure(parameters.size, order, parameters.keys.indexOf(key), value)
    }

    public val variable: ReadOnlyProperty<Any?, DerivativeStructure> = ReadOnlyProperty { _, property ->
        variables[property.name] ?: error("A variable with name ${property.name} does not exist")
    }

    public fun variable(name: String, default: DerivativeStructure? = null): DerivativeStructure =
        variables[name] ?: default ?: error("A variable with name $name does not exist")

    public fun Number.const(): DerivativeStructure = DerivativeStructure(order, parameters.size, toDouble())

    public fun DerivativeStructure.deriv(parName: String, order: Int = 1): Double {
        return deriv(mapOf(parName to order))
    }

    public fun DerivativeStructure.deriv(orders: Map<String, Int>): Double {
        return getPartialDerivative(*parameters.keys.map { orders[it] ?: 0 }.toIntArray())
    }

    public fun DerivativeStructure.deriv(vararg orders: Pair<String, Int>): Double = deriv(mapOf(*orders))
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
public class DiffExpression(public val function: DerivativeStructureField.() -> DerivativeStructure) :
    Expression<Double> {
    public override operator fun invoke(arguments: Map<String, Double>): Double = DerivativeStructureField(
        0,
        arguments
    ).function().value

    /**
     * Get the derivative expression with given orders
     * TODO make result [DiffExpression]
     */
    public fun derivative(orders: Map<String, Int>): Expression<Double> = Expression { arguments ->
        (DerivativeStructureField(orders.values.maxOrNull() ?: 0, arguments)) { function().deriv(orders) }
    }

    //TODO add gradient and maybe other vector operators
}

public fun DiffExpression.derivative(vararg orders: Pair<String, Int>): Expression<Double> = derivative(mapOf(*orders))
public fun DiffExpression.derivative(name: String): Expression<Double> = derivative(name to 1)

/**
 * A context for [DiffExpression] (not to be confused with [DerivativeStructure])
 */
public object DiffExpressionAlgebra : ExpressionAlgebra<Double, DiffExpression>, Field<DiffExpression> {
    public override val zero: DiffExpression = DiffExpression { 0.0.const() }
    public override val one: DiffExpression = DiffExpression { 1.0.const() }

    public override fun variable(name: String, default: Double?): DiffExpression =
        DiffExpression { variable(name, default?.const()) }

    public override fun const(value: Double): DiffExpression = DiffExpression { value.const() }

    public override fun add(a: DiffExpression, b: DiffExpression): DiffExpression =
        DiffExpression { a.function(this) + b.function(this) }

    public override fun multiply(a: DiffExpression, k: Number): DiffExpression = DiffExpression { a.function(this) * k }

    public override fun multiply(a: DiffExpression, b: DiffExpression): DiffExpression =
        DiffExpression { a.function(this) * b.function(this) }

    public override fun divide(a: DiffExpression, b: DiffExpression): DiffExpression =
        DiffExpression { a.function(this) / b.function(this) }
}
