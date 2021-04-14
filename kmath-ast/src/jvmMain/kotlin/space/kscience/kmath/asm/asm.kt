package space.kscience.kmath.asm

import space.kscience.kmath.asm.internal.AsmBuilder
import space.kscience.kmath.asm.internal.buildName
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MST.*
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra

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
            val symbol = algebra.bindSymbolOrNull(node.value)

            if (symbol != null)
                loadObjectConstant(symbol as Any)
            else
                loadVariable(node.value)
        }

        is Numeric -> loadNumberConstant(node.value)

        is Unary -> when {
            algebra is NumericAlgebra && node.value is Numeric -> loadObjectConstant(
                algebra.unaryOperationFunction(node.operation)(algebra.number((node.value as Numeric).value)))

            else -> buildCall(algebra.unaryOperationFunction(node.operation)) { visit(node.value) }
        }

        is Binary -> when {
            algebra is NumericAlgebra && node.left is Numeric && node.right is Numeric -> loadObjectConstant(
                algebra.binaryOperationFunction(node.operation).invoke(
                    algebra.number((node.left as Numeric).value),
                    algebra.number((node.right as Numeric).value)
                )
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
 * Create a compiled expression with given [MST] and given [algebra].
 */
public inline fun <reified T : Any> MST.compileToExpression(algebra: Algebra<T>): Expression<T> =
    compileWith(T::class.java, algebra)


/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(algebra: Algebra<T>, arguments: Map<Symbol, T>): T =
    compileToExpression(algebra).invoke(arguments)

/**
 * Compile given MST to expression and evaluate it against [arguments]
 */
public inline fun <reified T : Any> MST.compile(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    compileToExpression(algebra).invoke(*arguments)
