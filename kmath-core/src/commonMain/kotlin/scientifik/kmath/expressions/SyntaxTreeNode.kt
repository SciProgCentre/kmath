package scientifik.kmath.expressions

import scientifik.kmath.operations.NumericAlgebra

/**
 * A syntax tree node for mathematical expressions
 */
sealed class SyntaxTreeNode

/**
 * A node containing unparsed string
 */
data class SingularNode(val value: String) : SyntaxTreeNode()

/**
 * A node containing a number
 */
data class NumberNode(val value: Number) : SyntaxTreeNode()

/**
 * A node containing an unary operation
 */
data class UnaryNode(val operation: String, val value: SyntaxTreeNode) : SyntaxTreeNode() {
    companion object {
        const val ABS_OPERATION = "abs"
        const val SIN_OPERATION = "sin"
        const val COS_OPERATION = "cos"
        const val EXP_OPERATION = "exp"
        const val LN_OPERATION = "ln"
        //TODO add operations
    }
}

/**
 * A node containing binary operation
 */
data class BinaryNode(val operation: String, val left: SyntaxTreeNode, val right: SyntaxTreeNode) : SyntaxTreeNode() {
    companion object
}

//TODO add a function with positional arguments

//TODO add a function with named arguments

fun <T> NumericAlgebra<T>.compile(node: SyntaxTreeNode): T{
    return when (node) {
        is NumberNode -> number(node.value)
        is SingularNode -> raw(node.value)
        is UnaryNode -> unaryOperation(node.operation, compile(node.value))
        is BinaryNode -> binaryOperation(node.operation, compile(node.left), compile(node.right))
    }
}