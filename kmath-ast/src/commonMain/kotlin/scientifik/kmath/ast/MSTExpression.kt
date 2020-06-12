package scientifik.kmath.ast

import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.NumericAlgebra

/**
 * The expression evaluates MST on-flight. Should be much faster than functional expression, but slower than ASM-generated expressions.
 */
class MSTExpression<T>(val algebra: NumericAlgebra<T>, val mst: MST) : Expression<T> {

    /**
     * Substitute algebra raw value
     */
    private inner class InnerAlgebra(val arguments: Map<String, T>) : NumericAlgebra<T> by algebra {
        override fun raw(value: String): T = arguments[value] ?: super.raw(value)
    }

    override fun invoke(arguments: Map<String, T>): T = InnerAlgebra(arguments).evaluate(mst)
}