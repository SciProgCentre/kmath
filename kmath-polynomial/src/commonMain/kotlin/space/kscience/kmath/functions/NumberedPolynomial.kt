/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import kotlin.jvm.JvmName
import kotlin.math.max


/**
 * Represents multivariate polynomial that stores its coefficients in a [Map] and terms' signatures in a [List].
 *
 * @param C the type of constants.
 */
public data class NumberedPolynomial<out C>
@PublishedApi
internal constructor(
    /**
     * Map that contains coefficients of the polynomial.
     *
     * Every monomial \(a x_1^{d_1} ... x_n^{d_n}\) is stored as a pair "key-value" in the map, where the value is the
     * coefficient \(a\) and the key is a list that associates index of every variable in the monomial with their degree
     * in the monomial. For example, coefficients of a polynomial \(5 x_1^2 x_3^3 - 6 x_2\) can be represented as
     * ```
     * mapOf(
     *      listOf(2, 0, 3) to 5, // 5 x_1^2 x_3^3 +
     *      listOf(0, 1) to (-6), // (-6) x_2^1
     * )
     * ```
     * and also as
     * ```
     * mapOf(
     *      listOf(2, 0, 3) to 5, // 5 x_1^2 x_3^3 +
     *      listOf(0, 1) to (-6), // (-6) x_2^1
     *      listOf(0, 1, 1) to 0, // 0 x_2^1 x_3^1
     * )
     * ```
     * It is not prohibited to put extra zero monomials into the map (as for \(0 x_2 x_3\) in the example). But the
     * bigger the coefficients map the worse performance of arithmetical operations performed on it. Thus, it is
     * recommended not to put (or even to remove) extra (or useless) monomials in the coefficients map.
     * @usesMathJax
     */
    public val coefficients: Map<List<UInt>, C>
) {
    override fun toString(): String = "NumberedPolynomial$coefficients"
}

