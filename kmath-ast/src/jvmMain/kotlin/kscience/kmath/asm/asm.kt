package kscience.kmath.asm

import kscience.kmath.asm.internal.AsmBuilder
import kscience.kmath.asm.internal.buildName
import kscience.kmath.ast.MST
import kscience.kmath.ast.MST.*
import kscience.kmath.ast.MstExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.NumericAlgebra

/**
 * Compiles given MST to an Expression using AST compiler.
 *
 * @param type the target type.
 * @param algebra the target algebra.
 * @return the compiled expression.
 * @author Alexander Nozik
 */
@PublishedApi
internal fun <T : Any> MST.compileWith(type: Class<T>, algebra: Algebra<T>): Expression<T> {
    fun AsmBuilder<T>.visit(node: MST): Unit = when (node) {
        is Symbolic -> {
            val symbol = try {
                algebra.symbol(node.value)
            } catch (ignored: IllegalStateException) {
                null
            }

            if (symbol != null)
                loadObjectConstant(symbol as Any)
            else
                loadVariable(node.value)
        }

        is Numeric -> loadNumberConstant(node.value)

        is Unary -> when {
            algebra is NumericAlgebra && node.value is Numeric -> loadObjectConstant(
                algebra.unaryOperationFunction(node.operation)(algebra.number(node.value.value)))

            else -> buildCall(algebra.unaryOperationFunction(node.operation)) { visit(node.value) }
        }

        is Binary -> when {
            algebra is NumericAlgebra && node.left is Numeric && node.right is Numeric -> loadObjectConstant(
                algebra.binaryOperationFunction(node.operation)
                    .invoke(algebra.number(node.left.value), algebra.number(node.right.value))
            )

            algebra is NumericAlgebra && node.left is Numeric -> buildCall(
                algebra.leftSideNumberOperationFunction(node.operation)) {
                visit(node.left)
                visit(node.right)
            }

            algebra is NumericAlgebra && node.right is Numeric -> buildCall(
                algebra.rightSideNumberOperationFunction(node.operation)) {
                visit(node.left)
                visit(node.right)
            }

            else -> buildCall(algebra.binaryOperationFunction(node.operation)) {
                visit(node.left)
                visit(node.right)
            }
        }
    }

    return AsmBuilder<T>(type, buildName(this)) { visit(this@compileWith) }.instance
}

/**
 * Compiles an [MST] to ASM using given algebra.
 *
 * @author Alexander Nozik.
 */
public inline fun <reified T : Any> Algebra<T>.expression(mst: MST): Expression<T> =
    mst.compileWith(T::class.java, this)

/**
 * Optimizes performance of an [MstExpression] using ASM codegen.
 *
 * @author Alexander Nozik.
 */
public inline fun <reified T : Any> MstExpression<T, Algebra<T>>.compile(): Expression<T> =
    mst.compileWith(T::class.java, algebra)
