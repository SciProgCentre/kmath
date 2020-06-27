package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

/**
 * Create a functional expression on this [Space]
 */
fun <T> Space<T>.spaceExpression(block: FunctionalExpressionSpace<T, Space<T>>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionSpace(this).run(block)

/**
 * Create a functional expression on this [Ring]
 */
fun <T> Ring<T>.ringExpression(block: FunctionalExpressionRing<T, Ring<T>>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionRing(this).run(block)

/**
 * Create a functional expression on this [Field]
 */
fun <T> Field<T>.fieldExpression(block: FunctionalExpressionField<T, Field<T>>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionField(this).run(block)
