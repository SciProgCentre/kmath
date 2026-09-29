/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.attributes.SafeType
import space.kscience.attributes.WithType
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.NumericAlgebra

/**
 * MST form where all values belong to the type [T]. It is optimal for constant folding, dynamic compilation, etc.
 *
 * @param T the type.
 */
public sealed interface TypedMst<T> : WithType<T> {
    /**
     * A node containing a unary operation.
     *
     * @param T the type.
     * @property operation The identifier of operation.
     * @property function The function implementing this operation.
     * @property value The argument of this operation.
     */
    public class Unary<T>(
        public val operation: String,
        public val function: (T) -> T,
        public val value: TypedMst<T>,
    ) : TypedMst<T> {
        override val type: SafeType<T> get() = value.type

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

        init {
            require(left.type == right.type) { "Left and right expressions must be of the same type" }
        }

        override val type: SafeType<T> get() = left.type

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
    public class Constant<T>(
        override val type: SafeType<T>,
        public val value: T,
        public val number: Number?,
    ) : TypedMst<T> {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Constant<*>
            if (value != other.value) return false
            if (number != other.number) return false
            return true
        }

        override fun hashCode(): Int {
            var result = value.hashCode()
            result = 31 * result + number.hashCode()
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
    public class Variable<T>(override val type: SafeType<T>, public val symbol: Symbol) : TypedMst<T> {
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

    public class FunctionCall<T>(
        public val name: String,
        public val arguments: Map<Symbol, TypedMst<T>>,
    ) : TypedMst<T> {
        init {
            require(arguments.isNotEmpty()) { "Function call must have at least one argument" }
            require(arguments.values.map { it.type }.distinct().size == 1) {
                "Function call arguments must have the same type as the function"
            }
        }

        override val type: SafeType<T> get() = arguments.values.first().type
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as FunctionCall<*>

            if (name != other.name) return false
            if (arguments != other.arguments) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + arguments.hashCode()
            return result
        }


    }
}

/**
 * Interprets the [TypedMst] node
 */
context(mstContext: MSTInterpreterContext<T>)
public fun <T> TypedMst<T>.interpret(): T = when (this) {
    is TypedMst.Unary -> algebra.unaryOperation(operation, interpret())

    is TypedMst.Binary -> when (val algebra = algebra) {
        is NumericAlgebra if left is TypedMst.Constant && left.number != null ->
            algebra.leftSideNumberOperation(operation, left.number, right.interpret())

        is NumericAlgebra if right is TypedMst.Constant && right.number != null ->
            algebra.rightSideNumberOperation(operation, left.interpret(), right.number)

        else -> algebra.binaryOperation(
            operation,
            left.interpret(),
            right.interpret(),
        )
    }

    is TypedMst.Constant -> value
    is TypedMst.Variable -> mstContext.arguments.getValue(symbol)

    is TypedMst.FunctionCall<T> -> mstContext.callFunction(name, arguments.mapValues { it.value.interpret() })
}

public fun <T> TypedMst<T>.interpret(algebra: Algebra<T>, arguments: Map<Symbol, T>): T = context(
    MSTInterpreterContext(algebra, arguments)
) {
    interpret()
}

/**
 * Interprets the [TypedMst] node with this [Algebra] and optional [arguments].
 */
public fun <T> TypedMst<T>.interpret(algebra: Algebra<T>, vararg arguments: Pair<Symbol, T>): T = context(
    MSTInterpreterContext(algebra, arguments.toMap())
) {
    interpret()
}

/**
 * Interpret this [TypedMst] node as expression.
 */
public fun <T : Any> TypedMst<T>.toExpression(algebra: Algebra<T>): Expression<T> =
    Expression(algebra.type) { arguments ->
        interpret(algebra, arguments)
    }
