/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Ring
import kotlin.jvm.JvmName
import kotlin.math.max


/**
 * Represents multivariate polynomial that stores its coefficients in a [Map] and terms' signatures in a [Map] that
 * associates variables (of type [Symbol]) with their degree.
 *
 * @param C the type of constants.
 */
public data class LabeledPolynomial<C>
@PublishedApi
internal constructor(
    /**
     * Map that contains coefficients of the polynomial.
     *
     * Every monomial `a x_1^{d_1} ... x_n^{d_n}` is stored as a pair "key-value" in the map, where the value is the
     * coefficient `a` and the key is a map that associates variables in the monomial with their degree in the monomial.
     * For example, coefficients of a polynomial `5 a^2 c^3 - 6 b` can be represented as
     * ```
     * mapOf(
     *      mapOf(
     *          a to 2,
     *          c to 3
     *      ) to 5,
     *      mapOf(
     *          b to 1
     *      ) to (-6)
     * )
     * ```
     * and also as
     * ```
     * mapOf(
     *      mapOf(
     *          a to 2,
     *          c to 3
     *      ) to 5,
     *      mapOf(
     *          b to 1
     *      ) to (-6),
     *      mapOf(
     *          b to 1,
     *          c to 1
     *      ) to 0
     * )
     * ```
     * where `a`, `b` and `c` are corresponding [Symbol] objects.
     */
    public val coefficients: Map<Map<Symbol, UInt>, C>
) : Polynomial<C> {
    override fun toString(): String = "LabeledPolynomial$coefficients"
}

