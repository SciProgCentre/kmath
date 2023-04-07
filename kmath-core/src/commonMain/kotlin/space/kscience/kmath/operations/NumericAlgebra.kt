/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import kotlin.math.E
import kotlin.math.PI

/**
 * An algebraic structure where elements can have numeric representation.
 *
 * @param T the type of element of this structure.
 */
public interface NumericAlgebra<T> : Algebra<T> {
    /**
     * Wraps a number to [T] object.
     *
     * @param value the number to wrap.
     * @return an object.
     */
    public fun number(value: Number): T

    /**
     * Dynamically dispatches a binary operation with the certain name with numeric first argument.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [leftSideNumberOperation]: for any `a`, `b`, and `c`,
     * `leftSideNumberOperationFunction(a)(b, c) == leftSideNumberOperation(a, b)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun leftSideNumberOperationFunction(operation: String): (left: Number, right: T) -> T =
        { l, r -> binaryOperationFunction(operation)(number(l), r) }

    /**
     * Dynamically invokes a binary operation with the certain name with numeric first argument.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [leftSideNumberOperation]: for any `a`, `b`, and `c`,
     * `leftSideNumberOperationFunction(a)(b, c) == leftSideNumberOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @param left the first argument of operation.
     * @param right the second argument of operation.
     * @return a result of operation.
     */
    public fun leftSideNumberOperation(operation: String, left: Number, right: T): T =
        leftSideNumberOperationFunction(operation)(left, right)

    /**
     * Dynamically dispatches a binary operation with the certain name with numeric first argument.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [rightSideNumberOperation]: for any `a`, `b`, and `c`,
     * `rightSideNumberOperationFunction(a)(b, c) == leftSideNumberOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @return an operation.
     */
    public fun rightSideNumberOperationFunction(operation: String): (left: T, right: Number) -> T =
        { l, r -> binaryOperationFunction(operation)(l, number(r)) }

    /**
     * Dynamically invokes a binary operation with the certain name with numeric second argument.
     *
     * Implementations must fulfil the following requirements:
     *
     * 1. If operation is not defined in the structure, then the function throws [kotlin.IllegalStateException].
     * 1. Equivalence to [rightSideNumberOperationFunction]: for any `a`, `b`, and `c`,
     * `rightSideNumberOperationFunction(a)(b, c) == rightSideNumberOperation(a, b, c)`.
     *
     * @param operation the name of operation.
     * @param left the first argument of operation.
     * @param right the second argument of operation.
     * @return a result of operation.
     */
    public fun rightSideNumberOperation(operation: String, left: T, right: Number): T =
        rightSideNumberOperationFunction(operation)(left, right)

    override fun bindSymbolOrNull(value: String): T? = when (value) {
        "pi" -> number(PI)
        "e" -> number(E)
        else -> super.bindSymbolOrNull(value)
    }
}

/**
 * The &pi; mathematical constant.
 */
public val <T> NumericAlgebra<T>.pi: T get() = bindSymbolOrNull("pi") ?: number(PI)

/**
 * The *e* mathematical constant.
 */
public val <T> NumericAlgebra<T>.e: T get() = number(E)

/**
 * Scale by scalar operations
 */
public interface ScaleOperations<T> : Algebra<T> {
    /**
     * Scaling an element by a scalar.
     *
     * @param a the multiplier.
     * @param value the multiplicand.
     * @return the produce.
     */
    public fun scale(a: T, value: Double): T

    /**
     * Multiplication of this element by a scalar.
     *
     * @receiver the multiplier.
     * @param k the multiplicand.
     * @return the product.
     */
    public operator fun T.times(k: Number): T = scale(this, k.toDouble())

    /**
     * Division of this element by scalar.
     *
     * @receiver the dividend.
     * @param k the divisor.
     * @return the quotient.
     */
    public operator fun T.div(k: Number): T = scale(this, 1.0 / k.toDouble())

    /**
     * Multiplication of this number by element.
     *
     * @receiver the multiplier.
     * @param arg the multiplicand.
     * @return the product.
     */
    public operator fun Number.times(arg: T): T = arg * this
}

/**
 * A combination of [NumericAlgebra] and [Ring] that adds intrinsic simple operations on numbers like `T+1`
 * TODO to be removed and replaced by extensions after multiple receivers are there
 */
@UnstableKMathAPI
public interface NumbersAddOps<T> : RingOps<T>, NumericAlgebra<T> {
    /**
     * Addition of element and scalar.
     *
     * @receiver the augend.
     * @param other the addend.
     */
    public operator fun T.plus(other: Number): T = this + number(other)

    /**
     * Addition of scalar and element.
     *
     * @receiver the augend.
     * @param other the addend.
     */
    public operator fun Number.plus(other: T): T = other + this

    /**
     * Subtraction of element from number.
     *
     * @receiver the minuend.
     * @param other the subtrahend.
     * @receiver the difference.
     */
    public operator fun T.minus(other: Number): T = this - number(other)

    /**
     * Subtraction of number from element.
     *
     * @receiver the minuend.
     * @param other the subtrahend.
     * @receiver the difference.
     */
    public operator fun Number.minus(other: T): T = -other + this
}