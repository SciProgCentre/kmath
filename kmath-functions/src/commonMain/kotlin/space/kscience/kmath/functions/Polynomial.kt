/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")

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
public data class Polynomial<out C>(
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
     *
     * @usesMathJax
     */
    public val coefficients: List<C>
) {
    override fun toString(): String = "Polynomial$coefficients"
}

/**
 * Arithmetic context for univariate polynomials with coefficients stored as a [List] constructed with the provided
 * [ring] of constants.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public open class PolynomialSpace<C, A>(
    /**
     * Underlying ring of constants. Its operations on constants are used by local operations on constants and polynomials.
     */
    public val ring: A,
) : Ring<Polynomial<C>>, ScaleOperations<Polynomial<C>> where A : Ring<C>, A : ScaleOperations<C> {

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public val constantZero: C get() = ring.zero
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public val constantOne: C get() = ring.one

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    public operator fun C.plus(other: Polynomial<C>): Polynomial<C> =
        with(ring) {
            with(other.coefficients) {
                if (isEmpty()) Polynomial(listOf(this@plus))
                else Polynomial(
                    toMutableList()
                        .apply {
                            val result = if (size == 0) this@plus else this@plus + get(0)

                            if (size == 0) add(result)
                            else this[0] = result
                        }
                )
            }
        }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    public operator fun C.minus(other: Polynomial<C>): Polynomial<C> =
        with(ring) {
            with(other.coefficients) {
                if (isEmpty()) Polynomial(listOf(this@minus))
                else Polynomial(
                    toMutableList()
                        .apply {
                            (1..lastIndex).forEach { this[it] = -this[it] }

                            val result = if (size == 0) this@minus else this@minus - get(0)

                            if (size == 0) add(result)
                            else this[0] = result
                        }
                )
            }
        }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    public operator fun C.times(other: Polynomial<C>): Polynomial<C> =
        with(ring) {
            Polynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        for (deg in indices) this[deg] = this@times * this[deg]
                    }
            )
        }

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    public operator fun Polynomial<C>.plus(other: C): Polynomial<C> =
        with(ring) {
            with(coefficients) {
                if (isEmpty()) Polynomial(listOf(other))
                else Polynomial(
                    toMutableList()
                        .apply {
                            val result = if (size == 0) other else get(0) + other

                            if (size == 0) add(result)
                            else this[0] = result
                        }
                )
            }
        }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    public operator fun Polynomial<C>.minus(other: C): Polynomial<C> =
        with(ring) {
            with(coefficients) {
                if (isEmpty()) Polynomial(listOf(-other))
                else Polynomial(
                    toMutableList()
                        .apply {
                            val result = if (size == 0) other else get(0) - other

                            if (size == 0) add(result)
                            else this[0] = result
                        }
                )
            }
        }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    public operator fun Polynomial<C>.times(other: C): Polynomial<C> =
        with(ring) {
            Polynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        for (deg in indices) this[deg] = this[deg] * other
                    }
            )
        }

    /**
     * Converts the constant [value] to polynomial.
     */
    public fun number(value: C): Polynomial<C> = Polynomial(listOf(value))
    /**
     * Converts the constant to polynomial.
     */
    public fun C.asPolynomial(): Polynomial<C> = number(this)

    /**
     * Returns negation of the polynomial.
     */
    public override operator fun Polynomial<C>.unaryMinus(): Polynomial<C> = ring {
        Polynomial(coefficients.map { -it })
    }
    /**
     * Returns sum of the polynomials.
     */
    public override operator fun Polynomial<C>.plus(other: Polynomial<C>): Polynomial<C> = ring {
        val thisDegree = degree
        val otherDegree = other.degree
        return Polynomial(
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
    public override operator fun Polynomial<C>.minus(other: Polynomial<C>): Polynomial<C> = ring {
        val thisDegree = degree
        val otherDegree = other.degree
        return Polynomial(
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
    public override operator fun Polynomial<C>.times(other: Polynomial<C>): Polynomial<C> = ring {
        val thisDegree = degree
        val otherDegree = other.degree
        return Polynomial(
            List(thisDegree + otherDegree + 1) { d ->
                (max(0, d - otherDegree)..min(thisDegree, d))
                    .map { coefficients[it] * other.coefficients[d - it] }
                    .reduce { acc, rational -> acc + rational }
            }
        )
    }

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: Polynomial<C> = Polynomial(emptyList())
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    override val one: Polynomial<C> by lazy { Polynomial(listOf(constantOne)) }

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public val Polynomial<C>.degree: Int get() = coefficients.lastIndex

    override fun add(left: Polynomial<C>, right: Polynomial<C>): Polynomial<C> = left + right
    override fun multiply(left: Polynomial<C>, right: Polynomial<C>): Polynomial<C> = left * right
    override fun scale(a: Polynomial<C>, value: Double): Polynomial<C> =
        ring { Polynomial(a.coefficients.map { scale(it, value) }) }

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Evaluates value of [this] polynomial on provided [argument].
     */
    public inline fun Polynomial<C>.substitute(argument: C): C = value(ring, argument)

    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun Polynomial<C>.asFunction(): (C) -> C = asFunctionOver(ring)

    /**
     * Evaluates value of [this] polynomial on provided [argument].
     */
    public inline operator fun Polynomial<C>.invoke(argument: C): C = value(ring, argument)
}
