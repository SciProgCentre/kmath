package scientifik.kmath.expressions

sealed class SyntaxTreeNode

data class SingularNode(val value: String) : SyntaxTreeNode()

data class NumberNode(val value: Number) : SyntaxTreeNode()

data class UnaryNode(val operation: String, val value: SyntaxTreeNode) : SyntaxTreeNode() {
    companion object {
        const val PLUS_OPERATION = "+"
        const val MINUS_OPERATION = "-"
        const val NOT_OPERATION = "!"
        const val ABS_OPERATION = "abs"
        const val SIN_OPERATION = "sin"
        const val COS_OPERATION = "cos"
        const val EXP_OPERATION = "exp"
        const val LN_OPERATION = "ln"
        //TODO add operations
    }
}

data class BinaryNode(val operation: String, val left: SyntaxTreeNode, val right: SyntaxTreeNode) : SyntaxTreeNode() {
    companion object {
        const val PLUS_OPERATION = "+"
        const val MINUS_OPERATION = "-"
        const val TIMES_OPERATION = "*"
        const val DIV_OPERATION = "/"
        //TODO add operations
    }
}

//TODO add a function with positional arguments

//TODO add a function with named arguments