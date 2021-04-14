package space.kscience.kmath.estree

import space.kscience.kmath.estree.internal.ESTreeBuilder
import space.kscience.kmath.estree.internal.estree.BaseExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MST.*
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra

@PublishedApi
internal fun <T> MST.compileWith(algebra: Algebra<T>): Expression<T> {
    fun ESTreeBuilder<T>.visit(node: MST): BaseExpression = when (node) {
        is Symbolic -> {
            val symbol = algebra.bindSymbolOrNull(node.value)

            if (symbol != null)
                constant(symbol)
            else
                variable(node.value)
        }

        is Numeric -> constant(node.value)

        is Unary -> when {
            algebra is NumericAlgebra && node.value is Numeric -> constant(
                algebra.unaryOperationFunction(node.operation)(algebra.number((node.value as Numeric).value)))

            else -> call(algebra.unaryOperationFunction(node.operation), visit(node.value))
        }

        is Binary -> when {
            algebra is NumericAlgebra && node.left is Numeric && node.right is Numeric -> constant(
                algebra.binaryOperationFunction(node.operation).invoke(
                    algebra.number((node.left as Numeric).value),
                    algebra.number((node.right as Numeric).value)
                )
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
 * Create a compiled expression with given [MST] and given [algebra].
 */
public fun <T : Any> MST.compileToExpression(algebra: Algebra<T>): Expression<T> = compileWith(algebra)


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
