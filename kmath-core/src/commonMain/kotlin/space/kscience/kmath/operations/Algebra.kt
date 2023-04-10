/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Ring.Companion.optimizedPower
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * Represents an algebraic structure.
 *
 * @param T the type of element of this structure.
 */
public interface Algebra<T> {

    /**
     * Provide a factory for buffers, associated with this [Algebra]
     */
    public val bufferFactory: MutableBufferFactory<T> get() = MutableBufferFactory.boxing()

    /**
     * Wraps a raw string to [T] object. This method is designed for three purposes:
     *
     * 1. Mathematical constants (`e`, `pi`).
     * 1. Variables for expression-like contexts (`a`, `b`, `c`&hellip;).
     * 1. Literals (`{1, 2}`, (`(3; 4)`)).
     *
     * If algebra can't parse the string, then this method must throw [kotlin.IllegalStateException].
     *
     * @param value the raw string.
     * @return an object or `null` if symbol could not be bound to the context.
     */
    public fun bindSymbolOrNull(value: String): T? = null

    /**
     * The same as [bindSymbolOrNull] but throws an error if symbol could not be bound
     */
    public fun bindSymbol(value: String): T =
        bindSymbolOrNull(value) ?: error("Symbol '$value' is not supported in $this")

    /**
     * Dynamically dispatches a unary operation with the certain name.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [unaryOperation]: for any `a` and `b`, `unaryOperationFunction(a)(b) == unaryOperation(a, b)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun unaryOperationFunction(operation: String): (arg: T) -> T =
        error("Unary operation $operation not defined in $this")

    /**
     * Dynamically invokes a unary operation with the certain name.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [unaryOperationFunction]: i.e., for any `a` and `b`,
     * `unaryOperationFunction(a)(b) == unaryOperation(a, b)`.
     *
     * @param operation the name of operation.
     * @param arg the argument of operation.
     * @return a result of operation.
     */
    public fun unaryOperation(operation: String, arg: T): T = unaryOperationFunction(operation)(arg)

    /**
     * Dynamically dispatches a binary operation with the certain name.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [binaryOperation]: for any `a`, `b`, and `c`,
     * `binaryOperationFunction(a)(b, c) == binaryOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun binaryOperationFunction(operation: String): (left: T, right: T) -> T =
        error("Binary operation '$operation' not defined in $this")

    /**
     * Dynamically invokes a binary operation with the certain name.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [binaryOperationFunction]: for any `a`, `b`, and `c`,
     * `binaryOperationFunction(a)(b, c) == binaryOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @param left the first argument of operation.
     * @param right the second argument of operation.
     * @return a result of operation.
     */
    public fun binaryOperation(operation: String, left: T, right: T): T =
        binaryOperationFunction(operation)(left, right)

    /**
     * Export an algebra element, so it could be accessed even after algebra scope is closed.
     * This method must be used on algebras where data is stored externally or any local algebra state is used.
     * By default (if not overridden), exports the object itself.
     */
    @UnstableKMathAPI
    public fun export(arg: T): T = arg
}

public fun <T> Algebra<T>.bindSymbolOrNull(symbol: Symbol): T? = bindSymbolOrNull(symbol.identity)

public fun <T> Algebra<T>.bindSymbol(symbol: Symbol): T = bindSymbol(symbol.identity)

/**
 * Call a block with an [Algebra] as receiver.
 */
// TODO add contract when KT-32313 is fixed
public inline operator fun <A : Algebra<*>, R> A.invoke(block: A.() -> R): R = run(block)

/**
 * Represents group without neutral element (also known as inverse semigroup) i.e., algebraic structure with
 * associative, binary operation [add].
 *
 * @param T the type of element of this semispace.
 */
public interface GroupOps<T> : Algebra<T> {
    /**
     * Addition of two elements.
     *
     * @param left the augend.
     * @param right the addend.
     * @return the sum.
     */
    public fun add(left: T, right: T): T

    // Operations to be performed in this context. Could be moved to extensions in case of KEEP-176.

    /**
     * The negation of this element.
     *
     * @receiver this value.
     * @return the additive inverse of this value.
     */
    public operator fun T.unaryMinus(): T

    /**
     * Returns this value.
     *
     * @receiver this value.
     * @return this value.
     */
    public operator fun T.unaryPlus(): T = this

    /**
     * Addition of two elements.
     *
     * @receiver the augend.
     * @param arg the addend.
     * @return the sum.
     */
    public operator fun T.plus(arg: T): T = add(this, arg)

    /**
     * Subtraction of two elements.
     *
     * @receiver the minuend.
     * @param arg the subtrahend.
     * @return the difference.
     */
    public operator fun T.minus(arg: T): T = add(this, -arg)

    // Dynamic dispatch of operations
    override fun unaryOperationFunction(operation: String): (arg: T) -> T = when (operation) {
        PLUS_OPERATION -> { arg -> +arg }
        MINUS_OPERATION -> { arg -> -arg }
        else -> super.unaryOperationFunction(operation)
    }

    override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = when (operation) {
        PLUS_OPERATION -> ::add
        MINUS_OPERATION -> { left, right -> left - right }
        else -> super.binaryOperationFunction(operation)
    }

