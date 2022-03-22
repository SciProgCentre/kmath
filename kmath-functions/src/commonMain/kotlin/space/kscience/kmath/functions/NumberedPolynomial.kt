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
 * Returns the same degrees' description of the monomial, but without extra zero degrees on the end.
 */
internal fun List<UInt>.cleanUp() = subList(0, indexOfLast { it != 0U } + 1)

// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(coefs, toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> {
    if (!toCheckInput) return NumberedPolynomial<C>(coefs)

    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in coefs) {
        val key = entry.key.cleanUp()
        val value = entry.value
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return NumberedPolynomial<C>(
        fixedCoefs.filterValues { it != zero }
    )
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(pairs, toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean = true) : NumberedPolynomial<C> {
    if (!toCheckInput) return NumberedPolynomial<C>(pairs.toMap())

    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return NumberedPolynomial<C>(
        fixedCoefs.filterValues { it != zero }
    )
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(pairs = pairs, toCheckInput = toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> {
    if (!toCheckInput) return NumberedPolynomial<C>(pairs.toMap())

    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return NumberedPolynomial<C>(
        fixedCoefs.filterValues { it != zero }
    )
}

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs, toCheckInput = true)

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs, toCheckInput = true)

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs, toCheckInput = true)

//context(A)
//public fun <C, A: Ring<C>> Symbol.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(mapOf(this to 1u) to one))
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> Symbol.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(mapOf(this to 1u) to constantOne))

//context(A)
//public fun <C> C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this))

@DslMarker
internal annotation class NumberedPolynomialConstructorDSL

@NumberedPolynomialConstructorDSL
public class NumberedPolynomialTermSignatureBuilder {
    private val signature: MutableList<UInt> = ArrayList()
    public fun build(): List<UInt> = signature
    public infix fun Int.inPowerOf(deg: UInt) {
        if (this > signature.lastIndex) {
            signature.addAll(List(this - signature.lastIndex - 1) { 0u })
            signature.add(deg)
        } else {
            signature[this] = deg
        }
    }
    public infix fun Int.to(deg: UInt): Unit = this inPowerOf deg
}

@NumberedPolynomialConstructorDSL
public class NumberedPolynomialBuilderOverRing<C> internal constructor(internal val context: Ring<C>, capacity: Int = 0) {
    private val coefficients: MutableMap<List<UInt>, C> = LinkedHashMap(capacity)
    public fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)
    public operator fun C.invoke(block: NumberedPolynomialTermSignatureBuilder.() -> Unit) {
        val signature = NumberedPolynomialTermSignatureBuilder().apply(block).build()
        coefficients[signature] = context { coefficients.getOrElse(signature) { zero } + this@invoke }
    }
}

@NumberedPolynomialConstructorDSL
public class NumberedPolynomialBuilderOverPolynomialSpace<C> internal constructor(internal val context: NumberedPolynomialSpace<C, *>, capacity: Int = 0) {
    private val coefficients: MutableMap<List<UInt>, C> = LinkedHashMap(capacity)
    public fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)
    public operator fun C.invoke(block: NumberedPolynomialTermSignatureBuilder.() -> Unit) {
        val signature = NumberedPolynomialTermSignatureBuilder().apply(block).build()
        coefficients[signature] = context { coefficients.getOrElse(signature) { constantZero } + this@invoke }
    }
}

