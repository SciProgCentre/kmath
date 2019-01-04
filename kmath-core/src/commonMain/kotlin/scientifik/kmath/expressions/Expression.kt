package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space


interface Expression<T> {
    operator fun invoke(arguments: Map<String, T>): T
}

operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T = invoke(mapOf(*pairs))

interface ExpressionContext<T> {
    fun variable(name: String, default: T? = null): Expression<T>

    fun const(value: T): Expression<T>
}

internal class VariableExpression<T>(val name: String, val default: T? = null) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T {
        return arguments[name] ?: default ?: error("The parameter not found: $name")
    }
}

internal class ConstantExpression<T>(val value: T) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = value
}

internal class SumExpression<T>(val context: Space<T>, val first: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.add(first.invoke(arguments), second.invoke(arguments))
}

internal class ProductExpression<T>(val context: Field<T>, val first: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        context.multiply(first.invoke(arguments), second.invoke(arguments))
}

internal class ConstProductExpession<T>(val context: Field<T>, val expr: Expression<T>, val const: Double) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.multiply(expr.invoke(arguments), const)
}

internal class DivExpession<T>(val context: Field<T>, val expr: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.divide(expr.invoke(arguments), second.invoke(arguments))
}

class ExpressionField<T>(val field: Field<T>) : Field<Expression<T>>, ExpressionContext<T> {

    override val zero: Expression<T> = ConstantExpression(field.zero)

    override val one: Expression<T> = ConstantExpression(field.one)

    override fun const(value: T): Expression<T> = ConstantExpression(value)

    override fun variable(name: String, default: T?): Expression<T> = VariableExpression(name, default)

    override fun add(a: Expression<T>, b: Expression<T>): Expression<T> = SumExpression(field, a, b)

    override fun multiply(a: Expression<T>, k: Double): Expression<T> = ConstProductExpession(field, a, k)

    override fun multiply(a: Expression<T>, b: Expression<T>): Expression<T> = ProductExpression(field, a, b)

    override fun divide(a: Expression<T>, b: Expression<T>): Expression<T> = DivExpession(field, a, b)


    operator fun Expression<T>.plus(arg: T) = this + const(arg)
    operator fun Expression<T>.minus(arg: T) = this - const(arg)
    operator fun Expression<T>.times(arg: T) = this * const(arg)
    operator fun Expression<T>.div(arg: T) = this / const(arg)

    operator fun T.plus(arg: Expression<T>) = arg + this
    operator fun T.minus(arg: Expression<T>) = arg - this
    operator fun T.times(arg: Expression<T>) = arg * this
    operator fun T.div(arg: Expression<T>) = arg / this
}