/**
 * Arithmetic context for multivariate polynomials with coefficients stored as a [Map] and terms' signatures stored as a
 * [List] constructed with the provided [ring] of constants.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class NumberedPolynomialSpace<C, out A : Ring<C>>(
    public override val ring: A,
) : PolynomialSpaceOverRing<C, NumberedPolynomial<C>, A> {
    /**
     * Returns sum of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun NumberedPolynomial<C>.plus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), other.asConstant()) { it -> it + other }
        )
    /**
     * Returns difference between the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun NumberedPolynomial<C>.minus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), (-other).asConstant()) { it -> it - other }
        )
    /**
     * Returns product of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun NumberedPolynomial<C>.times(other: Int): NumberedPolynomial<C> =
        when (other) {
            0 -> zero
            1 -> this
            else -> NumberedPolynomialAsIs(
                coefficients.mapValues { it.value * other }
            )
        }

    /**
     * Returns sum of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) other
        else NumberedPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyList(), this@plus.asConstant()) { it -> this@plus + it }
        )
    /**
     * Returns difference between the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> this.asPolynomial()
            else -> NumberedPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyList(), other.coefficients.computeOnOrElse(emptyList(), { this@minus.asConstant() }, { it -> this@minus - it}))
                    other.coefficients.copyMapToBy(this, { _, c -> -c }) { currentC, _ -> currentC }
                }
            )
        }
    /**
     * Returns product of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when (this) {
            0 -> zero
            1 -> other
            else -> NumberedPolynomialAsIs(
                other.coefficients.mapValues { this@times * it.value }
            )
        }

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.asPolynomial()
        else NumberedPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyList(), this@plus) { it -> this@plus + it }
        )
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.asPolynomial()
        else NumberedPolynomialAsIs(
            buildMap(other.coefficients.size) {
                put(emptyList(), other.coefficients.computeOnOrElse(emptyList(), this@minus) { it -> this@minus - it })
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, _ -> currentC })
            }
        )
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    override operator fun C.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            other.coefficients.mapValues { this@times * it.value }
        )

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.plus(other: C): NumberedPolynomial<C> =
        if (coefficients.isEmpty()) other.asPolynomial()
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), other) { it -> it + other }
        )
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.minus(other: C): NumberedPolynomial<C> =
        if (coefficients.isEmpty()) other.asPolynomial()
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), -other) { it -> it - other }
        )
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.times(other: C): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            coefficients.mapValues { it.value * other }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to value))

    /**
     * Returns negation of the polynomial.
     */
    override fun NumberedPolynomial<C>.unaryMinus(): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            coefficients.mapValues { -it.value }
        )
    /**
     * Returns sum of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            mergeBy(coefficients, other.coefficients) { c1, c2 -> c1 + c2 }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
            }
        )
    /**
     * Returns product of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs =
                        (0..max(degs1.lastIndex, degs2.lastIndex))
                            .map { degs1.getOrElse(it) { 0U } + degs2.getOrElse(it) { 0U } }
                    val c = c1 * c2
                    putOrChange(degs, c) { it -> it + c }
                }
            }
        )
    /**
     * Raises [arg] to the integer power [exponent].
     */ // TODO: To optimize boxing
    override fun power(arg: NumberedPolynomial<C>, exponent: UInt): NumberedPolynomial<C> = super.power(arg, exponent)

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: NumberedPolynomial<C> = NumberedPolynomialAsIs(emptyMap())
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    override val one: NumberedPolynomial<C> by lazy { NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to constantOne)) }

    /**
     * Maximal index (ID) of variable occurring in the polynomial with positive power. If there is no such variable,
     * the result is -1.
     */
    public val NumberedPolynomial<C>.lastVariable: Int
        get() = coefficients.keys.maxOfOrNull { degs -> degs.lastIndex } ?: -1
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    override val NumberedPolynomial<C>.degree: Int
        get() = coefficients.keys.maxOfOrNull { degs -> degs.sum().toInt() } ?: -1
    /**
     * List that associates indices of variables (that appear in the polynomial in positive exponents) with their most
     * exponents in which the variables are appeared in the polynomial.
     *
     * As consequence all values in the list are non-negative integers. Also, if the polynomial is constant, the list is empty.
     * And last index of the list is [lastVariable].
     */
    public val NumberedPolynomial<C>.degrees: List<UInt>
        get() =
            MutableList(lastVariable + 1) { 0u }.apply {
                coefficients.keys.forEach { degs ->
                    degs.forEachIndexed { index, deg ->
                        this[index] = max(this[index], deg)
                    }
                }
            }
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public fun NumberedPolynomial<C>.degreeBy(variable: Int): UInt =
        coefficients.keys.maxOfOrNull { degs -> degs.getOrElse(variable) { 0u } } ?: 0u
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public fun NumberedPolynomial<C>.degreeBy(variables: Collection<Int>): UInt =
        coefficients.keys.maxOfOrNull { degs ->
            degs.withIndex().fold(0u) { acc, (index, value) -> if (index in variables) acc + value else acc }
        } ?: 0u
    /**
     * Count of variables occurring in the polynomial with positive power. If there is no such variable,
     * the result is 0.
     */
    public val NumberedPolynomial<C>.countOfVariables: Int
        get() =
            MutableList(lastVariable + 1) { false }.apply {
                coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
            }.count { it }

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    public inline fun NumberedPolynomial<C>.substitute(arguments: Map<Int, C>): NumberedPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(arguments: Map<Int, NumberedPolynomial<C>>) : NumberedPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    public inline fun NumberedPolynomial<C>.substitute(arguments: Buffer<C>): NumberedPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(arguments: Buffer<NumberedPolynomial<C>>) : NumberedPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided arguments [arguments] into [this] polynomial.
     */
    public inline fun NumberedPolynomial<C>.substituteFully(arguments: Buffer<C>): C = this.substituteFully(ring, arguments)

    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun NumberedPolynomial<C>.asFunction(): (Buffer<C>) -> C = asFunctionOver(ring)
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun NumberedPolynomial<C>.asFunctionOfConstant(): (Buffer<C>) -> C = asFunctionOfConstantOver(ring)
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun NumberedPolynomial<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedPolynomial<C> = asFunctionOfPolynomialOver(ring)

    /**
     * Evaluates value of [this] polynomial on provided [arguments].
     */
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<C>): C = substituteFully(ring, arguments)
    /**
     * Substitutes provided [arguments] into [this] polynomial.
     */
    @JvmName("invokePolynomial")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, arguments)
}