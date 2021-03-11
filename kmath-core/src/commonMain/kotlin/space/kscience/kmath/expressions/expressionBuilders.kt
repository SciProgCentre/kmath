package space.kscience.kmath.expressions

import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.Ring
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Creates a functional expression with this [Group].
 */
public inline fun <T> Group<T>.spaceExpression(block: FunctionalExpressionGroup<T, Group<T>>.() -> Expression<T>): Expression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return FunctionalExpressionGroup(this).block()
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
