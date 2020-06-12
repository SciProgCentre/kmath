package scientifik.kmath.ast

import scientifik.kmath.operations.NumericAlgebra
import scientifik.kmath.operations.RealField

/**
 * A syntax tree node for mathematical expressions
 */
sealed class MathSyntaxTree

/**
 * A node containing unparsed string
 */
data class SingularNode(val value: String) : MathSyntaxTree()

/**
 * A node containing a number
 */
data class NumberNode(val value: Number) : MathSyntaxTree()

/**
 * A node containing an unary operation
 */
data class UnaryNode(val operation: String, val value: MathSyntaxTree) : MathSyntaxTree() {
    companion object {
        const val ABS_OPERATION = "abs"
        //TODO add operations
    }
}

/**
 * A node containing binary operation
 */
data class BinaryNode(val operation: String, val left: MathSyntaxTree, val right: MathSyntaxTree) : MathSyntaxTree() {
    companion object
}

//TODO add a function with positional arguments

//TODO add a function with named arguments

fun <T> NumericAlgebra<T>.compile(node: MathSyntaxTree): T {
    return when (node) {
        is NumberNode -> number(node.value)
        is SingularNode -> raw(node.value)
        is UnaryNode -> unaryOperation(node.operation, compile(node.value))
        is BinaryNode -> when {
            node.left is NumberNode && node.right is NumberNode -> {
                val number = RealField.binaryOperation(
                    node.operation,
                    node.left.value.toDouble(),
                    node.right.value.toDouble()
                )
                number(number)
            }
            node.left is NumberNode -> leftSideNumberOperation(node.operation, node.left.value, compile(node.right))
            node.right is NumberNode -> rightSideNumberOperation(node.operation, compile(node.left), node.right.value)
            else -> binaryOperation(node.operation, compile(node.left), compile(node.right))
        }
    }
}