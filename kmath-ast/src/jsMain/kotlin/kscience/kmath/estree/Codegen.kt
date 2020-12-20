package kscience.kmath.estree

import kscience.kmath.ast.MST
import kscience.kmath.ast.MstExpression
import kscience.kmath.estree.internal.JSBuilder
import kscience.kmath.estree.internal.estree.BaseExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.NumericAlgebra
import kscience.kmath.operations.RealField

@PublishedApi
internal fun <T> MST.compileWith(algebra: Algebra<T>): Expression<T> {
    fun JSBuilder<T>.visit(node: MST): BaseExpression = when (node) {
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
        is MST.Unary -> call(algebra.unaryOperation(node.operation), visit(node.value))

        is MST.Binary -> when {
            algebra is NumericAlgebra<T> && node.left is MST.Numeric && node.right is MST.Numeric -> constant(
                algebra.number(
                    RealField
                        .binaryOperation(node.operation)
                        .invoke(node.left.value.toDouble(), node.right.value.toDouble())
                )
            )

            algebra is NumericAlgebra<T> && node.left is MST.Numeric -> call(
                algebra.leftSideNumberOperation(node.operation),
                visit(node.left),
                visit(node.right),
            )

            algebra is NumericAlgebra<T> && node.right is MST.Numeric -> call(
                algebra.rightSideNumberOperation(node.operation),
                visit(node.left),
                visit(node.right),
            )

            else -> call(
                algebra.binaryOperation(node.operation),
                visit(node.left),
                visit(node.right),
            )
        }
    }

    return JSBuilder<T> { visit(this@compileWith) }.instance
}


/**
 * Compiles an [MST] to ASM using given algebra.
 *
 * @author Alexander Nozik.
 */
public fun <T : Any> Algebra<T>.expression(mst: MST): Expression<T> =
    mst.compileWith(this)

/**
 * Optimizes performance of an [MstExpression] using ASM codegen.
 *
 * @author Alexander Nozik.
 */
public fun <T : Any> MstExpression<T, Algebra<T>>.compile(): Expression<T> =
    mst.compileWith(algebra)
