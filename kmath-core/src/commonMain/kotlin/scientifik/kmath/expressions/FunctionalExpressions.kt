package scientifik.kmath.expressions

import scientifik.kmath.operations.*

internal class FunctionalUnaryOperation<T>(val context: Algebra<T>, val name: String, val expr: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.unaryOperation(name, expr.invoke(arguments))
}

internal class FunctionalBinaryOperation<T>(
    val context: Algebra<T>,
    val name: String,
    val first: Expression<T>,
    val second: Expression<T>
) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        context.binaryOperation(name, first.invoke(arguments), second.invoke(arguments))
}

internal class FunctionalVariableExpression<T>(val name: String, val default: T? = null) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        arguments[name] ?: default ?: error("Parameter not found: $name")
}

internal class FunctionalConstantExpression<T>(val value: T) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = value
}

internal class FunctionalConstProductExpression<T>(val context: Space<T>, val expr: Expression<T>, val const: Number) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.multiply(expr.invoke(arguments), const)
}

open class FunctionalExpressionAlgebra<T>(val algebra: Algebra<T>) :
    Algebra<Expression<T>>,
    ExpressionAlgebra<T, Expression<T>> {
    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun const(value: T): Expression<T> = FunctionalConstantExpression(value)
    override fun variable(name: String, default: T?): Expression<T> = FunctionalVariableExpression(name, default)
}

open class FunctionalExpressionSpace<T>(val space: Space<T>) :
    FunctionalExpressionAlgebra<T>(space),
    Space<Expression<T>> {
    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override val zero: Expression<T> = FunctionalConstantExpression(space.zero)

    override fun add(a: Expression<T>, b: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(space, SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: Expression<T>, k: Number): Expression<T> = FunctionalConstProductExpression(space, a, k)
    operator fun Expression<T>.plus(arg: T): Expression<T> = this + const(arg)
    operator fun Expression<T>.minus(arg: T): Expression<T> = this - const(arg)
    operator fun T.plus(arg: Expression<T>): Expression<T> = arg + this
    operator fun T.minus(arg: Expression<T>): Expression<T> = arg - this
}

open class FunctionalExpressionRing<T>(val ring: Ring<T>) : FunctionalExpressionSpace<T>(ring), Ring<Expression<T>> {
    override val one: Expression<T>
        get() = const(this.ring.one)

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun number(value: Number): Expression<T> = const(ring { one * value })

    override fun multiply(a: Expression<T>, b: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(space, RingOperations.TIMES_OPERATION, a, b)

    operator fun Expression<T>.times(arg: T): Expression<T> = this * const(arg)
    operator fun T.times(arg: Expression<T>): Expression<T> = arg * this
}

open class FunctionalExpressionField<T>(val field: Field<T>) :
    FunctionalExpressionRing<T>(field),
    Field<Expression<T>> {

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun divide(a: Expression<T>, b: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(space, FieldOperations.DIV_OPERATION, a, b)

    operator fun Expression<T>.div(arg: T): Expression<T> = this / const(arg)
    operator fun T.div(arg: Expression<T>): Expression<T> = arg / this
}
