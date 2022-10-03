/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra
import space.kscience.kmath.operations.bindSymbolOrNull

/**
 * A Mathematical Syntax Tree (MST) node for mathematical expressions.
 *
 * @author Alexander Nozik
 */
public sealed interface MST {

    /**
     * A node containing a numeric value or scalar.
     *
     * @property value the value of this number.
     */
    public data class Numeric(val value: Number) : MST

    /**
     * A node containing a unary operation.
     *
     * @property operation the identifier of operation.
     * @property value the argument of this operation.
     */
    public data class Unary(val operation: String, val value: MST) : MST

    /**
     * A node containing binary operation.
     *
     * @property operation the identifier of operation.
     * @property left the left operand.
     * @property right the right operand.
     */
    public data class Binary(val operation: String, val left: MST, val right: MST) : MST
}

// TODO add a function with named arguments


/**
 * Interprets the [MST] node with this [Algebra] and optional [arguments]
 */
public fun <T> MST.interpret(algebra: Algebra<T>, arguments: Map<Symbol, T>): T = when (this) {
    is MST.Numeric -> (algebra as NumericAlgebra<T>?)?.number(value)
        ?: error("Numeric nodes are not supported by $algebra")

    is Symbol -> algebra.bindSymbolOrNull(this) ?: arguments.getValue(this)

    is MST.Unary -> when {
        algebra is NumericAlgebra && this.value is MST.Numeric -> algebra.unaryOperation(
            this.operation,
            algebra.number(this.value.value),
        )
        else -> algebra.unaryOperationFunction(this.operation)(this.value.interpret(algebra, arguments))
    }

    is MST.Binary -> when {
        algebra is NumericAlgebra && this.left is MST.Numeric && this.right is MST.Numeric -> algebra.binaryOperation(
            this.operation,
            algebra.number(this.left.value),
            algebra.number(this.right.value),
        )

        algebra is NumericAlgebra && this.left is MST.Numeric -> algebra.leftSideNumberOperation(
            this.operation,
            this.left.value,
            this.right.interpret(algebra, arguments),
        )

        algebra is NumericAlgebra && this.right is MST.Numeric -> algebra.rightSideNumberOperation(
            this.operation,
            left.interpret(algebra, arguments),
            right.value,
        )

        else -> algebra.binaryOperation(
            this.operation,
            this.left.interpret(algebra, arguments),
            this.right.interpret(algebra, arguments),
        )
    }
}

/**
 * Interprets the [MST] node with this [Algebra] and optional [arguments]
 *
 * @receiver the node to evaluate.
 * @param algebra the algebra that provides operations.
 * @return the value of expression.
 */
public fun <T> MST.interpret(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T = interpret(
    algebra,
    when (arguments.size) {
        0 -> emptyMap()
        1 -> mapOf(arguments[0])
        else -> hashMapOf(*arguments)
    },
)

/**
 * Interpret this [MST] as expression.
 */
public fun <T : Any> MST.toExpression(algebra: Algebra<T>): Expression<T> =
    Expression { arguments -> interpret(algebra, arguments) }
