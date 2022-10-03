/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.math.max
import kotlin.math.min


/**
 * Represents univariate polynomial that stores its coefficients in a [List].
 *
 * @param C the type of constants.
 */
public data class ListPolynomial<out C>(
    /**
     * List that contains coefficients of the polynomial.
     *
     * Every monomial \(a x^d\) is stored as a coefficient \(a\) placed
     * into the list at index \(d\). For example, coefficients of a polynomial \(5 x^2 - 6\) can be represented as
     * ```
     * listOf(
     *     -6, // -6 +
     *     0,  // 0 x +
     *     5,  // 5 x^2
     * )
     * ```
     * and also as
     * ```
     * listOf(
     *     -6, // -6 +
     *     0,  // 0 x +
     *     5,  // 5 x^2
     *     0,  // 0 x^3
     *     0,  // 0 x^4
     * )
     * ```
     * It is not prohibited to put extra zeros at end of the list (as for \(0x^3\) and \(0x^4\) in the example). But the
     * longer the coefficients list the worse performance of arithmetical operations performed on it. Thus, it is
     * recommended not to put (or even to remove) extra (or useless) coefficients at the end of the coefficients list.
     * @usesMathJax
     */
    public val coefficients: List<C>
) {
    override fun toString(): String = "ListPolynomial$coefficients"
}

/**
 * Arithmetic context for univariate polynomials with coefficients stored as a [List] constructed with the provided
 * [ring] of constants.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public open class ListPolynomialSpace<C, out A : Ring<C>>(
    public override val ring: A,
) : PolynomialSpaceOverRing<C, ListPolynomial<C>, A> {
    /**
     * Returns sum of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun ListPolynomial<C>.plus(other: Int): ListPolynomial<C> =
        if (other == 0) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = getOrElse(0) { constantZero } + other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    /**
     * Returns difference between the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun ListPolynomial<C>.minus(other: Int): ListPolynomial<C> =
        if (other == 0) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = getOrElse(0) { constantZero } - other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    /**
     * Returns product of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun ListPolynomial<C>.times(other: Int): ListPolynomial<C> =
        when (other) {
            0 -> zero
            1 -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }

    /**
     * Returns sum of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = this@plus + getOrElse(0) { constantZero }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    /**
     * Returns difference between the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients
                .toMutableList()
                .apply {
                    if (this@minus == 0) {
                        indices.forEach { this[it] = -this[it] }
                    } else {
                        (1..lastIndex).forEach { this[it] = -this[it] }

                        val result = this@minus - getOrElse(0) { constantZero }

                        if (size == 0) add(result)
                        else this[0] = result
                    }
                }
        )
    /**
     * Returns product of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: ListPolynomial<C>): ListPolynomial<C> =
        when (this) {
            0 -> zero
            1 -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    public override operator fun C.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@plus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) this@plus else this@plus + get(0)

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    public override operator fun C.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@minus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        (1 .. lastIndex).forEach { this[it] = -this[it] }

                        val result = if (size == 0) this@minus else this@minus - get(0)

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    public override operator fun C.times(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients.map { this@times * it }
        )

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    public override operator fun ListPolynomial<C>.plus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else get(0) + other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    public override operator fun ListPolynomial<C>.minus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(-other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else get(0) - other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    public override operator fun ListPolynomial<C>.times(other: C): ListPolynomial<C> =
        ListPolynomial(
            coefficients.map { it * other }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): ListPolynomial<C> = ListPolynomial(listOf(value))

    /**
     * Returns negation of the polynomial.
     */
    public override operator fun ListPolynomial<C>.unaryMinus(): ListPolynomial<C> =
        ListPolynomial(coefficients.map { -it })
    /**
     * Returns sum of the polynomials.
     */
    public override operator fun ListPolynomial<C>.plus(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(max(thisDegree, otherDegree) + 1) {
                when {
                    it > thisDegree -> other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> coefficients[it] + other.coefficients[it]
                }
            }
        )
    }
    /**
     * Returns difference of the polynomials.
     */
    public override operator fun ListPolynomial<C>.minus(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(max(thisDegree, otherDegree) + 1) {
                when {
                    it > thisDegree -> -other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> coefficients[it] - other.coefficients[it]
                }
            }
        )
    }
    /**
     * Returns product of the polynomials.
     */
    public override operator fun ListPolynomial<C>.times(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(thisDegree + otherDegree + 1) { d ->
                (max(0, d - otherDegree)..min(thisDegree, d))
                    .map { coefficients[it] * other.coefficients[d - it] }
                    .reduce { acc, rational -> acc + rational }
            }
        )
    }
    /**
     * Raises [arg] to the integer power [exponent].
     */ // TODO: To optimize boxing
    override fun power(arg: ListPolynomial<C>, exponent: UInt): ListPolynomial<C> = super.power(arg, exponent)

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: ListPolynomial<C> = ListPolynomial(emptyList())
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    override val one: ListPolynomial<C> by lazy { ListPolynomial(listOf(constantOne)) }

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public override val ListPolynomial<C>.degree: Int get() = coefficients.lastIndex

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Evaluates value of [this] polynomial on provided [argument].
     */
    public inline fun ListPolynomial<C>.substitute(argument: C): C = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] polynomial.
     */
    public inline fun ListPolynomial<C>.substitute(argument: ListPolynomial<C>): ListPolynomial<C> = substitute(ring, argument)

    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunction(): (C) -> C = asFunctionOver(ring)
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunctionOfConstant(): (C) -> C = asFunctionOfConstantOver(ring)
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListPolynomial<C> = asFunctionOfPolynomialOver(ring)

    /**
     * Evaluates value of [this] polynomial on provided [argument].
     */
    public inline operator fun ListPolynomial<C>.invoke(argument: C): C = substitute(ring, argument)
    /**
     * Evaluates value of [this] polynomial on provided [argument].
     */
    public inline operator fun ListPolynomial<C>.invoke(argument: ListPolynomial<C>): ListPolynomial<C> = substitute(ring, argument)
}

/**
 * Space of polynomials constructed over ring.
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param A type of underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class ScalableListPolynomialSpace<C, out A>(
    ring: A,
) : ListPolynomialSpace<C, A>(ring), ScaleOperations<ListPolynomial<C>> where A : Ring<C>, A : ScaleOperations<C> {
    override fun scale(a: ListPolynomial<C>, value: Double): ListPolynomial<C> =
        ring { ListPolynomial(a.coefficients.map { scale(it, value) }) }
}
