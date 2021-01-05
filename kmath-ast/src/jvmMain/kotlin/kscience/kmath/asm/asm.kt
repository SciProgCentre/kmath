package kscience.kmath.asm

import kscience.kmath.asm.internal.AsmBuilder
import kscience.kmath.asm.internal.buildName
import kscience.kmath.ast.MST
import kscience.kmath.ast.MstExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.NumericAlgebra
import kscience.kmath.operations.RealField

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
        is MST.Symbolic -> {
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

        is MST.Numeric -> loadNumberConstant(node.value)
        is MST.Unary -> buildCall(algebra.unaryOperationFunction(node.operation)) { visit(node.value) }

        is MST.Binary -> when {
            algebra is NumericAlgebra<T> && node.left is MST.Numeric && node.right is MST.Numeric -> loadObjectConstant(
                algebra.number(
                    RealField
                        .binaryOperationFunction(node.operation)
                        .invoke(node.left.value.toDouble(), node.right.value.toDouble())
                )
            )

            algebra is NumericAlgebra<T> && node.left is MST.Numeric -> buildCall(algebra.leftSideNumberOperationFunction(node.operation)) {
                visit(node.left)
                visit(node.right)
            }

            algebra is NumericAlgebra<T> && node.right is MST.Numeric -> buildCall(algebra.rightSideNumberOperationFunction(node.operation)) {
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
