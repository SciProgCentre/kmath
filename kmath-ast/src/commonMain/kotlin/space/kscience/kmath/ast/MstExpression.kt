package space.kscience.kmath.ast

import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The expression evaluates MST on-flight. Should be much faster than functional expression, but slower than
 * ASM-generated expressions.
 *
 * @property algebra the algebra that provides operations.
 * @property mst the [MST] node.
 * @author Alexander Nozik
 */
public class MstExpression<T, out A : Algebra<T>>(public val algebra: A, public val mst: MST) : Expression<T> {
    private inner class InnerAlgebra(val arguments: Map<Symbol, T>) : NumericAlgebra<T> {
        override fun bindSymbol(value: String): T = try {
            algebra.bindSymbol(value)
        } catch (ignored: IllegalStateException) {
            null
        } ?: arguments.getValue(StringSymbol(value))

        override fun unaryOperation(operation: String, arg: T): T =
            algebra.unaryOperation(operation, arg)

        override fun binaryOperation(operation: String, left: T, right: T): T =
            algebra.binaryOperation(operation, left, right)

        override fun unaryOperationFunction(operation: String): (arg: T) -> T =
            algebra.unaryOperationFunction(operation)

        override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T =
            algebra.binaryOperationFunction(operation)

        @Suppress("UNCHECKED_CAST")
        override fun number(value: Number): T = if (algebra is NumericAlgebra<*>)
            (algebra as NumericAlgebra<T>).number(value)
        else
            error("Numeric nodes are not supported by $this")
    }

    override operator fun invoke(arguments: Map<Symbol, T>): T = InnerAlgebra(arguments).evaluate(mst)
}

/**
 * Builds [MstExpression] over [Algebra].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Algebra<T>, E : Algebra<MST>> A.mst(
    mstAlgebra: E,
    block: E.() -> MST,
): MstExpression<T, A> = MstExpression(this, mstAlgebra.block())

/**
 * Builds [MstExpression] over [Group].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Group<T>> A.mstInGroup(block: MstGroup.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstGroup.block())
}

/**
 * Builds [MstExpression] over [Ring].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Ring<T>> A.mstInRing(block: MstRing.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstRing.block())
}

/**
 * Builds [MstExpression] over [Field].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Field<T>> A.mstInField(block: MstField.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstField.block())
}

/**
 * Builds [MstExpression] over [ExtendedField].
 *
 * @author Iaroslav Postovalov
 */
public inline fun <reified T : Any, A : ExtendedField<T>> A.mstInExtendedField(block: MstExtendedField.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MstExpression(this, MstExtendedField.block())
}

/**
 * Builds [MstExpression] over [FunctionalExpressionGroup].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Group<T>> FunctionalExpressionGroup<T, A>.mstInGroup(block: MstGroup.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInGroup(block)
}

/**
 * Builds [MstExpression] over [FunctionalExpressionRing].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Ring<T>> FunctionalExpressionRing<T, A>.mstInRing(block: MstRing.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInRing(block)
}

/**
 * Builds [MstExpression] over [FunctionalExpressionField].
 *
 * @author Alexander Nozik
 */
public inline fun <reified T : Any, A : Field<T>> FunctionalExpressionField<T, A>.mstInField(block: MstField.() -> MST): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInField(block)
}

/**
 * Builds [MstExpression] over [FunctionalExpressionExtendedField].
 *
 * @author Iaroslav Postovalov
 */
public inline fun <reified T : Any, A : ExtendedField<T>> FunctionalExpressionExtendedField<T, A>.mstInExtendedField(
    block: MstExtendedField.() -> MST,
): MstExpression<T, A> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return algebra.mstInExtendedField(block)
}
