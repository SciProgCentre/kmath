package scientifik.kmath.expressions

import scientifik.kmath.operations.*

internal class FunctionalUnaryOperation<T>(val context: Algebra<T>, val name: String, private val expr: Expression<T>) :
    Expression<T> {
    override operator fun invoke(arguments: Map<String, T>): T = context.unaryOperation(name, expr.invoke(arguments))
}

internal class FunctionalBinaryOperation<T>(
    val context: Algebra<T>,
    val name: String,
    val first: Expression<T>,
    val second: Expression<T>
) : Expression<T> {
    override operator fun invoke(arguments: Map<String, T>): T =
        context.binaryOperation(name, first.invoke(arguments), second.invoke(arguments))
}

internal class FunctionalVariableExpression<T>(val name: String, val default: T? = null) : Expression<T> {
    override operator fun invoke(arguments: Map<String, T>): T =
        arguments[name] ?: default ?: error("Parameter not found: $name")
}

internal class FunctionalConstantExpression<T>(val value: T) : Expression<T> {
    override operator fun invoke(arguments: Map<String, T>): T = value
}

internal class FunctionalConstProductExpression<T>(
    val context: Space<T>,
    private val expr: Expression<T>,
    val const: Number
) : Expression<T> {
    override operator fun invoke(arguments: Map<String, T>): T = context.multiply(expr.invoke(arguments), const)
}

/**
 * A context class for [Expression] construction.
 *
 * @param algebra The algebra to provide for Expressions built.
 */
abstract class FunctionalExpressionAlgebra<T, A : Algebra<T>>(val algebra: A) : ExpressionAlgebra<T, Expression<T>> {
    /**
     * Builds an Expression of constant expression which does not depend on arguments.
     */
    override fun const(value: T): Expression<T> = FunctionalConstantExpression(value)

    /**
     * Builds an Expression to access a variable.
     */
    override fun variable(name: String, default: T?): Expression<T> = FunctionalVariableExpression(name, default)

    /**
     * Builds an Expression of dynamic call of binary operation [operation] on [left] and [right].
     */
    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        FunctionalBinaryOperation(algebra, operation, left, right)

    /**
     * Builds an Expression of dynamic call of unary operation with name [operation] on [arg].
     */
    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        FunctionalUnaryOperation(algebra, operation, arg)
}

/**
 * A context class for [Expression] construction for [Space] algebras.
 */
open class FunctionalExpressionSpace<T, A : Space<T>>(algebra: A) :
    FunctionalExpressionAlgebra<T, A>(algebra), Space<Expression<T>> {
    override val zero: Expression<T> get() = const(algebra.zero)

    /**
     * Builds an Expression of addition of two another expressions.
     */
    override fun add(a: Expression<T>, b: Expression<T>): Expression<T> =
        binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)

    /**
     * Builds an Expression of multiplication of expression by number.
     */
    override fun multiply(a: Expression<T>, k: Number): Expression<T> =
        FunctionalConstProductExpression(algebra, a, k)

    operator fun Expression<T>.plus(arg: T): Expression<T> = this + const(arg)
    operator fun Expression<T>.minus(arg: T): Expression<T> = this - const(arg)
    operator fun T.plus(arg: Expression<T>): Expression<T> = arg + this
    operator fun T.minus(arg: Expression<T>): Expression<T> = arg - this

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        super<FunctionalExpressionAlgebra>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        super<FunctionalExpressionAlgebra>.binaryOperation(operation, left, right)
}

open class FunctionalExpressionRing<T, A>(algebra: A) : FunctionalExpressionSpace<T, A>(algebra),
    Ring<Expression<T>> where  A : Ring<T>, A : NumericAlgebra<T> {
    override val one: Expression<T>
        get() = const(algebra.one)

    /**
     * Builds an Expression of multiplication of two expressions.
     */
    override fun multiply(a: Expression<T>, b: Expression<T>): Expression<T> =
        binaryOperation(RingOperations.TIMES_OPERATION, a, b)

    operator fun Expression<T>.times(arg: T): Expression<T> = this * const(arg)
    operator fun T.times(arg: Expression<T>): Expression<T> = arg * this

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        super<FunctionalExpressionSpace>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        super<FunctionalExpressionSpace>.binaryOperation(operation, left, right)
}

open class FunctionalExpressionField<T, A>(algebra: A) :
    FunctionalExpressionRing<T, A>(algebra),
    Field<Expression<T>> where A : Field<T>, A : NumericAlgebra<T> {
    /**
     * Builds an Expression of division an expression by another one.
     */
    override fun divide(a: Expression<T>, b: Expression<T>): Expression<T> =
        binaryOperation(FieldOperations.DIV_OPERATION, a, b)

    operator fun Expression<T>.div(arg: T): Expression<T> = this / const(arg)
    operator fun T.div(arg: Expression<T>): Expression<T> = arg / this

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        super<FunctionalExpressionRing>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        super<FunctionalExpressionRing>.binaryOperation(operation, left, right)
}

open class FunctionalExpressionExtendedField<T, A>(algebra: A) :
    FunctionalExpressionField<T, A>(algebra),
    ExtendedField<Expression<T>> where A : ExtendedField<T>, A : NumericAlgebra<T> {
    override fun sin(arg: Expression<T>): Expression<T> = unaryOperation(TrigonometricOperations.SIN_OPERATION, arg)
    override fun cos(arg: Expression<T>): Expression<T> = unaryOperation(TrigonometricOperations.COS_OPERATION, arg)
    override fun asin(arg: Expression<T>): Expression<T> = unaryOperation(TrigonometricOperations.ASIN_OPERATION, arg)
    override fun acos(arg: Expression<T>): Expression<T> = unaryOperation(TrigonometricOperations.ACOS_OPERATION, arg)
    override fun atan(arg: Expression<T>): Expression<T> = unaryOperation(TrigonometricOperations.ATAN_OPERATION, arg)

    override fun power(arg: Expression<T>, pow: Number): Expression<T> =
        binaryOperation(PowerOperations.POW_OPERATION, arg, number(pow))

    override fun exp(arg: Expression<T>): Expression<T> = unaryOperation(ExponentialOperations.EXP_OPERATION, arg)
    override fun ln(arg: Expression<T>): Expression<T> = unaryOperation(ExponentialOperations.LN_OPERATION, arg)

    override fun unaryOperation(operation: String, arg: Expression<T>): Expression<T> =
        super<FunctionalExpressionField>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> =
        super<FunctionalExpressionField>.binaryOperation(operation, left, right)
}

inline fun <T, A : Space<T>> A.expressionInSpace(block: FunctionalExpressionSpace<T, A>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionSpace(this).block()

inline fun <T, A : Ring<T>> A.expressionInRing(block: FunctionalExpressionRing<T, A>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionRing(this).block()

inline fun <T, A : Field<T>> A.expressionInField(block: FunctionalExpressionField<T, A>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionField(this).block()

inline fun <T, A : ExtendedField<T>> A.expressionInExtendedField(block: FunctionalExpressionExtendedField<T, A>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionExtendedField(this).block()
