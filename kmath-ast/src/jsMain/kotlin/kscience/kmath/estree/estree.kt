package kscience.kmath.estree

import kscience.kmath.ast.MST
import kscience.kmath.ast.MstExpression
import kscience.kmath.estree.internal.ESTreeBuilder
import kscience.kmath.estree.internal.estree.BaseExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.NumericAlgebra
import kscience.kmath.operations.RealField

@PublishedApi
internal fun <T> MST.compileWith(algebra: Algebra<T>): Expression<T> {
    fun ESTreeBuilder<T>.visit(node: MST): BaseExpression = when (node) {
        is MST.Symbolic -> {
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

        is MST.Numeric -> constant(node.value)
        is MST.Unary -> call(algebra.unaryOperationFunction(node.operation), visit(node.value))

        is MST.Binary -> when {
            algebra is NumericAlgebra<T> && node.left is MST.Numeric && node.right is MST.Numeric -> constant(
                algebra.number(
                    RealField
                        .binaryOperationFunction(node.operation)
                        .invoke(node.left.value.toDouble(), node.right.value.toDouble())
                )
            )

            algebra is NumericAlgebra<T> && node.left is MST.Numeric -> call(
                algebra.leftSideNumberOperationFunction(node.operation),
                visit(node.left),
                visit(node.right),
            )

            algebra is NumericAlgebra<T> && node.right is MST.Numeric -> call(
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
