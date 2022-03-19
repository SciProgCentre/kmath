/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.BigInt
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.NumbersAddOps
import space.kscience.kmath.operations.toBigInt
import space.kscience.kmath.operations.BigInt.Companion.ZERO as I0
import space.kscience.kmath.operations.BigInt.Companion.ONE as I1

/**
 * The class represents rational numbers.
 *
 * Instances contain [numerator] and [denominator] represented as [Long].
 *
 * Also [numerator] and [denominator] are coprime and [denominator] is positive.
 *
 * @author [Gleb Minaev](https://github.com/lounres)
 */
public class Rational: Comparable<Rational> {
    public companion object {
        /**
         * Constant containing the zero (the additive identity) of the [Rational] field.
         */
        public val ZERO: Rational = Rational(I0)
        /**
         * Constant containing the one (the multiplicative identity) of the [Rational] field.
         */
        public val ONE: Rational = Rational(I1)
    }

    /**
     * Numerator of the fraction. It's stored as non-negative coprime with [denominator] integer.
     */
    public val numerator: BigInt
    /**
     * Denominator of the fraction. It's stored as non-zero coprime with [numerator] integer.
     */
    public val denominator: BigInt

    /**
     * If [toCheckInput] is `true` before assigning values to [Rational.numerator] and [Rational.denominator] makes them coprime and makes
     * denominator positive. Otherwise, just assigns the values.
     *
     * @throws ArithmeticException If denominator is zero.
     */
    internal constructor(numerator: BigInt, denominator: BigInt, toCheckInput: Boolean = true) {
        if (toCheckInput) {
            if (denominator == I0) throw ArithmeticException("/ by zero")

            val greatestCommonDivider = gcd(numerator, denominator).let { if (denominator < I0) -it else it }

            this.numerator = numerator / greatestCommonDivider
            this.denominator = denominator / greatestCommonDivider
        } else {
            this.numerator = numerator
            this.denominator = denominator
        }
    }

    /**
     * Before assigning values to [Rational.numerator] and [Rational.denominator] makes them coprime and makes
     * denominator positive.
     *
     * @throws ArithmeticException If denominator is zero.
     */
    public constructor(numerator: BigInt, denominator: BigInt) : this(numerator, denominator, true)
    public constructor(numerator: Int, denominator: BigInt) : this(numerator.toBigInt(), denominator, true)
    public constructor(numerator: Long, denominator: BigInt) : this(numerator.toBigInt(), denominator, true)
    public constructor(numerator: BigInt, denominator: Int) : this(numerator, denominator.toBigInt(), true)
    public constructor(numerator: BigInt, denominator: Long) : this(numerator, denominator.toBigInt(), true)
    public constructor(numerator: Int, denominator: Int) : this(numerator.toBigInt(), denominator.toBigInt(), true)
    public constructor(numerator: Int, denominator: Long) : this(numerator.toBigInt(), denominator.toBigInt(), true)
    public constructor(numerator: Long, denominator: Int) : this(numerator.toBigInt(), denominator.toBigInt(), true)
    public constructor(numerator: Long, denominator: Long) : this(numerator.toBigInt(), denominator.toBigInt(), true)
    public constructor(numerator: BigInt) : this(numerator, I1, false)
    public constructor(numerator: Int) : this(numerator.toBigInt(), I1, false)
    public constructor(numerator: Long) : this(numerator.toBigInt(), I1, false)

    /**
     * Returns the same instant.
     */
    public operator fun unaryPlus(): Rational = this

    /**
     * Returns negation of the instant of [Rational] field.
     */
    public operator fun unaryMinus(): Rational = Rational(-this.numerator, this.denominator)

    /**
     * Returns sum of the instants of [Rational] field.
     */
    public operator fun plus(other: Rational): Rational =
        Rational(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )

    /**
     * Returns sum of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun plus(other: BigInt): Rational =
        Rational(
            numerator + denominator * other,
            denominator
        )

    /**
     * Returns sum of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun plus(other: Int): Rational =
        Rational(
            numerator + denominator * other.toBigInt(),
            denominator
        )

    /**
     * Returns sum of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun plus(other: Long): Rational =
        Rational(
            numerator + denominator * other.toBigInt(),
            denominator
        )

    /**
     * Returns difference of the instants of [Rational] field.
     */
    public operator fun minus(other: Rational): Rational =
        Rational(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )

    /**
     * Returns difference of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun minus(other: BigInt): Rational =
        Rational(
            numerator - denominator * other,
            denominator
        )

    /**
     * Returns difference of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun minus(other: Int): Rational =
        Rational(
            numerator - denominator * other.toBigInt(),
            denominator
        )

    /**
     * Returns difference of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun minus(other: Long): Rational =
        Rational(
            numerator - denominator * other.toBigInt(),
            denominator
        )

    /**
     * Returns product of the instants of [Rational] field.
     */
    public operator fun times(other: Rational): Rational =
        Rational(
            numerator * other.numerator,
            denominator * other.denominator
        )

    /**
     * Returns product of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun times(other: BigInt): Rational =
        Rational(
            numerator * other,
            denominator
        )

    /**
     * Returns product of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun times(other: Int): Rational =
        Rational(
            numerator * other.toBigInt(),
            denominator
        )

    /**
     * Returns product of the instants of [Rational] field. [other] is represented as [Rational].
     */
    public operator fun times(other: Long): Rational =
        Rational(
            numerator * other.toBigInt(),
            denominator
        )

