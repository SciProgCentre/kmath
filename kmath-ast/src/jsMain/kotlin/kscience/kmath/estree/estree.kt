package kscience.kmath.estree

import kscience.kmath.ast.MST
import kscience.kmath.ast.MST.*
import kscience.kmath.ast.MstExpression
import kscience.kmath.estree.internal.ESTreeBuilder
import kscience.kmath.estree.internal.estree.BaseExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.NumericAlgebra

@PublishedApi
internal fun <T> MST.compileWith(algebra: Algebra<T>): Expression<T> {
    fun ESTreeBuilder<T>.visit(node: MST): BaseExpression = when (node) {
        is Symbolic -> {
            val symbol = try {
                algebra.symbol(node.value)
            } catch (ignored: IllegalStateException) {
                null
            }

            if (symbol != null)
                constant(symbol)
            else
                variable(node.value)
        }

        is Numeric -> constant(node.value)

        is Unary -> when {
            algebra is NumericAlgebra && node.value is Numeric -> constant(
                algebra.unaryOperationFunction(node.operation)(algebra.number(node.value.value)))

            else -> call(algebra.unaryOperationFunction(node.operation), visit(node.value))
        }

        is Binary -> when {
            algebra is NumericAlgebra && node.left is Numeric && node.right is Numeric -> constant(
                algebra
                    .binaryOperationFunction(node.operation)
                    .invoke(algebra.number(node.left.value), algebra.number(node.right.value))
            )

            algebra is NumericAlgebra && node.left is Numeric -> call(
                algebra.leftSideNumberOperationFunction(node.operation),
                visit(node.left),
                visit(node.right),
            )

            algebra is NumericAlgebra && node.right is Numeric -> call(
                algebra.rightSideNumberOperationFunction(node.operation),
                visit(node.left),
                visit(node.right),
            )

            else -> call(
                algebra.binaryOperationFunction(node.operation),
                visit(node.left),
                visit(node.right),
            )
        }
    }

    return ESTreeBuilder<T> { visit(this@compileWith) }.instance
}


/**
 * Compiles an [MST] to ESTree generated expression using given algebra.
 *
 * @author Alexander Nozik.
 */
public fun <T : Any> Algebra<T>.expression(mst: MST): Expression<T> =
    mst.compileWith(this)

/**
 * Optimizes performance of an [MstExpression] by compiling it into ESTree generated expression.
 *
 * @author Alexander Nozik.
 */
public fun <T : Any> MstExpression<T, Algebra<T>>.compile(): Expression<T> =
    mst.compileWith(algebra)
