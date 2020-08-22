package scientifik.kmath.ast

import scientifik.kmath.expressions.*
import scientifik.kmath.operations.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The expression evaluates MST on-flight. Should be much faster than functional expression, but slower than
 * ASM-generated expressions.
 *
 * @property algebra the algebra that provides operations.
 * @property mst the [MST] node.
 */
class MstExpression<T>(val algebra: Algebra<T>, val mst: MST) : Expression<T> {
    private inner class InnerAlgebra(val arguments: Map<String, T>) : NumericAlgebra<T> {
        override fun symbol(value: String): T = arguments[value] ?: algebra.symbol(value)
        override fun unaryOperation(operation: String, arg: T): T = algebra.unaryOperation(operation, arg)

        override fun binaryOperation(operation: String, left: T, right: T): T =
            algebra.binaryOperation(operation, left, right)

        override fun number(value: Number): T = if (algebra is NumericAlgebra)
            algebra.number(value)
        else
            error("Numeric nodes are not supported by $this")
    }

    override operator fun invoke(arguments: Map<String, T>): T = InnerAlgebra(arguments).evaluate(mst)
}

/**
 * Builds [MstExpression] over [Algebra].
 */
inline fun <reified T : Any, A : Algebra<T>, E : Algebra<MST>> A.mst(
    mstAlgebra: E,
    block: E.() -> MST
): MstExpression<T> = MstExpression(this, mstAlgebra.block())

/**
 * Builds [MstExpression] over [Space].
 */
inline fun <reified T : Any> Space<T>.mstInSpace(block: MstSpace.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstSpace.block())
}

/**
 * Builds [MstExpression] over [Ring].
 */
inline fun <reified T : Any> Ring<T>.mstInRing(block: MstRing.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstRing.block())
}

/**
 * Builds [MstExpression] over [Field].
 */
inline fun <reified T : Any> Field<T>.mstInField(block: MstField.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstField.block())
}

/**
 * Builds [MstExpression] over [ExtendedField].
 */
inline fun <reified T : Any> Field<T>.mstInExtendedField(block: MstExtendedField.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstExtendedField.block())
}

/**
 * Builds [MstExpression] over [FunctionalExpressionSpace].
 */
inline fun <reified T : Any, A : Space<T>> FunctionalExpressionSpace<T, A>.mstInSpace(block: MstSpace.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInSpace(block)
}

/**
 * Builds [MstExpression] over [FunctionalExpressionRing].
 */
inline fun <reified T : Any, A : Ring<T>> FunctionalExpressionRing<T, A>.mstInRing(block: MstRing.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInRing(block)
}

/**
 * Builds [MstExpression] over [FunctionalExpressionField].
 */
inline fun <reified T : Any, A : Field<T>> FunctionalExpressionField<T, A>.mstInField(block: MstField.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInField(block)
}

/**
 * Builds [MstExpression] over [FunctionalExpressionExtendedField].
 */
inline fun <reified T : Any, A : ExtendedField<T>> FunctionalExpressionExtendedField<T, A>.mstInExtendedField(block: MstExtendedField.() -> MST): MstExpression<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInExtendedField(block)
}
