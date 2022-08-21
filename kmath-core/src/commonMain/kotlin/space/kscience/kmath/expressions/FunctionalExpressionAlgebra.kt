/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A context class for [Expression] construction.
 *
 * @param algebra The algebra to provide for Expressions built.
 */
public abstract class FunctionalExpressionAlgebra<T, out A : Algebra<T>>(
    public val algebra: A,
) : ExpressionAlgebra<T, Expression<T>> {
    /**
     * Builds an Expression of constant expression that does not depend on arguments.
     */
    override fun const(value: T): Expression<T> = Expression { value }

    /**
     * Builds an Expression to access a variable.
     */
    override fun bindSymbolOrNull(value: String): Expression<T>? = Expression { arguments ->
        algebra.bindSymbolOrNull(value)
            ?: arguments[StringSymbol(value)]
            ?: error("Symbol '$value' is not supported in $this")
    }

    override fun binaryOperationFunction(operation: String): (left: Expression<T>, right: Expression<T>) -> Expression<T> =
        { left, right ->
            Expression { arguments ->
                algebra.binaryOperationFunction(operation)(left(arguments), right(arguments))
            }
        }

    override fun unaryOperationFunction(operation: String): (arg: Expression<T>) -> Expression<T> = { arg ->
        Expression { arguments -> algebra.unaryOperation(operation, arg(arguments)) }
    }
}

/**
 * A context class for [Expression] construction for [Ring] algebras.
 */
public open class FunctionalExpressionGroup<T, out A : Group<T>>(
    algebra: A,
) : FunctionalExpressionAlgebra<T, A>(algebra), Group<Expression<T>> {
    override val zero: Expression<T> get() = const(algebra.zero)

    override fun Expression<T>.unaryMinus(): Expression<T> =
        unaryOperation(GroupOps.MINUS_OPERATION, this)

    /**
     * Builds an Expression of addition of two another expressions.
     */
    override fun add(left: Expression<T>, right: Expression<T>): Expression<T> =
        binaryOperation(GroupOps.PLUS_OPERATION, left, right)

//    /**
//     * Builds an Expression of multiplication of expression by number.
//     */
//    override fun multiply(a: Expression<T>, k: Number): Expression<T> = Expression { arguments ->
//        algebra.multiply(a.invoke(arguments), k)
//    }

    public operator fun Expression<T>.plus(arg: T): Expression<T> = this + const(arg)
    public operator fun Expression<T>.minus(arg: T): Expression<T> = this - const(arg)
    public operator fun T.plus(arg: Expression<T>): Expression<T> = arg + this
    public operator fun T.minus(arg: Expression<T>): Expression<T> = arg - this

    override fun unaryOperationFunction(operation: String): (arg: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionAlgebra>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Expression<T>, right: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionAlgebra>.binaryOperationFunction(operation)

}

public open class FunctionalExpressionRing<T, out A : Ring<T>>(
    algebra: A,
) : FunctionalExpressionGroup<T, A>(algebra), Ring<Expression<T>> {
    override val one: Expression<T> get() = const(algebra.one)

    /**
     * Builds an Expression of multiplication of two expressions.
     */
    override fun multiply(left: Expression<T>, right: Expression<T>): Expression<T> =
        binaryOperationFunction(RingOps.TIMES_OPERATION)(left, right)

    public operator fun Expression<T>.times(arg: T): Expression<T> = this * const(arg)
    public operator fun T.times(arg: Expression<T>): Expression<T> = arg * this

    override fun unaryOperationFunction(operation: String): (arg: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionGroup>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Expression<T>, right: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionGroup>.binaryOperationFunction(operation)
}

public open class FunctionalExpressionField<T, out A : Field<T>>(
    algebra: A,
) : FunctionalExpressionRing<T, A>(algebra), Field<Expression<T>>, ScaleOperations<Expression<T>> {
    /**
     * Builds an Expression of division an expression by another one.
     */
    override fun divide(left: Expression<T>, right: Expression<T>): Expression<T> =
        binaryOperationFunction(FieldOps.DIV_OPERATION)(left, right)

    public operator fun Expression<T>.div(arg: T): Expression<T> = this / const(arg)
    public operator fun T.div(arg: Expression<T>): Expression<T> = arg / this

    override fun unaryOperationFunction(operation: String): (arg: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionRing>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Expression<T>, right: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionRing>.binaryOperationFunction(operation)

    override fun scale(a: Expression<T>, value: Double): Expression<T> = algebra {
        Expression { args -> a(args) * value }
    }

    override fun bindSymbolOrNull(value: String): Expression<T>? =
        super<FunctionalExpressionRing>.bindSymbolOrNull(value)
}

public open class FunctionalExpressionExtendedField<T, out A : ExtendedField<T>>(
    algebra: A,
) : FunctionalExpressionField<T, A>(algebra), ExtendedField<Expression<T>> {
    override fun number(value: Number): Expression<T> = const(algebra.number(value))

    override fun sqrt(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(PowerOperations.SQRT_OPERATION)(arg)

    override fun sin(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(TrigonometricOperations.SIN_OPERATION)(arg)

    override fun cos(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(TrigonometricOperations.COS_OPERATION)(arg)

    override fun asin(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(TrigonometricOperations.ASIN_OPERATION)(arg)

    override fun acos(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(TrigonometricOperations.ACOS_OPERATION)(arg)

    override fun atan(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(TrigonometricOperations.ATAN_OPERATION)(arg)

    override fun power(arg: Expression<T>, pow: Number): Expression<T> =
        binaryOperationFunction(PowerOperations.POW_OPERATION)(arg, number(pow))

    override fun exp(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(ExponentialOperations.EXP_OPERATION)(arg)

    override fun ln(arg: Expression<T>): Expression<T> =
        unaryOperationFunction(ExponentialOperations.LN_OPERATION)(arg)

    override fun unaryOperationFunction(operation: String): (arg: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionField>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Expression<T>, right: Expression<T>) -> Expression<T> =
        super<FunctionalExpressionField>.binaryOperationFunction(operation)
}

public inline fun <T, A : Group<T>> A.expressionInGroup(
    block: FunctionalExpressionGroup<T, A>.() -> Expression<T>,
): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionGroup(this).block()
}

public inline fun <T, A : Ring<T>> A.expressionInRing(
    block: FunctionalExpressionRing<T, A>.() -> Expression<T>,
): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionRing(this).block()
}

public inline fun <T, A : Field<T>> A.expressionInField(
    block: FunctionalExpressionField<T, A>.() -> Expression<T>,
): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionField(this).block()
}

public inline fun <T, A : ExtendedField<T>> A.expressionInExtendedField(
    block: FunctionalExpressionExtendedField<T, A>.() -> Expression<T>,
): Expression<T> = FunctionalExpressionExtendedField(this).block()

public inline fun DoubleField.expression(
    block: FunctionalExpressionExtendedField<Double, DoubleField>.() -> Expression<Double>,
): Expression<Double> = FunctionalExpressionExtendedField(this).block()
