package scientifik.kmath.asm.internal

import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Algebra

internal abstract class AsmCompiledExpression<T> internal constructor(
    @JvmField protected val algebra: Algebra<T>,
    @JvmField protected val constants: Array<Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

