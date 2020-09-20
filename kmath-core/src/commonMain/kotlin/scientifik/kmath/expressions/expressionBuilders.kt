package scientifik.kmath.expressions

import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a functional expression with this [Space].
 */
public inline fun <T> Space<T>.spaceExpression(block: FunctionalExpressionSpace<T, Space<T>>.() -> Expression<T>): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionSpace(this).block()
}

/**
 * Creates a functional expression with this [Ring].
 */
public inline fun <T> Ring<T>.ringExpression(block: FunctionalExpressionRing<T, Ring<T>>.() -> Expression<T>): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionRing(this).block()
}

/**
 * Creates a functional expression with this [Field].
 */
public inline fun <T> Field<T>.fieldExpression(block: FunctionalExpressionField<T, Field<T>>.() -> Expression<T>): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionField(this).block()
}

/**
 * Creates a functional expression with this [ExtendedField].
 */
public inline fun <T> ExtendedField<T>.extendedFieldExpression(block: FunctionalExpressionExtendedField<T, ExtendedField<T>>.() -> Expression<T>): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionExtendedField(this).block()
}
