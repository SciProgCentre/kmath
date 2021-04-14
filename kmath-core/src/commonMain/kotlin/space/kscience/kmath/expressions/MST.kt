package space.kscience.kmath.expressions

import space.kscience.kmath.misc.StringSymbol
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra

/**
 * A Mathematical Syntax Tree (MST) node for mathematical expressions.
 *
 * @author Alexander Nozik
 */
public sealed class MST {
    /**
     * A node containing raw string.
     *
     * @property value the value of this node.
     */
    public data class Symbolic(val value: String) : MST()

    /**
     * A node containing a numeric value or scalar.
     *
     * @property value the value of this number.
     */
    public data class Numeric(val value: Number) : MST()

    /**
     * A node containing an unary operation.
     *
     * @property operation the identifier of operation.
     * @property value the argument of this operation.
     */
    public data class Unary(val operation: String, val value: MST) : MST()

    /**
     * A node containing binary operation.
     *
     * @property operation the identifier operation.
     * @property left the left operand.
     * @property right the right operand.
     */
    public data class Binary(val operation: String, val left: MST, val right: MST) : MST()
}

// TODO add a function with named arguments

/**
 * Interprets the [MST] node with this [Algebra].
 *
 * @receiver the algebra that provides operations.
 * @param node the node to evaluate.
 * @return the value of expression.
 * @author Alexander Nozik
 */
public fun <T> Algebra<T>.evaluate(node: MST): T = when (node) {
    is MST.Numeric -> (this as? NumericAlgebra<T>)?.number(node.value)
        ?: error("Numeric nodes are not supported by $this")

    is MST.Symbolic -> bindSymbol(node.value)

    is MST.Unary -> when {
        this is NumericAlgebra && node.value is MST.Numeric -> unaryOperationFunction(node.operation)(number(node.value.value))
        else -> unaryOperationFunction(node.operation)(evaluate(node.value))
    }

    is MST.Binary -> when {
        this is NumericAlgebra && node.left is MST.Numeric && node.right is MST.Numeric ->
            binaryOperationFunction(node.operation)(number(node.left.value), number(node.right.value))

        this is NumericAlgebra && node.left is MST.Numeric ->
            leftSideNumberOperationFunction(node.operation)(node.left.value, evaluate(node.right))

        this is NumericAlgebra && node.right is MST.Numeric ->
            rightSideNumberOperationFunction(node.operation)(evaluate(node.left), node.right.value)

        else -> binaryOperationFunction(node.operation)(evaluate(node.left), evaluate(node.right))
    }
}

internal class InnerAlgebra<T : Any>(val algebra: Algebra<T>, val arguments: Map<Symbol, T>) : NumericAlgebra<T> {
    override fun bindSymbolOrNull(value: String): T? = algebra.bindSymbolOrNull(value) ?: arguments[StringSymbol(value)]

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

/**
 * Interprets the [MST] node with this [Algebra] and optional [arguments]
 */
public fun <T : Any> MST.interpret(algebra: Algebra<T>, arguments: Map<Symbol, T>): T =
    InnerAlgebra(algebra, arguments).evaluate(this)

/**
 * Interprets the [MST] node with this [Algebra] and optional [arguments]
 *
 * @receiver the node to evaluate.
 * @param algebra the algebra that provides operations.
 * @return the value of expression.
 */
public fun <T : Any> MST.interpret(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T =
    interpret(algebra, mapOf(*arguments))

/**
 * Interpret this [MST] as expression.
 */
public fun <T : Any> MST.toExpression(algebra: Algebra<T>): Expression<T> = Expression { arguments ->
    interpret(algebra, arguments)
}