    public companion object {
        /**
         * The identifier of addition and unary positive operator.
         */
        public const val PLUS_OPERATION: String = "+"

        /**
         * The identifier of subtraction and unary negative operator.
         */
        public const val MINUS_OPERATION: String = "-"
    }
}

/**
 * Represents group i.e., algebraic structure with associative, binary operation [add].
 *
 * @param T the type of element of this semispace.
 */
public interface Group<T> : GroupOps<T> {
    /**
     * The neutral element of addition.
     */
    public val zero: T
}

/**
 * Represents ring without multiplicative and additive identities i.e., algebraic structure with
 * associative, binary, commutative operation [add] and associative, operation [multiply] distributive over [add].
 *
 * @param T the type of element of this semiring.
 */
public interface RingOps<T> : GroupOps<T> {
    /**
     * Multiplies two elements.
     *
     * @param left the multiplier.
     * @param right the multiplicand.
     */
    public fun multiply(left: T, right: T): T

    /**
     * Multiplies this element by scalar.
     *
     * @receiver the multiplier.
     * @param arg the multiplicand.
     */
    public operator fun T.times(arg: T): T = multiply(this, arg)

    override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = when (operation) {
        TIMES_OPERATION -> ::multiply
        else -> super.binaryOperationFunction(operation)
    }

    public companion object {
        /**
         * The identifier of multiplication.
         */
        public const val TIMES_OPERATION: String = "*"
    }
}

/**
 * Represents ring i.e., algebraic structure with two associative binary operations called "addition" and
 * "multiplication" and their neutral elements.
 *
 * @param T the type of element of this ring.
 */
public interface Ring<T> : Group<T>, RingOps<T> {
    /**
     * The neutral element of multiplication
     */
    public val one: T

    /**
     * Raises [arg] to the integer power [pow].
     */
    public fun power(arg: T, pow: UInt): T = optimizedPower(arg, pow)

    public companion object{
        /**
         * Raises [arg] to the non-negative integer power [exponent].
         *
         * Special case: 0 ^ 0 is 1.
         *
         * @receiver the algebra to provide multiplication.
         * @param arg the base.
         * @param exponent the exponent.
         * @return the base raised to the power.
         * @author Evgeniy Zhelenskiy
         */
        internal fun <T> Ring<T>.optimizedPower(arg: T, exponent: UInt): T = when {
            arg == zero && exponent > 0U -> zero
            arg == one -> arg
            arg == -one -> powWithoutOptimization(arg, exponent % 2U)
            else -> powWithoutOptimization(arg, exponent)
        }

        private fun <T> Ring<T>.powWithoutOptimization(base: T, exponent: UInt): T = when (exponent) {
            0U -> one
            1U -> base
            else -> {
                val pre = powWithoutOptimization(base, exponent shr 1).let { it * it }
                if (exponent and 1U == 0U) pre else pre * base
            }
        }
    }
}

/**
 * Represents field without multiplicative and additive identities i.e., algebraic structure with associative, binary,
 * commutative operations [add] and [multiply]; binary operation [divide] as multiplication of left operand by
 * reciprocal of right one.
 *
 * @param T the type of element of this semifield.
 */
public interface FieldOps<T> : RingOps<T> {
    /**
     * Division of two elements.
     *
     * @param left the dividend.
     * @param right the divisor.
     * @return the quotient.
     */
    public fun divide(left: T, right: T): T

    /**
     * Division of two elements.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.div(arg: T): T = divide(this, arg)

    override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = when (operation) {
        DIV_OPERATION -> ::divide
        else -> super.binaryOperationFunction(operation)
    }

    public companion object {
        /**
         * The identifier of division.
         */
        public const val DIV_OPERATION: String = "/"
    }
}

/**
 * Represents field i.e., algebraic structure with three operations: associative, commutative addition and
 * multiplication, and division. **This interface differs from the eponymous mathematical definition: fields in KMath
 * also support associative multiplication by scalar.**
 *
 * @param T the type of element of this field.
 */
public interface Field<T> : Ring<T>, FieldOps<T>, ScaleOperations<T>, NumericAlgebra<T> {
    override fun number(value: Number): T = scale(one, value.toDouble())

    public fun power(arg: T, pow: Int): T = optimizedPower(arg, pow)

    public companion object{
        /**
         * Raises [arg] to the integer power [exponent].
         *
         * Special case: 0 ^ 0 is 1.
         *
         * @receiver the algebra to provide multiplication and division.
         * @param arg the base.
         * @param exponent the exponent.
         * @return the base raised to the power.
         * @author Iaroslav Postovalov, Evgeniy Zhelenskiy
         */
        private fun <T> Field<T>.optimizedPower(arg: T, exponent: Int): T = when {
            exponent < 0 -> one / (this as Ring<T>).optimizedPower(arg, if (exponent == Int.MIN_VALUE) Int.MAX_VALUE.toUInt().inc() else (-exponent).toUInt())
            else -> (this as Ring<T>).optimizedPower(arg, exponent.toUInt())
        }
    }
}
