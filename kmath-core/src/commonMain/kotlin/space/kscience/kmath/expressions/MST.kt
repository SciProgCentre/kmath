/*
 * Copyright 2018-2026 KMath contributors.
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

    /**
     * A node containing a function call with named arguments.
     */
    public data class FunctionCall(val name: String, val arguments: Map<Symbol, MST>) : MST
}


/**
 * A context for MST interpretation.
 *
 * @param algebra the algebra used for interpretation.
 * @param arguments the map of arguments.
 * @param constants the map of constants.
 * @param functions the map of functions.
 */
public data class MSTInterpreterContext<T>(
    public val algebra: Algebra<T>,
    public val arguments: Map<Symbol, T>,
    public val constants: Map<Symbol, T> = emptyMap(),
    public val functions: Map<String, Expression<T>> = emptyMap(),
)

/**
 * Call function in this [MSTInterpreterContext]
 */
public fun <T> MSTInterpreterContext<T>.callFunction(name: String, arguments: Map<Symbol, T>): T {
    //first try using library function
    functions[name]?.invoke(arguments)?.let { return it }

    //TODO add aliases for functions from  algebra
    error("Function with name ${name} is not defined in $this")
}

/**
 * Bridge method to extract algebra from MST interpretation context
 */
context(mstInterpreterContext: MSTInterpreterContext<T>)
public val <T> algebra: Algebra<T> get() = mstInterpreterContext.algebra

/**
 * Interprets the [MST] node using given [MSTInterpreterContext]
 */
context(mstContext: MSTInterpreterContext<T>)
public fun <T> MST.interpret(): T = when (this) {
    is MST.Numeric -> (algebra as NumericAlgebra<T>?)?.number(value)
        ?: error("Numeric nodes are not supported by $algebra")

    is Symbol -> mstContext.constants[this]
        ?: mstContext.arguments[this]
        ?: algebra.bindSymbolOrNull(this)
        ?: error("Symbol $this is not defined in $mstContext")

    is MST.Unary -> when(val algebra = mstContext.algebra) {
        is NumericAlgebra if this.value is MST.Numeric -> algebra.unaryOperation(
            this.operation,
            algebra.number(this.value.value),
        )

        else -> algebra.unaryOperationFunction(this.operation)(this.value.interpret())
    }

    is MST.Binary -> when (val algebra = algebra) {
        is NumericAlgebra if this.left is MST.Numeric && this.right is MST.Numeric -> algebra.binaryOperation(
            this.operation,
            algebra.number(this.left.value),
            algebra.number(this.right.value),
        )

        is NumericAlgebra if this.left is MST.Numeric -> algebra.leftSideNumberOperation(
            this.operation,
            this.left.value,
            this.right.interpret(),
        )

        is NumericAlgebra if this.right is MST.Numeric -> algebra.rightSideNumberOperation(
            this.operation,
            left.interpret(),
            right.value,
        )

        else -> algebra.binaryOperation(
            this.operation,
            this.left.interpret(),
            this.right.interpret(),
        )
    }

    is MST.FunctionCall -> mstContext.callFunction(name, arguments.mapValues { it.value.interpret() })
}

/**
 * Interprets the [MST] node with this [Algebra] and  [arguments]
 */
public fun <T> MST.interpret(algebra: Algebra<T>, arguments: Map<Symbol, T>): T =
    context(MSTInterpreterContext(algebra, arguments)) {
        interpret()
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
    Expression(algebra.type) { arguments -> interpret(algebra, arguments) }
