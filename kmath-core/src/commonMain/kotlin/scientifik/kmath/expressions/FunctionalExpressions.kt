package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

internal class VariableExpression<T>(val name: String, val default: T? = null) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        arguments[name] ?: default ?: error("Parameter not found: $name")
}

internal class ConstantExpression<T>(val value: T) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = value
}

internal class SumExpression<T>(
    val context: Space<T>,
    val first: Expression<T>,
    val second: Expression<T>
) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.add(first.invoke(arguments), second.invoke(arguments))
}

internal class ProductExpression<T>(val context: Ring<T>, val first: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        context.multiply(first.invoke(arguments), second.invoke(arguments))
}

internal class ConstProductExpession<T>(val context: Space<T>, val expr: Expression<T>, val const: Number) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.multiply(expr.invoke(arguments), const)
}

internal class DivExpession<T>(val context: Field<T>, val expr: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.divide(expr.invoke(arguments), second.invoke(arguments))
}

open class FunctionalExpressionSpace<T>(
    val space: Space<T>,
    one: T
) : Space<Expression<T>>, ExpressionSpace<T,Expression<T>> {

    override val zero: Expression<T> = ConstantExpression(space.zero)

    override fun const(value: T): Expression<T> = ConstantExpression(value)

    override fun variable(name: String, default: T?): Expression<T> = VariableExpression(name, default)

    override fun add(a: Expression<T>, b: Expression<T>): Expression<T> = SumExpression(space, a, b)

    override fun multiply(a: Expression<T>, k: Number): Expression<T> = ConstProductExpession(space, a, k)


    operator fun Expression<T>.plus(arg: T) = this + const(arg)
    operator fun Expression<T>.minus(arg: T) = this - const(arg)

    operator fun T.plus(arg: Expression<T>) = arg + this
    operator fun T.minus(arg: Expression<T>) = arg - this
}

open class FunctionalExpressionField<T>(
    val field: Field<T>
) : ExpressionField<T,Expression<T>>, FunctionalExpressionSpace<T>(field, field.one) {

    override val one: Expression<T>
        get() = const(this.field.one)

    override fun const(value: Double): Expression<T> = const(field.run { one*value})

    override fun multiply(a: Expression<T>, b: Expression<T>): Expression<T> = ProductExpression(field, a, b)

    override fun divide(a: Expression<T>, b: Expression<T>): Expression<T> = DivExpession(field, a, b)

    operator fun Expression<T>.times(arg: T) = this * const(arg)
    operator fun Expression<T>.div(arg: T) = this / const(arg)

    operator fun T.times(arg: Expression<T>) = arg * this
    operator fun T.div(arg: Expression<T>) = arg / this
}