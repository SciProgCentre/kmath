package scientifik.kmath.asm.internal

import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Algebra

/**
 * [Expression] partial implementation to have it subclassed by actual implementations. Provides unified storage for
 * objects needed to implement the expression.
 *
 * @property algebra the algebra to delegate calls.
 * @property constants the constants array to have persistent objects to reference in [invoke].
 */
internal abstract class AsmCompiledExpression<T> internal constructor(
    @JvmField protected val algebra: Algebra<T>,
    @JvmField protected val constants: Array<Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}
