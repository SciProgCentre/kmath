/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra

/**
 * MST form where all values belong to the type [T]. It is optimal for constant folding, dynamic compilation, etc.
 *
 * @param T the type.
 */
public sealed interface TypedMst<T> {
    /**
     * A node containing a unary operation.
     *
     * @param T the type.
     * @property operation The identifier of operation.
     * @property function The function implementing this operation.
     * @property value The argument of this operation.
     */
    public class Unary<T>(public val operation: String, public val function: (T) -> T, public val value: TypedMst<T>) :
        TypedMst<T> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Unary<*>
            if (operation != other.operation) return false
            if (value != other.value) return false
            return true
        }

        override fun hashCode(): Int {
            var result = operation.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String = "Unary(operation=$operation, value=$value)"
    }

    /**
     * A node containing binary operation.
     *
     * @param T the type.
     * @property operation The identifier of operation.
     * @property function The binary function implementing this operation.
     * @property left The left operand.
     * @property right The right operand.
     */
    public class Binary<T>(
        public val operation: String,
        public val function: Function<T>,
        public val left: TypedMst<T>,
        public val right: TypedMst<T>,
    ) : TypedMst<T> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Binary<*>

            if (operation != other.operation) return false
            if (left != other.left) return false
            if (right != other.right) return false

            return true
        }

        override fun hashCode(): Int {
            var result = operation.hashCode()
            result = 31 * result + left.hashCode()
            result = 31 * result + right.hashCode()
            return result
        }

        override fun toString(): String = "Binary(operation=$operation, left=$left, right=$right)"
    }

    /**
     * The non-numeric constant value.
     *
     * @param T the type.
     * @property value The held value.
     * @property number The number this value corresponds.
     */
    public class Constant<T>(public val value: T, public val number: Number?) : TypedMst<T> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Constant<*>
            if (value != other.value) return false
            if (number != other.number) return false
            return true
        }

        override fun hashCode(): Int {
            var result = value?.hashCode() ?: 0
            result = 31 * result + (number?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String = "Constant(value=$value, number=$number)"
    }

    /**
     * The node containing a variable
     *
     * @param T the type.
     * @property symbol The symbol of the variable.
     */
    public class Variable<T>(public val symbol: Symbol) : TypedMst<T> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Variable<*>
            if (symbol != other.symbol) return false
            return true
        }

        override fun hashCode(): Int = symbol.hashCode()
        override fun toString(): String = "Variable(symbol=$symbol)"
    }
}

/**
 * Interprets the [TypedMst] node with this [Algebra] and [arguments].
 */
public fun <T> TypedMst<T>.interpret(algebra: Algebra<T>, arguments: Map<Symbol, T>): T = when (this) {
    is TypedMst.Unary -> algebra.unaryOperation(operation, interpret(algebra, arguments))

    is TypedMst.Binary -> when {
        algebra is NumericAlgebra && left is TypedMst.Constant && left.number != null ->
            algebra.leftSideNumberOperation(operation, left.number, right.interpret(algebra, arguments))

        algebra is NumericAlgebra && right is TypedMst.Constant && right.number != null ->
            algebra.rightSideNumberOperation(operation, left.interpret(algebra, arguments), right.number)

        else -> algebra.binaryOperation(
            operation,
            left.interpret(algebra, arguments),
            right.interpret(algebra, arguments),
        )
    }

    is TypedMst.Constant -> value
    is TypedMst.Variable -> arguments.getValue(symbol)
}

/**
 * Interprets the [TypedMst] node with this [Algebra] and optional [arguments].
 */
public fun <T> TypedMst<T>.interpret(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T = interpret(
    algebra,
    when (arguments.size) {
        0 -> emptyMap()
        1 -> mapOf(arguments[0])
        else -> hashMapOf(*arguments)
    },
)

/**
 * Interpret this [TypedMst] node as expression.
 */
public fun <T : Any> TypedMst<T>.toExpression(algebra: Algebra<T>): Expression<T> = Expression { arguments ->
    interpret(algebra, arguments)
}
