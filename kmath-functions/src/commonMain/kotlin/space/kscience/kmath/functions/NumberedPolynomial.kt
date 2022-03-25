/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.math.max


/**
 * Polynomial model without fixation on specific context they are applied to.
 *
 * @param C the type of constants.
 */
public data class NumberedPolynomial<C>
internal constructor(
    /**
     * Map that collects coefficients of the polynomial. Every monomial `a x_1^{d_1} ... x_n^{d_n}` is represented as
     * pair "key-value" in the map, where value is coefficients `a` and
     * key is list that associates index of every variable in the monomial with multiplicity of the variable occurring
     * in the monomial. For example coefficients of polynomial `5 x_1^2 x_3^3 - 6 x_2` can be represented as
     * ```
     * mapOf(
     *      listOf(2, 0, 3) to 5,
     *      listOf(0, 1) to (-6),
     * )
     * ```
     * and also as
     * ```
     * mapOf(
     *      listOf(2, 0, 3) to 5,
     *      listOf(0, 1) to (-6),
     *      listOf(0, 1, 1) to 0,
     * )
     * ```
     * It is recommended not to put zero monomials into the map, but is not prohibited. Lists of degrees always do not
     * contain any zeros on end, but can contain zeros on start or anywhere in middle.
     */
    public val coefficients: Map<List<UInt>, C>
) : Polynomial<C> {
    override fun toString(): String = "NumberedPolynomial$coefficients"
}

/**
 * Space of polynomials.
 *
 * @param C the type of operated polynomials.
 * @param A the intersection of [Ring] of [C] and [ScaleOperations] of [C].
 * @param ring the [A] instance.
 */
public open class NumberedPolynomialSpace<C, A : Ring<C>>(
    public final override val ring: A,
) : PolynomialSpaceOverRing<C, NumberedPolynomial<C>, A> {
    /**
     * Returns sum of the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun NumberedPolynomial<C>.plus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else
            NumberedPolynomial(
                coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = getOrElse(degs) { constantZero } + other
                    }
            )
    /**
     * Returns difference between the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun NumberedPolynomial<C>.minus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else
            NumberedPolynomial(
                coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = getOrElse(degs) { constantZero } - other
                    }
            )
    /**
     * Returns product of the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun NumberedPolynomial<C>.times(other: Int): NumberedPolynomial<C> =
        if (other == 0) zero
        else NumberedPolynomial<C>(
            coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this[degs]!! * other
                }
        )

    /**
     * Returns sum of the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) other
        else
            NumberedPolynomial(
                other.coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = this@plus + getOrElse(degs) { constantZero }
                    }
            )
    /**
     * Returns difference between the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) other
        else
            NumberedPolynomial(
                other.coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = this@minus - getOrElse(degs) { constantZero }
                    }
            )
    /**
     * Returns product of the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) zero
        else NumberedPolynomial(
            other.coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this@times * this[degs]!!
                }
        )

    /**
     * Converts the integer [value] to polynomial.
     */
    public override fun number(value: Int): NumberedPolynomial<C> = number(constantNumber(value))

    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    override operator fun C.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this@plus))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = this@plus + getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    override operator fun C.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this@minus))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyList<UInt>()

                        this[degs] = this@minus - getOrElse(degs) { constantZero }
                    }
            )
        }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    override operator fun C.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            other.coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this@times * this[degs]!!
                }
        )

    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.plus(other: C): NumberedPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to other))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = getOrElse(degs) { constantZero } + other
                    }
            )
        }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.minus(other: C): NumberedPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to other))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        this[degs] = getOrElse(degs) { constantZero } - other
                    }
            )
        }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.times(other: C): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            coefficients
                .toMutableMap()
                .apply {
                    for (degs in keys) this[degs] = this[degs]!! * other
                }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): NumberedPolynomial<C> =
        NumberedPolynomial(mapOf(emptyList<UInt>() to value))

    /**
     * Returns negation of the polynomial.
     */
    override fun NumberedPolynomial<C>.unaryMinus(): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            coefficients.mapValues { -it.value }
        )
    /**
     * Returns sum of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            buildMap(coefficients.size + other.coefficients.size) {
                other.coefficients.mapValuesTo(this) { it.value }
                other.coefficients.mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! + value else value }
            }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            buildMap(coefficients.size + other.coefficients.size) {
                other.coefficients.mapValuesTo(this) { it.value }
                other.coefficients.mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! - value else -value }
            }
        )
    /**
     * Returns product of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs =
                        (0..max(degs1.lastIndex, degs2.lastIndex))
                            .map { degs1.getOrElse(it) { 0U } + degs2.getOrElse(it) { 0U } }
                    val c = c1 * c2
                    this[degs] = if (degs in this) this[degs]!! + c else c
                }
            }
        )

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: NumberedPolynomial<C> = NumberedPolynomial<C>(emptyMap())
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    override val one: NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            mapOf(
                emptyList<UInt>() to constantOne // 1 * x_1^0 * x_2^0 * ...
            )
        )

    /**
     * Maximal index (ID) of variable occurring in the polynomial with positive power. If there is no such variable,
     * the result is `-1`.
     */
    public val NumberedPolynomial<C>.lastVariable: Int
        get() = coefficients.entries.maxOfOrNull { (degs, _) -> degs.lastIndex } ?: -1
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    override val NumberedPolynomial<C>.degree: Int
        get() = coefficients.entries.maxOfOrNull { (degs, _) -> degs.sum().toInt() } ?: -1
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
                coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        this[index] = max(this[index], deg)
                    }
                }
            }
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public fun NumberedPolynomial<C>.degreeBy(variable: Int): UInt =
        coefficients.entries.maxOfOrNull { (degs, _) -> degs.getOrElse(variable) { 0u } } ?: 0u
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public fun NumberedPolynomial<C>.degreeBy(variables: Collection<Int>): UInt =
        coefficients.entries.maxOfOrNull { (degs, _) ->
            degs.withIndex().filter { (index, _) -> index in variables }.sumOf { it.value }
        } ?: 0u
    /**
     * Count of variables occurring in the polynomial with positive power. If there is no such variable,
     * the result is `0`.
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

    @Suppress("NOTHING_TO_INLINE")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, C>): NumberedPolynomial<C> = this.substitute(ring, argument)
    @Suppress("NOTHING_TO_INLINE")
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedPolynomial<C> = this.substitute(ring, argument)

    @Suppress("NOTHING_TO_INLINE")
    public inline fun NumberedPolynomial<C>.asFunction(): (Map<Int, C>) -> NumberedPolynomial<C> = { this.substitute(ring, it) }
    @Suppress("NOTHING_TO_INLINE")
    public inline fun NumberedPolynomial<C>.asFunctionOnConstants(): (Map<Int, C>) -> NumberedPolynomial<C> = { this.substitute(ring, it) }
    @Suppress("NOTHING_TO_INLINE")
    public inline fun NumberedPolynomial<C>.asFunctionOnPolynomials(): (Map<Int, NumberedPolynomial<C>>) -> NumberedPolynomial<C> = { this.substitute(ring, it) }

    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun NumberedPolynomial<C>.invoke(argument: Map<Int, C>): NumberedPolynomial<C> = this.substitute(ring, argument)
    @Suppress("NOTHING_TO_INLINE")
    @JvmName("invokePolynomial")
    public inline operator fun NumberedPolynomial<C>.invoke(argument: Map<Int, NumberedPolynomial<C>>): NumberedPolynomial<C> = this.substitute(ring, argument)

    // FIXME: Move to other constructors with context receiver
    public fun C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this))
}