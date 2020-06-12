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

internal class FunctionalConstProductExpression<T>(
    val context: Space<T>,
    val expr: Expression<T>,
    val const: Number
) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.multiply(expr.invoke(arguments), const)
}

interface FunctionalExpressionAlgebra<T, A : Algebra<T>> : ExpressionAlgebra<T, Expression<T>> {
    val algebra: A

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun const(value: T): Expression<T> = FunctionalConstantExpression(value)
    override fun variable(name: String, default: T?): Expression<T> = FunctionalVariableExpression(name, default)
}

open class FunctionalExpressionSpace<T, A>(override val algebra: A) : FunctionalExpressionAlgebra<T, A>,
    Space<Expression<T>> where  A : Space<T> {
    override val zero: Expression<T>
        get() = const(algebra.zero)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun add(a: Expression<T>, b: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: Expression<T>, k: Number): Expression<T> =
        FunctionalConstProductExpression(algebra, a, k)

    operator fun Expression<T>.plus(arg: T): Expression<T> = this + const(arg)
    operator fun Expression<T>.minus(arg: T): Expression<T> = this - const(arg)
    operator fun T.plus(arg: Expression<T>): Expression<T> = arg + this
    operator fun T.minus(arg: Expression<T>): Expression<T> = arg - this
}

open class FunctionalExpressionRing<T, A>(override val algebra: A) : FunctionalExpressionSpace<T, A>(algebra),
    Ring<Expression<T>> where  A : Ring<T>, A : NumericAlgebra<T> {
    override val one: Expression<T>
        get() = const(algebra.one)

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun number(value: Number): Expression<T> = const(algebra { one * value })

    override fun multiply(a: Expression<T>, b: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, RingOperations.TIMES_OPERATION, a, b)

    operator fun Expression<T>.times(arg: T): Expression<T> = this * const(arg)
    operator fun T.times(arg: Expression<T>): Expression<T> = arg * this
}

open class FunctionalExpressionField<T, A>(override val algebra: A) :
    FunctionalExpressionRing<T, A>(algebra),
    Field<Expression<T>> where A : Field<T>, A : NumericAlgebra<T> {

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    override fun divide(a: Expression<T>, b: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, FieldOperations.DIV_OPERATION, a, b)

    operator fun Expression<T>.div(arg: T): Expression<T> = this / const(arg)
    operator fun T.div(arg: Expression<T>): Expression<T> = arg / this
}
