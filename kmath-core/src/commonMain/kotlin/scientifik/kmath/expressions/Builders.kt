package scientifik.kmath.expressions

import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

/**
 * Creates a functional expression with this [Space].
 */
fun <T> Space<T>.spaceExpression(block: FunctionalExpressionSpace<T, Space<T>>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionSpace(this).run(block)

/**
 * Creates a functional expression with this [Ring].
 */
fun <T> Ring<T>.ringExpression(block: FunctionalExpressionRing<T, Ring<T>>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionRing(this).run(block)

/**
 * Creates a functional expression with this [Field].
 */
fun <T> Field<T>.fieldExpression(block: FunctionalExpressionField<T, Field<T>>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionField(this).run(block)

/**
 * Creates a functional expression with this [ExtendedField].
 */
fun <T> ExtendedField<T>.fieldExpression(
    block: FunctionalExpressionExtendedField<T, ExtendedField<T>>.() -> Expression<T>
): Expression<T> = FunctionalExpressionExtendedField(this).run(block)
