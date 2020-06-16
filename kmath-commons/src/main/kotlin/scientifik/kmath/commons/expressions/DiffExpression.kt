package scientifik.kmath.commons.expressions

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure
import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.ExpressionAlgebra
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.Field
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A field wrapping commons-math derivative structures
 */
class DerivativeStructureField(
    val order: Int,
    val parameters: Map<String, Double>
) : ExtendedField<DerivativeStructure> {

    override val zero: DerivativeStructure by lazy { DerivativeStructure(order, parameters.size) }

    override val one: DerivativeStructure by lazy { DerivativeStructure(order, parameters.size, 1.0) }

    private val variables: Map<String, DerivativeStructure> = parameters.mapValues { (key, value) ->
        DerivativeStructure(parameters.size, order, parameters.keys.indexOf(key), value)
    }

    val variable = object : ReadOnlyProperty<Any?, DerivativeStructure> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): DerivativeStructure {
            return variables[property.name] ?: error("A variable with name ${property.name} does not exist")
        }
    }

    fun variable(name: String, default: DerivativeStructure? = null): DerivativeStructure =
        variables[name] ?: default ?: error("A variable with name $name does not exist")


    fun Number.const() = DerivativeStructure(order, parameters.size, toDouble())

    fun DerivativeStructure.deriv(parName: String, order: Int = 1): Double {
        return deriv(mapOf(parName to order))
    }

    fun DerivativeStructure.deriv(orders: Map<String, Int>): Double {
        return getPartialDerivative(*parameters.keys.map { orders[it] ?: 0 }.toIntArray())
    }

    fun DerivativeStructure.deriv(vararg orders: Pair<String, Int>): Double = deriv(mapOf(*orders))

    override fun add(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.add(b)

    override fun multiply(a: DerivativeStructure, k: Number): DerivativeStructure = when (k) {
        is Double -> a.multiply(k)
        is Int -> a.multiply(k)
        else -> a.multiply(k.toDouble())
    }

    override fun multiply(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.multiply(b)

    override fun divide(a: DerivativeStructure, b: DerivativeStructure): DerivativeStructure = a.divide(b)

    override fun sin(arg: DerivativeStructure): DerivativeStructure = arg.sin()
    override fun cos(arg: DerivativeStructure): DerivativeStructure = arg.cos()
    override fun asin(arg: DerivativeStructure): DerivativeStructure = arg.asin()
    override fun acos(arg: DerivativeStructure): DerivativeStructure = arg.acos()
    override fun atan(arg: DerivativeStructure): DerivativeStructure = arg.atan()

    override fun power(arg: DerivativeStructure, pow: Number): DerivativeStructure = when (pow) {
        is Double -> arg.pow(pow)
        is Int -> arg.pow(pow)
        else -> arg.pow(pow.toDouble())
    }

    fun power(arg: DerivativeStructure, pow: DerivativeStructure): DerivativeStructure = arg.pow(pow)

    override fun exp(arg: DerivativeStructure): DerivativeStructure = arg.exp()

    override fun ln(arg: DerivativeStructure): DerivativeStructure = arg.log()

    override operator fun DerivativeStructure.plus(b: Number): DerivativeStructure = add(b.toDouble())
    override operator fun DerivativeStructure.minus(b: Number): DerivativeStructure = subtract(b.toDouble())
    override operator fun Number.plus(b: DerivativeStructure) = b + this
    override operator fun Number.minus(b: DerivativeStructure) = b - this
}

/**
 * A constructs that creates a derivative structure with required order on-demand
 */
class DiffExpression(val function: DerivativeStructureField.() -> DerivativeStructure) : Expression<Double> {

    override fun invoke(arguments: Map<String, Double>): Double = DerivativeStructureField(
        0,
        arguments
    ).run(function).value

    /**
     * Get the derivative expression with given orders
     * TODO make result [DiffExpression]
     */
    fun derivative(orders: Map<String, Int>): Expression<Double> {
        return object : Expression<Double> {
            override fun invoke(arguments: Map<String, Double>): Double =
                DerivativeStructureField(orders.values.max() ?: 0, arguments)
                    .run {
                        function().deriv(orders)
                    }
        }
    }

    //TODO add gradient and maybe other vector operators
}

fun DiffExpression.derivative(vararg orders: Pair<String, Int>) = derivative(mapOf(*orders))
fun DiffExpression.derivative(name: String) = derivative(name to 1)

/**
 * A context for [DiffExpression] (not to be confused with [DerivativeStructure])
 */
object DiffExpressionAlgebra : ExpressionAlgebra<Double, DiffExpression>, Field<DiffExpression> {
    override fun variable(name: String, default: Double?) =
        DiffExpression { variable(name, default?.const()) }

    override fun const(value: Double): DiffExpression =
        DiffExpression { value.const() }

    override fun add(a: DiffExpression, b: DiffExpression) =
        DiffExpression { a.function(this) + b.function(this) }

    override val zero = DiffExpression { 0.0.const() }

    override fun multiply(a: DiffExpression, k: Number) =
        DiffExpression { a.function(this) * k }

    override val one = DiffExpression { 1.0.const() }

    override fun multiply(a: DiffExpression, b: DiffExpression) =
        DiffExpression { a.function(this) * b.function(this) }

    override fun divide(a: DiffExpression, b: DiffExpression) =
        DiffExpression { a.function(this) / b.function(this) }
}
