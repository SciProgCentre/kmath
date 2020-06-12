package scientifik.kmath.expressions

import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

/**
 * Create a functional expression on this [Algebra]
 */
fun <T> Algebra<T>.buildExpression(block: FunctionalExpressionAlgebra<T>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionAlgebra(this).run(block)

/**
 * Create a functional expression on this [Space]
 */
fun <T> Space<T>.buildExpression(block: FunctionalExpressionSpace<T>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionSpace(this).run(block)

/**
 * Create a functional expression on this [Ring]
 */
fun <T> Ring<T>.buildExpression(block: FunctionalExpressionRing<T>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionRing(this).run(block)

/**
 * Create a functional expression on this [Field]
 */
fun <T> Field<T>.buildExpression(block: FunctionalExpressionField<T>.() -> Expression<T>): Expression<T> =
    FunctionalExpressionField(this).run(block)
