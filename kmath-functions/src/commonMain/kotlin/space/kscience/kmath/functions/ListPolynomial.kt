/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min


/**
 * Polynomial model without fixation on specific context they are applied to.
 *
 * @param coefficients constant is the leftmost coefficient.
 */
public data class ListPolynomial<C>(
    /**
     * List that collects coefficients of the polynomial. Every monomial `a x^d` is represented as a coefficients
     * `a` placed into the list with index `d`. For example coefficients of polynomial `5 x^2 - 6` can be represented as
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
     * It is recommended not to put extra zeros at end of the list (as for `0x^3` and `0x^4` in the example), but is not
     * prohibited.
     */
    public val coefficients: List<C>
) : Polynomial<C> {
    override fun toString(): String = "Polynomial$coefficients"
}

/**
 * Returns a [ListPolynomial] instance with given [coefficients]. The collection of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(coefficients: List<C>, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(with(coefficients) { if (reverse) reversed() else this })

/**
 * Returns a [ListPolynomial] instance with given [coefficients]. The collection of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(vararg coefficients: C, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(with(coefficients) { if (reverse) reversed() else toList() })

public fun <C> C.asListPolynomial() : ListPolynomial<C> = ListPolynomial(listOf(this))

/**
 * Space of univariate polynomials constructed over ring.
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param A type of underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public open class ListPolynomialSpace<C, A : Ring<C>>(
    public override val ring: A,
) : PolynomialSpaceOverRing<C, ListPolynomial<C>, A> {
    /**
     * Returns sum of the polynomial and the integer represented as polynomial.
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
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
    /**
     * Returns difference between the polynomial and the integer represented as polynomial.
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
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
    /**
     * Returns product of the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun ListPolynomial<C>.times(other: Int): ListPolynomial<C> =
        if (other == 0) zero
        else ListPolynomial(
            coefficients
                .applyAndRemoveZeros {
                    for (deg in indices) this[deg] = this[deg] * other
                }
        )

    /**
     * Returns sum of the integer represented as polynomial and the polynomial.
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
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
    /**
     * Returns difference between the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        forEachIndexed { index, c -> if (index != 0) this[index] = -c }

                        val result = this@minus - getOrElse(0) { constantZero }
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
    /**
     * Returns product of the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0) zero
        else ListPolynomial(
            other.coefficients
                .applyAndRemoveZeros {
                    for (deg in indices) this[deg] = this@times * this[deg]
                }
        )

    /**
     * Converts the integer [value] to polynomial.
     */
    public override fun number(value: Int): ListPolynomial<C> = number(constantNumber(value))

    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    public override operator fun C.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this.isZero()) other
        else with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@plus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) this@plus else this@plus + get(0)
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
        }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    public override operator fun C.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this.isZero()) other
        else with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@minus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        forEachIndexed { index, c -> if (index != 0) this[index] = -c }

                        val result = if (size == 0) this@minus else this@minus - get(0)
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
        }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    public override operator fun C.times(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this.isZero()) other
        else ListPolynomial(
            other.coefficients
                .applyAndRemoveZeros {
                    for (deg in indices) this[deg] = this@times * this[deg]
                }
        )

    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    public override operator fun ListPolynomial<C>.plus(other: C): ListPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else get(0) + other
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
        }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    public override operator fun ListPolynomial<C>.minus(other: C): ListPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(-other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else get(0) - other
                        val isResultZero = result.isZero()

                        when {
                            size == 0 && !isResultZero -> add(result)
                            size > 1 || !isResultZero -> this[0] = result
                            else -> clear()
                        }
                    }
            )
        }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    public override operator fun ListPolynomial<C>.times(other: C): ListPolynomial<C> =
        if (other.isZero()) this
        else ListPolynomial(
            coefficients
                .applyAndRemoveZeros {
                    for (deg in indices) this[deg] = this[deg] * other
                }
        )

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun number(value: C): ListPolynomial<C> =
        if (value.isZero()) zero
        else ListPolynomial(value)

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
            Coefficients(max(thisDegree, otherDegree) + 1) {
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
            Coefficients(max(thisDegree, otherDegree) + 1) {
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
        return when {
            thisDegree == -1 -> zero
            otherDegree == -1 -> zero
            else ->
                ListPolynomial(
                    Coefficients(thisDegree + otherDegree + 1) { d ->
                        (max(0, d - otherDegree)..min(thisDegree, d))
                            .map { coefficients[it] * other.coefficients[d - it] }
                            .reduce { acc, rational -> acc + rational }
                    }
                )
        }
    }

    /**
     * Check if the instant is zero polynomial.
     */
    public override fun ListPolynomial<C>.isZero(): Boolean = coefficients.all { it.isZero() }
    /**
     * Check if the instant is unit polynomial.
     */
    public override fun ListPolynomial<C>.isOne(): Boolean =
        with(coefficients) {
            isNotEmpty() && withIndex().all { (index, c) -> if (index == 0) c.isOne() else c.isZero() }
        }
    /**
     * Check if the instant is minus unit polynomial.
     */
    public override fun ListPolynomial<C>.isMinusOne(): Boolean =
        with(coefficients) {
            isNotEmpty() && withIndex().all { (index, c) -> if (index == 0) c.isMinusOne() else c.isZero() }
        }

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    override val zero: ListPolynomial<C> = ListPolynomial(emptyList())
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    override val one: ListPolynomial<C> = ListPolynomial(listOf(constantOne))

    /**
     * Checks equality of the polynomials.
     */
    public override infix fun ListPolynomial<C>.equalsTo(other: ListPolynomial<C>): Boolean =
        when {
            this === other -> true
            this.degree == other.degree -> (0..degree).all { coefficients[it] == other.coefficients[it] }
            else -> false
        }

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public override val ListPolynomial<C>.degree: Int get() = coefficients.indexOfLast { it != constantZero }

    /**
     * If polynomial is a constant polynomial represents and returns it as constant.
     * Otherwise, (when the polynomial is not constant polynomial) returns `null`.
     */
    public override fun ListPolynomial<C>.asConstantOrNull(): C? =
        with(coefficients) {
            when {
                isEmpty() -> constantZero
                withIndex().all { (index, c) -> index == 0 || c.isZero() } -> first()
                else -> null
            }
        }

    @Suppress("NOTHING_TO_INLINE")
    public inline fun ListPolynomial<C>.substitute(argument: C): C = this.substitute(ring, argument)
    @Suppress("NOTHING_TO_INLINE")
    public inline fun ListPolynomial<C>.substitute(argument: ListPolynomial<C>): ListPolynomial<C> = this.substitute(ring, argument)

    @Suppress("NOTHING_TO_INLINE")
    public inline fun ListPolynomial<C>.asFunction(): (C) -> C = { this.substitute(ring, it) }
    @Suppress("NOTHING_TO_INLINE")
    public inline fun ListPolynomial<C>.asFunctionOnConstants(): (C) -> C = { this.substitute(ring, it) }
    @Suppress("NOTHING_TO_INLINE")
    public inline fun ListPolynomial<C>.asFunctionOnPolynomials(): (ListPolynomial<C>) -> ListPolynomial<C> = { this.substitute(ring, it) }

    /**
     * Evaluates the polynomial for the given value [argument].
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun ListPolynomial<C>.invoke(argument: C): C = this.substitute(ring, argument)
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun ListPolynomial<C>.invoke(argument: ListPolynomial<C>): ListPolynomial<C> = this.substitute(ring, argument)

    // TODO: Move to other internal utilities with context receiver
    @JvmName("applyAndRemoveZerosInternal")
    internal inline fun MutableList<C>.applyAndRemoveZeros(block: MutableList<C>.() -> Unit) : MutableList<C> {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        block()
        while (isNotEmpty() && elementAt(lastIndex).isZero()) removeAt(lastIndex)
        return this
    }
    internal inline fun List<C>.applyAndRemoveZeros(block: MutableList<C>.() -> Unit) : List<C> =
        toMutableList().applyAndRemoveZeros(block)
    @Suppress("FunctionName")
    internal inline fun MutableCoefficients(size: Int, init: (index: Int) -> C): MutableList<C> {
        val list = ArrayList<C>(size)
        repeat(size) { index -> list.add(init(index)) }
        with(list) { while (isNotEmpty() && elementAt(lastIndex).isZero()) removeAt(lastIndex) }
        return list
    }
    @Suppress("FunctionName")
    internal inline fun Coefficients(size: Int, init: (index: Int) -> C): List<C> = MutableCoefficients(size, init)
    @OptIn(ExperimentalTypeInference::class)
    internal inline fun buildCoefficients(@BuilderInference builderAction: MutableList<C>.() -> Unit): List<C> {
        contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
        return buildList {
            builderAction()
            while (isNotEmpty() && elementAt(lastIndex).isZero()) removeAt(lastIndex)
        }
    }
    @OptIn(ExperimentalTypeInference::class)
    internal inline fun buildCoefficients(capacity: Int, @BuilderInference builderAction: MutableList<C>.() -> Unit): List<C> {
        contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
        return buildList(capacity) {
            builderAction()
            while (isNotEmpty() && elementAt(lastIndex).isZero()) removeAt(lastIndex)
        }
    }
}

/**
 * Space of polynomials constructed over ring.
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param A type of underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class ScalableListPolynomialSpace<C, A>(
    ring: A,
) : ListPolynomialSpace<C, A>(ring), ScaleOperations<ListPolynomial<C>> where A : Ring<C>, A : ScaleOperations<C> {
    override fun scale(a: ListPolynomial<C>, value: Double): ListPolynomial<C> =
        ring { ListPolynomial(a.coefficients.map { scale(it, value) }) }
}