@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(block: NumberedPolynomialBuilderOverRing<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverRing(this).apply(block).build()
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(capacity: Int, block: NumberedPolynomialBuilderOverRing<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverRing(this, capacity).apply(block).build()
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(block: NumberedPolynomialBuilderOverPolynomialSpace<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverPolynomialSpace(this).apply(block).build()
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(capacity: Int, block: NumberedPolynomialBuilderOverPolynomialSpace<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverPolynomialSpace(this, capacity).apply(block).build()

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

                        val result = getOrElse(degs) { constantZero } + other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
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

                        val result = getOrElse(degs) { constantZero } - other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
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
                .applyAndRemoveZeros {
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

                        val result = this@plus + getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
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

                        val result = this@minus - getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
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
                .applyAndRemoveZeros {
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
        if (this.isZero()) other
        else with(other.coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this@plus))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = this@plus + getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    override operator fun C.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this.isZero()) -other
        else with(other.coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this@minus))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyList<UInt>()

                        val result = this@minus - getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    override operator fun C.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this.isZero()) zero
        else NumberedPolynomial<C>(
            other.coefficients
                .applyAndRemoveZeros {
                    for (degs in keys) this[degs] = this@times * this[degs]!!
                }
        )

    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.plus(other: C): NumberedPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to other))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = getOrElse(degs) { constantZero } + other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.minus(other: C): NumberedPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to other))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = getOrElse(degs) { constantZero } - other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    override operator fun NumberedPolynomial<C>.times(other: C): NumberedPolynomial<C> =
        if (other.isZero()) zero
        else NumberedPolynomial<C>(
            coefficients
                .applyAndRemoveZeros {
                    for (degs in keys) this[degs] = this[degs]!! * other
                }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): NumberedPolynomial<C> =
        if (value == 0) zero
        else NumberedPolynomial(mapOf(emptyList<UInt>() to value))

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
            buildCoefficients(coefficients.size + other.coefficients.size) {
                other.coefficients.mapValuesTo(this) { it.value }
                other.coefficients.mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! + value else value }
            }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            buildCoefficients(coefficients.size + other.coefficients.size) {
                other.coefficients.mapValuesTo(this) { it.value }
                other.coefficients.mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! - value else -value }
            }
        )
    /**
     * Returns product of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when {
            isZero() -> zero
            other.isZero() -> zero
            else ->
                NumberedPolynomial<C>(
                    buildCoefficients(coefficients.size * other.coefficients.size) {
                        for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                            val degs =
                                (0..max(degs1.lastIndex, degs2.lastIndex))
                                    .map { degs1.getOrElse(it) { 0U } + degs2.getOrElse(it) { 0U } }
                            val c = c1 * c2
                            this[degs] = if (degs in this) this[degs]!! + c else c
                        }
                    }
                )
        }

    /**
     * Check if the instant is zero polynomial.
     */
    public override fun NumberedPolynomial<C>.isZero(): Boolean = coefficients.values.all { it.isZero() }
    /**
     * Check if the instant is unit polynomial.
     */
    public override fun NumberedPolynomial<C>.isOne(): Boolean =
        with(coefficients) {
            var foundAbsoluteTermAndItIsOne = false
            for ((degs, c) in this) {
                if (degs.isNotEmpty()) if (c.isNotZero()) return@with false
                else {
                    if (c.isNotOne()) return@with false
                    else foundAbsoluteTermAndItIsOne = true
                }
            }
            foundAbsoluteTermAndItIsOne
        }
    /**
     * Check if the instant is minus unit polynomial.
     */
    public override fun NumberedPolynomial<C>.isMinusOne(): Boolean =
        with(coefficients) {
            var foundAbsoluteTermAndItIsMinusOne = false
            for ((degs, c) in this) {
                if (degs.isNotEmpty()) if (c.isNotZero()) return@with false
                else {
                    if (c.isNotMinusOne()) return@with false
                    else foundAbsoluteTermAndItIsMinusOne = true
                }
            }
            foundAbsoluteTermAndItIsMinusOne
        }

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
     * Checks equality of the polynomials.
     */
    override infix fun NumberedPolynomial<C>.equalsTo(other: NumberedPolynomial<C>): Boolean =
        when {
            this === other -> true
            else -> coefficients.size == other.coefficients.size &&
                    coefficients.all { (key, value) -> with(other.coefficients) { key in this && this[key] == value } }
        }

    /**
     * Maximal index (ID) of variable occurring in the polynomial with positive power. If there is no such variable,
     * the result is `-1`.
     */
    public val NumberedPolynomial<C>.lastVariable: Int
        get() = coefficients.entries.maxOfOrNull { (degs, c) -> if (c.isZero()) -1 else degs.lastIndex } ?: -1
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    override val NumberedPolynomial<C>.degree: Int
        get() = coefficients.entries.maxOfOrNull { (degs, c) -> if (c.isZero()) -1 else degs.sum().toInt() } ?: -1
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
                coefficients.entries.forEach { (degs, c) ->
                    if (c.isNotZero()) degs.forEachIndexed { index, deg ->
                        this[index] = max(this[index], deg)
                    }
                }
            }
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public fun NumberedPolynomial<C>.degreeBy(variable: Int): UInt =
        coefficients.entries.maxOfOrNull { (degs, c) -> if (c.isZero()) 0u else degs.getOrElse(variable) { 0u } } ?: 0u
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public fun NumberedPolynomial<C>.degreeBy(variables: Collection<Int>): UInt =
        coefficients.entries.maxOfOrNull { (degs, c) ->
            if (c.isZero()) 0u else degs.withIndex().filter { (index, _) -> index in variables }.sumOf { it.value }
        } ?: 0u
    /**
     * Count of variables occurring in the polynomial with positive power. If there is no such variable,
     * the result is `0`.
     */
    public val NumberedPolynomial<C>.countOfVariables: Int
        get() =
            MutableList(lastVariable + 1) { false }.apply {
                coefficients.entries.forEach { (degs, c) ->
                    if (c.isNotZero()) degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
            }.count { it }

    /**
     * Checks if the instant is constant polynomial (of degree no more than 0) over considered ring.
     */
    override fun NumberedPolynomial<C>.isConstant(): Boolean =
        coefficients.all { (degs, c) -> degs.isEmpty() || c.isZero() }
    /**
     * Checks if the instant is constant non-zero polynomial (of degree no more than 0) over considered ring.
     */
    override fun NumberedPolynomial<C>.isNonZeroConstant(): Boolean =
        with(coefficients) {
            var foundAbsoluteTermAndItIsNotZero = false
            for ((degs, c) in this) {
                if (degs.isNotEmpty()) if (c.isNotZero()) return@with false
                else {
                    if (c.isZero()) return@with false
                    else foundAbsoluteTermAndItIsNotZero = true
                }
            }
            foundAbsoluteTermAndItIsNotZero
        }
    /**
     * If polynomial is a constant polynomial represents and returns it as constant.
     * Otherwise, (when the polynomial is not constant polynomial) returns `null`.
     */
    override fun NumberedPolynomial<C>.asConstantOrNull(): C? =
        with(coefficients) {
            if(isConstant()) getOrElse(emptyList()) { constantZero }
            else null
        }

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

    // TODO: Move to other internal utilities with context receiver
    @JvmName("applyAndRemoveZerosInternal")
    internal fun MutableMap<List<UInt>, C>.applyAndRemoveZeros(block: MutableMap<List<UInt>, C>.() -> Unit) : MutableMap<List<UInt>, C> {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        block()
        for ((degs, c) in this) if (c.isZero()) this.remove(degs)
        return this
    }
    internal fun Map<List<UInt>, C>.applyAndRemoveZeros(block: MutableMap<List<UInt>, C>.() -> Unit) : Map<List<UInt>, C> =
        toMutableMap().applyAndRemoveZeros(block)
    @OptIn(ExperimentalTypeInference::class)
    internal inline fun buildCoefficients(@BuilderInference builderAction: MutableMap<List<UInt>, C>.() -> Unit): Map<List<UInt>, C> {
        contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
        return buildMap {
            builderAction()
            for ((degs, c) in this) if (c.isZero()) this.remove(degs)
        }
    }
    @OptIn(ExperimentalTypeInference::class)
    internal inline fun buildCoefficients(capacity: Int, @BuilderInference builderAction: MutableMap<List<UInt>, C>.() -> Unit): Map<List<UInt>, C> {
        contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
        return buildMap(capacity) {
            builderAction()
            for ((degs, c) in this) if (c.isZero()) this.remove(degs)
        }
    }

    // TODO: Move to other constructors with context receiver
    public fun C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this))
}