    /**
     * Returns quotient of the instants of [Rational] field.
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun div(other: Rational): Rational =
        Rational(
            numerator * other.denominator,
            denominator * other.numerator
        )

    /**
     * Returns quotient of the instants of [Rational] field. [other] is represented as [Rational].
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun div(other: BigInt): Rational =
        Rational(
            numerator,
            denominator * other
        )

    /**
     * Returns quotient of the instants of [Rational] field. [other] is represented as [Rational].
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun div(other: Int): Rational =
        Rational(
            numerator,
            denominator * other.toBigInt()
        )

    /**
     * Returns quotient of the instants of [Rational] field. [other] is represented as [Rational].
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun div(other: Long): Rational =
        Rational(
            numerator,
            denominator * other.toBigInt()
        )

    /**
     * Returns reminder from integral division.
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun rem(other: Rational): Rational =
        Rational(
            (numerator * other.denominator) % (denominator * other.numerator),
            denominator * other.denominator
        )

    /**
     * Returns reminder from integral division.
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun rem(other: BigInt): Rational =
        Rational(
            numerator % denominator * other,
            denominator * other
        )

    /**
     * Returns reminder from integral division.
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun rem(other: Int): Rational =
        Rational(
            numerator % denominator * other.toBigInt(),
            denominator * other.toBigInt()
        )

    /**
     * Returns reminder from integral division.
     *
     * @throws ArithmeticException if [other] is the zero of the field it can't be a divisor.
     */
    public operator fun rem(other: Long): Rational =
        Rational(
            numerator % denominator * other.toBigInt(),
            denominator * other.toBigInt()
        )

    /**
     * Checks equality of the instance to [other].
     *
     * [BigInt], [Int] and [Long] values are also checked as Rational ones.
     */
    override fun equals(other: Any?): Boolean =
        when (other) {
            is Rational -> numerator == other.numerator && denominator == other.denominator
            is BigInt -> numerator == other && denominator == I1
            is Int -> numerator == other && denominator == I1
            is Long -> numerator == other && denominator == I1
            else -> false
        }

    /**
     * Compares the instance to [other] as [Comparable.compareTo].
     *
     * @see Comparable.compareTo
     */
    override operator fun compareTo(other: Rational): Int = (numerator * other.denominator).compareTo(other.numerator * denominator)

    /**
     * Compares the instance to [other] as [Comparable.compareTo].
     *
     * [Integer] values are also checked as Rational ones.
     *
     * @see Comparable.compareTo
     */
    public operator fun compareTo(other: BigInt): Int = (numerator).compareTo(denominator * other)

    /**
     * Compares the instance to [other] as [Comparable.compareTo].
     *
     * [Int] values are also checked as Rational ones.
     *
     * @see Comparable.compareTo
     */
    public operator fun compareTo(other: Int): Int = (numerator).compareTo(denominator * other.toBigInt())

    /**
     * Compares the instance to [other] as [Comparable.compareTo].
     *
     * [Long] values are also checked as Rational ones.
     *
     * @see Comparable.compareTo
     */
    public operator fun compareTo(other: Long): Int = (numerator).compareTo(denominator * other.toBigInt())

    public override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

//    /** Creates a range from this value to the specified [other] value. */
//    operator fun rangeTo(other: JBInt) = ClosedRationalRange(this, other.toRational())
//    /** Creates a range from this value to the specified [other] value. */
//    operator fun rangeTo(other: Rational) = ClosedRationalRange(this, other)
//    /** Creates a range from this value to the specified [other] value. */
//    operator fun rangeTo(other: Int) = ClosedRationalRange(this, other.toRational())
//    /** Creates a range from this value to the specified [other] value. */
//    operator fun rangeTo(other: Long) = ClosedRationalRange(this, other.toRational())

    public fun toRational(): Rational = this

    public fun toBigInt(): BigInt = numerator / denominator

//    public fun toInt(): Int = (numerator / denominator).toInt()
//
//    public fun toLong(): Long = (numerator / denominator).toLong()
//
//    public fun toDouble(): Double = (numerator.toDouble() / denominator.toDouble())
//
//    public fun toFloat(): Float = (numerator.toFloat() / denominator.toFloat())

    public override fun toString(): String = if (denominator == I1) "$numerator" else "$numerator/$denominator"
}


/**
 * Algebraic structure for rational numbers.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
@OptIn(UnstableKMathAPI::class)
public object RationalField : Field<Rational>, NumbersAddOps<Rational> {
    override inline val zero: Rational get() = Rational.ZERO
    override inline val one: Rational get() = Rational.ONE

    override inline fun number(value: Number): Rational = Rational(value.toLong())

    override inline fun add(left: Rational, right: Rational): Rational = left + right
    override inline fun multiply(left: Rational, right: Rational): Rational = left * right
    override inline fun divide(left: Rational, right: Rational): Rational = left / right
    override inline fun scale(a: Rational, value: Double): Rational = a * number(value)

    override inline fun Rational.unaryMinus(): Rational = -this
    override inline fun Rational.plus(arg: Rational): Rational = this + arg
    override inline fun Rational.minus(arg: Rational): Rational = this - arg
    override inline fun Rational.times(arg: Rational): Rational = this * arg
    override inline fun Rational.div(arg: Rational): Rational = this / arg
}