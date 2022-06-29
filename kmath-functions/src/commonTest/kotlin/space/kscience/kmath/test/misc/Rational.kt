/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.NumbersAddOps

class Rational {
    companion object {
        val ZERO: Rational = Rational(0L)
        val ONE: Rational = Rational(1L)
    }

    val numerator: Long
    val denominator: Long

    internal constructor(numerator: Long, denominator: Long, toCheckInput: Boolean = true) {
        if (toCheckInput) {
            if (denominator == 0L) throw ArithmeticException("/ by zero")

            val greatestCommonDivider = gcd(numerator, denominator).let { if (denominator < 0L) -it else it }

            this.numerator = numerator / greatestCommonDivider
            this.denominator = denominator / greatestCommonDivider
        } else {
            this.numerator = numerator
            this.denominator = denominator
        }
    }

    constructor(numerator: Int, denominator: Int) : this(numerator.toLong(), denominator.toLong(), true)
    constructor(numerator: Int, denominator: Long) : this(numerator.toLong(), denominator, true)
    constructor(numerator: Long, denominator: Int) : this(numerator, denominator.toLong(), true)
    constructor(numerator: Long, denominator: Long) : this(numerator, denominator, true)
    constructor(numerator: Int) : this(numerator.toLong(), 1L, false)
    constructor(numerator: Long) : this(numerator, 1L, false)

    operator fun unaryPlus(): Rational = this
    operator fun unaryMinus(): Rational = Rational(-this.numerator, this.denominator)
    operator fun plus(other: Rational): Rational =
        Rational(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    operator fun plus(other: Int): Rational =
        Rational(
            numerator + denominator * other.toLong(),
            denominator
        )
    operator fun plus(other: Long): Rational =
        Rational(
            numerator + denominator * other,
            denominator
        )
    operator fun minus(other: Rational): Rational =
        Rational(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    operator fun minus(other: Int): Rational =
        Rational(
            numerator - denominator * other.toLong(),
            denominator
        )
    operator fun minus(other: Long): Rational =
        Rational(
            numerator - denominator * other,
            denominator
        )
    operator fun times(other: Rational): Rational =
        Rational(
            numerator * other.numerator,
            denominator * other.denominator
        )
    operator fun times(other: Int): Rational =
        Rational(
            numerator * other.toLong(),
            denominator
        )
    operator fun times(other: Long): Rational =
        Rational(
            numerator * other,
            denominator
        )
    operator fun div(other: Rational): Rational =
        Rational(
            numerator * other.denominator,
            denominator * other.numerator
        )
    operator fun div(other: Int): Rational =
        Rational(
            numerator,
            denominator * other.toLong()
        )
    operator fun div(other: Long): Rational =
        Rational(
            numerator,
            denominator * other
        )
    override fun equals(other: Any?): Boolean =
        when (other) {
            is Rational -> numerator == other.numerator && denominator == other.denominator
            is Int -> numerator == other && denominator == 1L
            is Long -> numerator == other && denominator == 1L
            else -> false
        }

    override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

    override fun toString(): String = if (denominator == 1L) "$numerator" else "$numerator/$denominator"
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
@OptIn(UnstableKMathAPI::class)
object RationalField : Field<Rational>, NumbersAddOps<Rational> {
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