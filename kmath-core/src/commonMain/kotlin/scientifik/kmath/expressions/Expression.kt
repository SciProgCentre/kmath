package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space

/**
 * An elementary function that could be invoked on a map of arguments
 */
interface Expression<T> {
    operator fun invoke(arguments: Map<String, T>): T
}

operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T = invoke(mapOf(*pairs))

/**
 * A context for expression construction
 */
interface ExpressionContext<T, E> {
    /**
     * Introduce a variable into expression context
     */
    fun variable(name: String, default: T? = null): E

    /**
     * A constant expression which does not depend on arguments
     */
    fun const(value: T): E

    fun produce(node: SyntaxTreeNode): E
}

interface ExpressionSpace<T, E> : Space<E>, ExpressionContext<T, E> {

    fun produceSingular(value: String): E = variable(value)

    fun produceUnary(operation: String, value: E): E {
        return when (operation) {
            UnaryNode.PLUS_OPERATION -> value
            UnaryNode.MINUS_OPERATION -> -value
            else -> error("Unary operation $operation is not supported by $this")
        }
    }

    fun produceBinary(operation: String, left: E, right: E): E {
        return when (operation) {
            BinaryNode.PLUS_OPERATION -> left + right
            BinaryNode.MINUS_OPERATION -> left - right
            else -> error("Binary operation $operation is not supported by $this")
        }
    }

    override fun produce(node: SyntaxTreeNode): E {
        return when (node) {
            is NumberNode -> error("Single number nodes are not supported")
            is SingularNode -> produceSingular(node.value)
            is UnaryNode -> produceUnary(node.operation, produce(node.value))
            is BinaryNode -> {
                when (node.operation) {
                    BinaryNode.TIMES_OPERATION -> {
                        if (node.left is NumberNode) {
                            return produce(node.right) * node.left.value
                        } else if (node.right is NumberNode) {
                            return produce(node.left) * node.right.value
                        }
                    }
                    BinaryNode.DIV_OPERATION -> {
                        if (node.right is NumberNode) {
                            return produce(node.left) / node.right.value
                        }
                    }
                }
                produceBinary(node.operation, produce(node.left), produce(node.right))
            }
        }
    }
}

interface ExpressionField<T, E> : Field<E>, ExpressionSpace<T, E> {
    fun number(value: Number): E = one * value

    override fun produce(node: SyntaxTreeNode): E {
        if (node is BinaryNode) {
            when (node.operation) {
                BinaryNode.PLUS_OPERATION -> {
                    if (node.left is NumberNode) {
                        return produce(node.right) + one * node.left.value
                    } else if (node.right is NumberNode) {
                        return produce(node.left) + one * node.right.value
                    }
                }
                BinaryNode.MINUS_OPERATION -> {
                    if (node.left is NumberNode) {
                        return one * node.left.value - produce(node.right)
                    } else if (node.right is NumberNode) {
                        return produce(node.left) - one * node.right.value
                    }
                }
            }
        }
        return super.produce(node)
    }

    override fun produceBinary(operation: String, left: E, right: E): E {
        return when (operation) {
            BinaryNode.TIMES_OPERATION -> left * right
            BinaryNode.DIV_OPERATION -> left / right
            else -> super.produceBinary(operation, left, right)
        }
    }

}