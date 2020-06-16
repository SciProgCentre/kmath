package scientifik.kmath.ast

import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.FunctionalExpressionField
import scientifik.kmath.expressions.FunctionalExpressionRing
import scientifik.kmath.expressions.FunctionalExpressionSpace
import scientifik.kmath.operations.*

/**
 * The expression evaluates MST on-flight. Should be much faster than functional expression, but slower than ASM-generated expressions.
 */
class MSTExpression<T>(val algebra: Algebra<T>, val mst: MST) : Expression<T> {

    /**
     * Substitute algebra raw value
     */
    private inner class InnerAlgebra(val arguments: Map<String, T>) : NumericAlgebra<T>{
        override fun symbol(value: String): T = arguments[value] ?: algebra.symbol(value)
        override fun unaryOperation(operation: String, arg: T): T  = algebra.unaryOperation(operation, arg)

        override fun binaryOperation(operation: String, left: T, right: T): T =algebra.binaryOperation(operation, left, right)

        override fun number(value: Number): T = if(algebra is NumericAlgebra){
            algebra.number(value)
        } else{
            error("Numeric nodes are not supported by $this")
        }
    }

    override fun invoke(arguments: Map<String, T>): T = InnerAlgebra(arguments).evaluate(mst)
}


inline fun <reified T : Any, A : Algebra<T>, E : Algebra<MST>> A.mst(
    mstAlgebra: E,
    block: E.() -> MST
): MSTExpression<T> = MSTExpression(this, mstAlgebra.block())

inline fun <reified T : Any> Space<T>.mstInSpace(block: MSTSpace.() -> MST): MSTExpression<T> =
    MSTExpression(this, MSTSpace.block())

inline fun <reified T : Any>  Ring<T>.mstInRing(block: MSTRing.() -> MST): MSTExpression<T> =
    MSTExpression(this, MSTRing.block())

inline fun <reified T : Any> Field<T>.mstInField(block: MSTField.() -> MST): MSTExpression<T> =
    MSTExpression(this, MSTField.block())

inline fun <reified T: Any, A : Space<T>> FunctionalExpressionSpace<T, A>.mstInSpace(block: MSTSpace.() -> MST): MSTExpression<T> =
    algebra.mstInSpace(block)

inline fun <reified T: Any, A : Ring<T>> FunctionalExpressionRing<T, A>.mstInRing(block: MSTRing.() -> MST): MSTExpression<T> =
    algebra.mstInRing(block)

inline fun <reified T: Any, A : Field<T>> FunctionalExpressionField<T, A>.mstInField(block: MSTField.() -> MST): MSTExpression<T> =
    algebra.mstInField(block)