/**
 * Arithmetic context for multivariate polynomials with coefficients stored as a [Map] and terms' signatures stored as a
 * [Map] constructed with the provided [ring] of constants.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class LabeledPolynomialSpace<C, A : Ring<C>>(
    public override val ring: A,
) : MultivariatePolynomialSpace<C, Symbol, LabeledPolynomial<C>>, PolynomialSpaceOverRing<C, LabeledPolynomial<C>, A> {
    /**
     * Returns sum of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    public override operator fun Symbol.plus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomialAsIs(mapOf(
            mapOf(this@plus to 1U) to constantOne,
        ))
        else LabeledPolynomialAsIs(mapOf(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to constantOne * other,
        ))
    /**
     * Returns difference between the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    public override operator fun Symbol.minus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomialAsIs(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
        ))
        else LabeledPolynomialAsIs(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to constantOne * other,
        ))
    /**
     * Returns product of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    public override operator fun Symbol.times(other: Int): LabeledPolynomial<C> =
        if (other == 0) zero
        else LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U) to constantOne * other,
        ))

    /**
     * Returns sum of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun Int.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to constantOne,
        ))
        else LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to constantOne * this@plus,
        ))
    /**
     * Returns difference between the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun Int.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to -constantOne,
        ))
        else LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to constantOne * this@minus,
        ))
    /**
     * Returns product of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun Int.times(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) zero
        else LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to constantOne * this@times,
        ))

    /**
     * Returns sum of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun LabeledPolynomial<C>.plus(other: Int): LabeledPolynomial<C> =
        if (other == 0) this
        else with(coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to other.asConstant()))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = getOrElse(degs) { constantZero } + other
                    }
            )
        }
    /**
     * Returns difference between the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun LabeledPolynomial<C>.minus(other: Int): LabeledPolynomial<C> =
        if (other == 0) this
        else with(coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to (-other).asConstant()))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = getOrElse(degs) { constantZero } - other
                    }
            )
        }
    /**
     * Returns product of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun LabeledPolynomial<C>.times(other: Int): LabeledPolynomial<C> =
        if (other == 0) zero
        else LabeledPolynomial(
            coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this[degs]!! * other
                }
        )

    /**
     * Returns sum of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this == 0) other
        else with(other.coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this@plus.asConstant()))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = this@plus + getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns difference between the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this == 0) other
        else with(other.coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this@minus.asConstant()))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        forEach { (key, value) -> if (key.isNotEmpty()) this[key] = -value }

                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = this@minus - getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns product of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this == 0) zero
        else LabeledPolynomial(
            other.coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this@times * this[degs]!!
                }
        )

    /**
     * Converts the integer [value] to polynomial.
     */
    public override fun number(value: Int): LabeledPolynomial<C> = number(constantNumber(value))

    /**
     * Returns sum of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    public override operator fun Symbol.plus(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to other,
        ))
    /**
     * Returns difference between the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    public override operator fun Symbol.minus(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to other,
        ))
    /**
     * Returns product of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    public override operator fun Symbol.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(this@times to 1U) to other,
        ))

    /**
     * Returns sum of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun C.plus(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to this@plus,
        ))
    /**
     * Returns difference between the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun C.minus(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to this@minus,
        ))
    /**
     * Returns product of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun C.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(other to 1U) to this@times,
        ))

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this@plus))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = this@plus + getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this@minus))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = this@minus - getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this@times * this[degs]!!
                }
        )

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    override operator fun LabeledPolynomial<C>.plus(other: C): LabeledPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to other))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = getOrElse(degs) { constantZero } + other
                    }
            )
        }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    override operator fun LabeledPolynomial<C>.minus(other: C): LabeledPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to other))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyMap<Symbol, UInt>()

                        this[degs] = getOrElse(degs) { constantZero } - other
                    }
            )
        }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    override operator fun LabeledPolynomial<C>.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this[degs]!! * other
                }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): LabeledPolynomial<C> =
        LabeledPolynomial(mapOf(emptyMap<Symbol, UInt>() to value))

    /**
     * Represents the variable as a monic monomial.
     */
    public override operator fun Symbol.unaryPlus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U) to constantOne,
        ))
    /**
     * Returns negation of representation of the variable as a monic monomial.
     */
    public override operator fun Symbol.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U) to -constantOne,
        ))
    /**
     * Returns sum of the variables represented as monic monomials.
     */
    public override operator fun Symbol.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U) to constantOne * 2
        ))
        else LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to constantOne,
        ))
    /**
     * Returns difference between the variables represented as monic monomials.
     */
    public override operator fun Symbol.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) zero
        else LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to -constantOne,
        ))
    /**
     * Returns product of the variables represented as monic monomials.
     */
    public override operator fun Symbol.times(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(mapOf(
            mapOf(this to 2U) to constantOne
        ))
        else LabeledPolynomialAsIs(mapOf(
            mapOf(this to 1U, other to 1U) to constantOne,
        ))

    /**
     * Returns sum of the variable represented as a monic monomial and the polynomial.
     */
    public override operator fun Symbol.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(mapOf(this@plus to 1u) to constantOne))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = mapOf(this@plus to 1U)

                        this[degs] = constantOne + getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns difference between the variable represented as a monic monomial and the polynomial.
     */
    public override operator fun Symbol.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(mapOf(this@minus to 1u) to constantOne))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = mapOf(this@minus to 1U)

                        forEach { (degs, c) -> if(degs != degs) this[degs] = -c }

                        this[degs] = constantOne - getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns product of the variable represented as a monic monomial and the polynomial.
     */
    public override operator fun Symbol.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients
                .mapKeys { (degs, _) -> degs.toMutableMap().also{ it[this] = if (this in it) it[this]!! + 1U else 1U } }
        )

    /**
     * Returns sum of the polynomial and the variable represented as a monic monomial.
     */
    public override operator fun LabeledPolynomial<C>.plus(other: Symbol): LabeledPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(mapOf(other to 1u) to constantOne))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = mapOf(other to 1U)

                        this[degs] = constantOne + getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns difference between the polynomial and the variable represented as a monic monomial.
     */
    public override operator fun LabeledPolynomial<C>.minus(other: Symbol): LabeledPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) LabeledPolynomialAsIs(mapOf(mapOf(other to 1u) to constantOne))
            else LabeledPolynomialAsIs(
                toMutableMap()
                    .apply {
                        val degs = mapOf(other to 1U)

                        this[degs] = constantOne - getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns product of the polynomial and the variable represented as a monic monomial.
     */
    public override operator fun LabeledPolynomial<C>.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients
                .mapKeys { (degs, _) -> degs.toMutableMap().also{ it[other] = if (other in it) it[other]!! + 1U else 1U } }
        )

    /**
     * Returns negation of the polynomial.
     */
    override fun LabeledPolynomial<C>.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { -it.value }
        )
    /**
     * Returns sum of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.mapValuesTo(this) { it.value }
                other.coefficients.mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! + value else value }
            }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.mapValuesTo(this) { it.value }
                other.coefficients.mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! - value else -value }
            }
        )
    /**
     * Returns product of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs = degs1.toMutableMap()
                    degs2.mapValuesTo(degs) { (variable, deg) -> degs.getOrElse(variable) { 0u } + deg }
                    val c = c1 * c2
                    this[degs] = if (degs in this) this[degs]!! + c else c
                }
            }
        )

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: LabeledPolynomial<C> = LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to constantZero))
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    override val one: LabeledPolynomial<C> = LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to constantOne))

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    override val LabeledPolynomial<C>.degree: Int
        get() = coefficients.entries.maxOfOrNull { (degs, _) -> degs.values.sum().toInt() } ?: -1
    /**
     * Map that associates variables (that appear in the polynomial in positive exponents) with their most exponents
     * in which they are appeared in the polynomial.
     *
     * As consequence all values in the map are positive integers. Also, if the polynomial is constant, the map is empty.
     * And keys of the map is the same as in [variables].
     */
    public override val LabeledPolynomial<C>.degrees: Map<Symbol, UInt>
        get() =
            buildMap {
                coefficients.entries.forEach { (degs, _) ->
                    degs.mapValuesTo(this) { (variable, deg) ->
                        max(getOrElse(variable) { 0u }, deg)
                    }
                }
            }
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public override fun LabeledPolynomial<C>.degreeBy(variable: Symbol): UInt =
        coefficients.entries.maxOfOrNull { (degs, _) -> degs.getOrElse(variable) { 0u } } ?: 0u
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public override fun LabeledPolynomial<C>.degreeBy(variables: Collection<Symbol>): UInt =
        coefficients.entries.maxOfOrNull { (degs, _) -> degs.filterKeys { it in variables }.values.sum() } ?: 0u
    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public override val LabeledPolynomial<C>.variables: Set<Symbol>
        get() =
            buildSet {
                coefficients.entries.forEach { (degs, _) -> addAll(degs.keys) }
            }
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public override val LabeledPolynomial<C>.countOfVariables: Int get() = variables.size

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, C>): LabeledPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    @Suppress("NOTHING_TO_INLINE")
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> = substitute(ring, arguments)
}