package scientifik.kmath.ast

import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.NumericAlgebra
import scientifik.kmath.operations.RealField

/**
 * A Mathematical Syntax Tree node for mathematical expressions
 */
sealed class MST {

    /**
     * A node containing unparsed string
     */
    data class Symbolic(val value: String) : MST()

    /**
     * A node containing a number
     */
    data class Numeric(val value: Number) : MST()

    /**
     * A node containing an unary operation
     */
    data class Unary(val operation: String, val value: MST) : MST() {
        companion object {
            const val ABS_OPERATION = "abs"
            //TODO add operations
        }
    }

    /**
     * A node containing binary operation
     */
    data class Binary(val operation: String, val left: MST, val right: MST) : MST() {
        companion object
    }
}

//TODO add a function with positional arguments

//TODO add a function with named arguments

fun <T> Algebra<T>.evaluate(node: MST): T = when (node) {
    is MST.Numeric -> (this as? NumericAlgebra<T>)?.number(node.value)
        ?: error("Numeric nodes are not supported by $this")
    is MST.Symbolic -> symbol(node.value)
    is MST.Unary -> unaryOperation(node.operation, evaluate(node.value))
    is MST.Binary -> when {
        this !is NumericAlgebra -> binaryOperation(node.operation, evaluate(node.left), evaluate(node.right))

        node.left is MST.Numeric && node.right is MST.Numeric -> {
            val number = RealField.binaryOperation(
                node.operation,
                node.left.value.toDouble(),
                node.right.value.toDouble()
            )

            number(number)
        }

        node.left is MST.Numeric -> leftSideNumberOperation(node.operation, node.left.value, evaluate(node.right))
        node.right is MST.Numeric -> rightSideNumberOperation(node.operation, evaluate(node.left), node.right.value)
        else -> binaryOperation(node.operation, evaluate(node.left), evaluate(node.right))
    }
}

fun <T> MST.compile(algebra: Algebra<T>): T = algebra.evaluate(this)
