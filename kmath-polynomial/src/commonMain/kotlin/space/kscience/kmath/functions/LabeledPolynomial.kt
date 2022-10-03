/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

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
public data class LabeledPolynomial<out C>
@PublishedApi
internal constructor(
    /**
     * Map that contains coefficients of the polynomial.
     *
     * Every monomial \(a x_1^{d_1} ... x_n^{d_n}\) is stored as a pair "key-value" in the map, where the value is the
     * coefficient \(a\) and the key is a map that associates variables in the monomial with their degree in the monomial.
     * For example, coefficients of a polynomial \(5 a^2 c^3 - 6 b\) can be represented as
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
     * where \(a\), \(b\) and \(c\) are corresponding [Symbol] objects.
     *
     * It is not prohibited to put extra zero monomials into the map (as for \(0 b c\) in the example). But the
     * bigger the coefficients map the worse performance of arithmetical operations performed on it. Thus, it is
     * recommended not to put (or even to remove) extra (or useless) monomials in the coefficients map.
     * @usesMathJax
     */
    public val coefficients: Map<Map<Symbol, UInt>, C>
) {
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
public class LabeledPolynomialSpace<C, out A : Ring<C>>(
    public override val ring: A,
) : MultivariatePolynomialSpace<C, Symbol, LabeledPolynomial<C>>, PolynomialSpaceOverRing<C, LabeledPolynomial<C>, A> {
    /**
     * Returns sum of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    public override operator fun Symbol.plus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to other.asConstant(),
        )
    /**
     * Returns difference between the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    public override operator fun Symbol.minus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to (-other).asConstant(),
        )
    /**
     * Returns product of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    public override operator fun Symbol.times(other: Int): LabeledPolynomial<C> =
        if (other == 0) zero
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to other.asConstant(),
        )

    /**
     * Returns sum of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun Int.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to this@plus.asConstant(),
        )
    /**
     * Returns difference between the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun Int.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomialAsIs(
            mapOf(other to 1U) to -constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to constantOne * this@minus,
        )
    /**
     * Returns product of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun Int.times(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) zero
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to this@times.asConstant(),
        )

    /**
     * Returns sum of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun LabeledPolynomial<C>.plus(other: Int): LabeledPolynomial<C> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.asPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), other.asConstant()) { it -> it + other }
            )
        }
    /**
     * Returns difference between the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun LabeledPolynomial<C>.minus(other: Int): LabeledPolynomial<C> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.asPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), (-other).asConstant()) { it -> it - other }
            )
        }
    /**
     * Returns product of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun LabeledPolynomial<C>.times(other: Int): LabeledPolynomial<C> =
        when(other) {
            0 -> zero
            1 -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValues { (_, value) -> value * other }
            )
        }

    /**
     * Returns sum of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0 -> other
            other.coefficients.isEmpty() -> this@plus.asPolynomial()
            else -> LabeledPolynomialAsIs(
                other.coefficients.withPutOrChanged(emptyMap(), this@plus.asConstant()) { it -> this@plus + it }
            )
        }
    /**
     * Returns difference between the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> this@minus.asPolynomial()
            else -> LabeledPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyMap(), asConstant())
                    other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
                }
            )
        }
    /**
     * Returns product of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when(this) {
            0 -> zero
            1 -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValues { (_, value) -> this@times * value }
            )
        }

    /**
     * Returns sum of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    public override operator fun Symbol.plus(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to other,
        )
    /**
     * Returns difference between the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    public override operator fun Symbol.minus(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to -other,
        )
    /**
     * Returns product of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    public override operator fun Symbol.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this@times to 1U) to other,
        )

    /**
     * Returns sum of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun C.plus(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to this@plus,
        )
    /**
     * Returns difference between the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun C.minus(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to this@minus,
        )
    /**
     * Returns product of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    public override operator fun C.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to this@times,
        )

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyMap(), this@plus) { it -> this@plus + it }
        )
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.asPolynomial()
        else LabeledPolynomialAsIs(
            buildMap(other.coefficients.size + 1) {
                put(emptyMap(), this@minus)
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
            }
        )
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients.mapValues { this@times * it.value }
        )

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    override operator fun LabeledPolynomial<C>.plus(other: C): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(emptyMap(), other) { it -> it + other }
        )
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    override operator fun LabeledPolynomial<C>.minus(other: C): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(emptyMap(), -other) { it -> it - other }
        )
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    override operator fun LabeledPolynomial<C>.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { it.value * other }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): LabeledPolynomial<C> = value.asLabeledPolynomial()

    /**
     * Represents the variable as a monic monomial.
     */
    public override operator fun Symbol.unaryPlus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
        )
    /**
     * Returns negation of representation of the variable as a monic monomial.
     */
    public override operator fun Symbol.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this to 1U) to -constantOne,
        )
    /**
     * Returns sum of the variables represented as monic monomials.
     */
    public override operator fun Symbol.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne * 2
        )
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to constantOne,
        )
    /**
     * Returns difference between the variables represented as monic monomials.
     */
    public override operator fun Symbol.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) zero
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to -constantOne,
        )
    /**
     * Returns product of the variables represented as monic monomials.
     */
    public override operator fun Symbol.times(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(
            mapOf(this to 2U) to constantOne
        )
        else LabeledPolynomialAsIs(
            mapOf(this to 1U, other to 1U) to constantOne,
        )

    /**
     * Returns sum of the variable represented as a monic monomial and the polynomial.
     */
    public override operator fun Symbol.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.asPolynomial()
        else LabeledPolynomialAsIs(
            other.coefficients.withPutOrChanged(mapOf(this@plus to 1U), constantOne) { it -> constantOne + it }
        )
    /**
     * Returns difference between the variable represented as a monic monomial and the polynomial.
     */
    public override operator fun Symbol.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.asPolynomial()
        else LabeledPolynomialAsIs(
            buildMap(other.coefficients.size + 1) {
                put(mapOf(this@minus to 1U), constantOne)
                other.coefficients.copyMapToBy(this, { _, c -> -c }) { currentC, newC -> currentC - newC }
            }
        )
    /**
     * Returns product of the variable represented as a monic monomial and the polynomial.
     */
    public override operator fun Symbol.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients
                .mapKeys { (degs, _) -> degs.withPutOrChanged(this, 1u) { it -> it + 1u } }
        )

    /**
     * Returns sum of the polynomial and the variable represented as a monic monomial.
     */
    public override operator fun LabeledPolynomial<C>.plus(other: Symbol): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(mapOf(other to 1U), constantOne) { it -> it + constantOne }
        )
    /**
     * Returns difference between the polynomial and the variable represented as a monic monomial.
     */
    public override operator fun LabeledPolynomial<C>.minus(other: Symbol): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(mapOf(other to 1U), -constantOne) { it -> it - constantOne }
        )
    /**
     * Returns product of the polynomial and the variable represented as a monic monomial.
     */
    public override operator fun LabeledPolynomial<C>.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients
                .mapKeys { (degs, _) -> degs.withPutOrChanged(other, 1u) { it -> it + 1u } }
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
            mergeBy(coefficients, other.coefficients) { c1, c2 -> c1 + c2 }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
            }
        )
    /**
     * Returns product of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs = mergeBy(degs1, degs2) { deg1, deg2 -> deg1 + deg2 }
                    val c = c1 * c2
                    this.putOrChange(degs, c) { it -> it + c }
                }
            }
        )

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: LabeledPolynomial<C> = LabeledPolynomialAsIs()
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    override val one: LabeledPolynomial<C> = constantOne.asLabeledPolynomial()

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
                coefficients.keys.forEach { degs ->
                    degs.copyToBy(this, ::max)
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
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, C>): LabeledPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> = substitute(ring, arguments)
}