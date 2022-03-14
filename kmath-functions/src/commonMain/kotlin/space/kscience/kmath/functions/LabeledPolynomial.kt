/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.math.max


/**
 * Represents multivariate polynomials with labeled variables.
 *
 * @param C Ring in which the polynomial is considered.
 */
public data class LabeledPolynomial<C>
internal constructor(
    /**
     * Map that collects coefficients of the polynomial. Every non-zero monomial
     * `a x_1^{d_1} ... x_n^{d_n}` is represented as pair "key-value" in the map, where value is coefficients `a` and
     * key is map that associates variables in the monomial with multiplicity of them occurring in the monomial.
     * For example polynomial
     * ```
     * 5 a^2 c^3 - 6 b + 0 b c
     * ```
     * has coefficients represented as
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
     * where `a`, `b` and `c` are corresponding [Variable] objects.
     */
    public val coefficients: Map<Map<Variable, UInt>, C>
) : AbstractPolynomial<C> {
    override fun toString(): String = "LabeledPolynomial$coefficients"
}

// region Internal utilities

/**
 * Represents internal [LabeledPolynomial] errors.
 */
internal class LabeledPolynomialError: Error {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

/**
 * Throws an [LabeledPolynomialError] with the given [message].
 */
internal fun labeledPolynomialError(message: Any): Nothing = throw LabeledPolynomialError(message.toString())

/**
 * Returns the same degrees description of the monomial, but without zero degrees.
 */
internal fun Map<Variable, UInt>.cleanUp() = filterValues { it > 0U }

// endregion

// region Constructors and converters

///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//internal fun <C, A: Ring<C>> LabeledPolynomial(coefs: Map<Map<Variable, UInt>, C>, toCheckInput: Boolean = true): LabeledPolynomial<C> {
//    if (!toCheckInput) return LabeledPolynomial<C>(coefs)
//
//    // Map for cleaned coefficients.
//    val fixedCoefs = mutableMapOf<Map<Variable, UInt>, C>()
//
//    // Cleaning the degrees, summing monomials of the same degrees.
//    for (entry in coefs) {
//        val key = entry.key.cleanUp()
//        val value = entry.value
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    // Removing zero monomials.
//    return LabeledPolynomial<C>(
//        fixedCoefs
//            .filter { it.value.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//internal fun <C, A: Ring<C>> LabeledPolynomial(pairs: Collection<Pair<Map<Variable, UInt>, C>>, toCheckInput: Boolean): LabeledPolynomial<C> {
//    if (!toCheckInput) return LabeledPolynomial<C>(pairs.toMap())
//
//    // Map for cleaned coefficients.
//    val fixedCoefs = mutableMapOf<Map<Variable, UInt>, C>()
//
//    // Cleaning the degrees, summing monomials of the same degrees.
//    for (entry in pairs) {
//        val key = entry.first.cleanUp()
//        val value = entry.second
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    // Removing zero monomials.
//    return LabeledPolynomial<C>(
//        fixedCoefs.filterValues { it.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represents monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//internal fun <C, A: Ring<C>> LabeledPolynomial(vararg pairs: Pair<Map<Variable, UInt>, C>, toCheckInput: Boolean): LabeledPolynomial<C> {
//    if (!toCheckInput) return LabeledPolynomial<C>(pairs.toMap())
//
//    // Map for cleaned coefficients.
//    val fixedCoefs = mutableMapOf<Map<Variable, UInt>, C>()
//
//    // Cleaning the degrees, summing monomials of the same degrees.
//    for (entry in pairs) {
//        val key = entry.first.cleanUp()
//        val value = entry.second
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    // Removing zero monomials.
//    return LabeledPolynomial<C>(
//        fixedCoefs.filterValues { it.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param coefs Coefficients of the instants.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//fun <C, A: Ring<C>> LabeledPolynomial(coefs: Map<Map<Variable, UInt>, C>): LabeledPolynomial<C> = LabeledPolynomial(coefs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represents monomials.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//fun <C, A: Ring<C>> LabeledPolynomial(pairs: Collection<Pair<Map<Variable, UInt>, C>>): LabeledPolynomial<C> = LabeledPolynomial(pairs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represents monomials.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//fun <C, A: Ring<C>> LabeledPolynomial(vararg pairs: Pair<Map<Variable, UInt>, C>): LabeledPolynomial<C> = LabeledPolynomial(*pairs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(LabeledPolynomialSpace<C, A>)
//internal fun <C, A: Ring<C>> LabeledPolynomial(coefs: Map<Map<Variable, UInt>, C>, toCheckInput: Boolean = true): LabeledPolynomial<C> {
//    if (!toCheckInput) return LabeledPolynomial<C>(coefs)
//
//    // Map for cleaned coefficients.
//    val fixedCoefs = mutableMapOf<Map<Variable, UInt>, C>()
//
//    // Cleaning the degrees, summing monomials of the same degrees.
//    for (entry in coefs) {
//        val key = entry.key.cleanUp()
//        val value = entry.value
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    // Removing zero monomials.
//    return LabeledPolynomial<C>(
//        fixedCoefs
//            .filter { it.value.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(LabeledPolynomialSpace<C, A>)
//internal fun <C, A: Ring<C>> LabeledPolynomial(pairs: Collection<Pair<Map<Variable, UInt>, C>>, toCheckInput: Boolean): LabeledPolynomial<C> {
//    if (!toCheckInput) return LabeledPolynomial<C>(pairs.toMap())
//
//    // Map for cleaned coefficients.
//    val fixedCoefs = mutableMapOf<Map<Variable, UInt>, C>()
//
//    // Cleaning the degrees, summing monomials of the same degrees.
//    for (entry in pairs) {
//        val key = entry.first.cleanUp()
//        val value = entry.second
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    // Removing zero monomials.
//    return LabeledPolynomial<C>(
//        fixedCoefs.filterValues { it.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represents monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(LabeledPolynomialSpace<C, A>)
//internal fun <C, A: Ring<C>> LabeledPolynomial(vararg pairs: Pair<Map<Variable, UInt>, C>, toCheckInput: Boolean): LabeledPolynomial<C> {
//    if (!toCheckInput) return LabeledPolynomial<C>(pairs.toMap())
//
//    // Map for cleaned coefficients.
//    val fixedCoefs = mutableMapOf<Map<Variable, UInt>, C>()
//
//    // Cleaning the degrees, summing monomials of the same degrees.
//    for (entry in pairs) {
//        val key = entry.first.cleanUp()
//        val value = entry.second
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    // Removing zero monomials.
//    return LabeledPolynomial<C>(
//        fixedCoefs.filterValues { it.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param coefs Coefficients of the instants.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial(coefs: Map<Map<Variable, UInt>, C>): LabeledPolynomial<C> = LabeledPolynomial(coefs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represents monomials.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial(pairs: Collection<Pair<Map<Variable, UInt>, C>>): LabeledPolynomial<C> = LabeledPolynomial(pairs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from keys of received
// * map, sums up proportional monomials, removes aero monomials, and if result is zero map adds only element in it.
// *
// * @param pairs Collection of pairs that represents monomials.
// *
// * @throws LabeledPolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial(vararg pairs: Pair<Map<Variable, UInt>, C>): LabeledPolynomial<C> = LabeledPolynomial(*pairs, toCheckInput = true)
//
//fun <C> C.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to this))
//
//context(A)
//fun <C, A: Ring<C>> Variable.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf<Variable, UInt>(this to 1U) to one))
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> Variable.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf<Variable, UInt>(this to 1U) to constantOne))
//
//context(A)
//fun <C, A: Ring<C>> Variable.asLabeledPolynomial(c: C) : LabeledPolynomial<C> =
//    if(c.isZero()) LabeledPolynomial<C>(emptyMap())
//    else LabeledPolynomial<C>(mapOf(mapOf<Variable, UInt>(this to 1U) to c))
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> Variable.asLabeledPolynomial(c: C) : LabeledPolynomial<C> =
//    if(c.isZero()) zero
//    else LabeledPolynomial<C>(mapOf(mapOf<Variable, UInt>(this to 1U) to c))

// endregion

/**
 * Space of polynomials.
 *
 * @param C the type of operated polynomials.
 * @param A the intersection of [Ring] of [C] and [ScaleOperations] of [C].
 * @param ring the [A] instance.
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "INAPPLICABLE_JVM_NAME")
public class LabeledPolynomialSpace<C, A : Ring<C>>(
    public override val ring: A,
) : AbstractPolynomialSpaceOverRing<C, LabeledPolynomial<C>, A> {

    // region Variable-integer relation
    public operator fun Variable.plus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomial<C>(mapOf(
            mapOf(this@plus to 1U) to constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Variable, UInt>() to constantOne * other,
        ))
    public operator fun Variable.minus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomial<C>(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
            emptyMap<Variable, UInt>() to constantOne * other,
        ))
    public operator fun Variable.times(other: Int): LabeledPolynomial<C> =
        if (other == 0) zero
        else LabeledPolynomial<C>(mapOf(
            mapOf(this to 1U) to constantOne * other,
        ))
    // endregion

    // region Integer-variable relation
    public operator fun Int.plus(other: Variable): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to constantOne,
            emptyMap<Variable, UInt>() to constantOne * this@plus,
        ))
    public operator fun Int.minus(other: Variable): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to -constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Variable, UInt>() to constantOne * this@minus,
        ))
    public operator fun Int.times(other: Variable): LabeledPolynomial<C> =
        if (this == 0) zero
        else LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to constantOne * this@times,
        ))
    // endregion

    // region Polynomial-integer relation
    public override operator fun LabeledPolynomial<C>.plus(other: Int): LabeledPolynomial<C> =
        if (other == 0) this
        else
            LabeledPolynomial(
                coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyMap<Variable, UInt>()

                        val result = getOrElse(degs) { constantZero } + other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun LabeledPolynomial<C>.minus(other: Int): LabeledPolynomial<C> =
        if (other == 0) this
        else
            LabeledPolynomial(
                coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyMap<Variable, UInt>()

                        val result = getOrElse(degs) { constantZero } - other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun LabeledPolynomial<C>.times(other: Int): LabeledPolynomial<C> =
        if (other == 0) zero
        else LabeledPolynomial(
            coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> c * other }
                }
        )
    // endregion

    // region Integer-polynomial relation
    public override operator fun Int.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this == 0) other
        else
            LabeledPolynomial(
                other.coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyMap<Variable, UInt>()

                        val result = this@plus + getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun Int.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this == 0) other
        else
            LabeledPolynomial(
                other.coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyMap<Variable, UInt>()

                        val result = this@minus - getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun Int.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this == 0) zero
        else LabeledPolynomial(
            other.coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> this@times * c }
                }
        )
    // endregion

    // region Constant-variable relation
    public operator fun C.plus(other: Variable): LabeledPolynomial<C> =
        if (isZero()) LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to constantOne,
            emptyMap<Variable, UInt>() to this@plus,
        ))
    public operator fun C.minus(other: Variable): LabeledPolynomial<C> =
        if (isZero()) LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to -constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Variable, UInt>() to this@minus,
        ))
    public operator fun C.times(other: Variable): LabeledPolynomial<C> =
        if (isZero()) zero
        else LabeledPolynomial<C>(mapOf(
            mapOf(other to 1U) to this@times,
        ))
    // endregion

    // region Variable-constant relation
    public operator fun Variable.plus(other: C): LabeledPolynomial<C> =
        if (other.isZero()) LabeledPolynomial<C>(mapOf(
            mapOf(this@plus to 1U) to constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Variable, UInt>() to other,
        ))
    public operator fun Variable.minus(other: C): LabeledPolynomial<C> =
        if (other.isZero()) LabeledPolynomial<C>(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(this@minus to 1U) to -constantOne,
            emptyMap<Variable, UInt>() to other,
        ))
    public operator fun Variable.times(other: C): LabeledPolynomial<C> =
        if (other.isZero()) zero
        else LabeledPolynomial<C>(mapOf(
            mapOf(this@times to 1U) to other,
        ))
    // endregion

    // region Constant-polynomial relation
    override operator fun C.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this.isZero()) other
        else with(other.coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to this@plus))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Variable, UInt>()

                        val result = this@plus + getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    override operator fun C.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this.isZero()) other
        else with(other.coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to this@minus))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyMap<Variable, UInt>()

                        val result = this@minus - getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    override operator fun C.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (this.isZero()) zero
        else LabeledPolynomial<C>(
            other.coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> this@times * c }
                }
        )
    // endregion

    // region Polynomial-constant relation
    /**
     * Returns sum of the polynomials. [other] is interpreted as [UnivariatePolynomial].
     */
    override operator fun LabeledPolynomial<C>.plus(other: C): LabeledPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to other))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyMap<Variable, UInt>()

                        val result = getOrElse(degs) { constantZero } + other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns difference of the polynomials. [other] is interpreted as [UnivariatePolynomial].
     */
    override operator fun LabeledPolynomial<C>.minus(other: C): LabeledPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to other))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyMap<Variable, UInt>()

                        val result = getOrElse(degs) { constantZero } - other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns product of the polynomials. [other] is interpreted as [UnivariatePolynomial].
     */
    override operator fun LabeledPolynomial<C>.times(other: C): LabeledPolynomial<C> =
        if (other.isZero()) zero
        else LabeledPolynomial<C>(
            coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> c * other }
                }
        )
    // endregion

    // region Variable-variable relation
    public operator fun Variable.plus(other: Variable): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomial<C>(mapOf(
            mapOf(this to 1U) to constantOne * 2
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to constantOne,
        ))
    public operator fun Variable.minus(other: Variable): LabeledPolynomial<C> =
        if (this == other) zero
        else LabeledPolynomial<C>(mapOf(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to -constantOne,
        ))
    public operator fun Variable.times(other: Variable): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomial<C>(mapOf(
            mapOf(this to 2U) to constantOne
        ))
        else LabeledPolynomial<C>(mapOf(
            mapOf(this to 1U, other to 1U) to constantOne,
        ))
    // endregion

    // region Variable-polynomial relation
    public operator fun Variable.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(mapOf(this@plus to 1u) to constantOne))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = mapOf(this@plus to 1U)

                        val result = constantOne + getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    public operator fun Variable.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(mapOf(this@minus to 1u) to constantOne))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = mapOf(this@minus to 1U)

                        val result = constantOne - getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    public operator fun Variable.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomial<C>(
            other.coefficients
                .mapKeys { (degs, _) -> degs.toMutableMap().also{ it[this] = if (this in it) it[this]!! + 1U else 1U } }
        )
    // endregion

    // region Polynomial-variable relation
    public operator fun LabeledPolynomial<C>.plus(other: Variable): LabeledPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(mapOf(other to 1u) to constantOne))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = mapOf(other to 1U)

                        val result = constantOne + getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    public operator fun LabeledPolynomial<C>.minus(other: Variable): LabeledPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) LabeledPolynomial<C>(mapOf(mapOf(other to 1u) to constantOne))
            else LabeledPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = mapOf(other to 1U)

                        val result = constantOne - getOrElse(degs) { constantZero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    public operator fun LabeledPolynomial<C>.times(other: Variable): LabeledPolynomial<C> =
        LabeledPolynomial<C>(
            coefficients
                .mapKeys { (degs, _) -> degs.toMutableMap().also{ it[other] = if (other in it) it[other]!! + 1U else 1U } }
        )
    // endregion

    // region Polynomial-polynomial relation
    /**
     * Returns negation of the polynomial.
     */
    override fun LabeledPolynomial<C>.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomial<C>(
            coefficients.mapValues { -it.value }
        )
    /**
     * Returns sum of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomial<C>(
            coefficients
                .applyAndRemoveZeros {
                    other.coefficients
                        .mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! + value else value }
                }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomial<C>(
            coefficients
                .applyAndRemoveZeros {
                    other.coefficients
                        .mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! - value else -value }
                }
        )
    /**
     * Returns product of the polynomials.
     */
    override operator fun LabeledPolynomial<C>.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            isZero() -> zero
            other.isZero() -> zero
            else -> LabeledPolynomial<C>(
                buildCoefficients(coefficients.size * other.coefficients.size) {
                    for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                        val degs = degs1.toMutableMap()
                        degs2.mapValuesTo(degs) { (variable, deg) -> degs.getOrElse(variable) { 0u } + deg }
                        val c = c1 * c2
                        this[degs] = if (degs in this) this[degs]!! + c else c
                    }
                }
            )
        }

    override val zero: LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to constantZero))
    override val one: LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to constantOne))

    // TODO: Docs
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER", "CovariantEquals")
    override infix fun LabeledPolynomial<C>.equalsTo(other: LabeledPolynomial<C>): Boolean =
        when {
            this === other -> true
            else -> coefficients.size == other.coefficients.size &&
                    coefficients.all { (key, value) -> with(other.coefficients) { key in this && this[key] == value } }
        }
    // endregion

    // Not sure is it necessary...
    // region Polynomial properties
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    override val LabeledPolynomial<C>.degree: Int
        get() = coefficients.entries.maxOfOrNull { (degs, c) -> if (c.isZero()) -1 else degs.values.sum().toInt() } ?: -1
    /**
     * Map that associates variables (that appear in the polynomial in positive exponents) with their most exponents
     * in which they are appeared in the polynomial.
     *
     * As consequence all values in the map are positive integers. Also, if the polynomial is constant, the map is empty.
     * And keys of the map is the same as in [variables].
     */
    public val LabeledPolynomial<C>.degrees: Map<Variable, UInt>
        get() =
            buildMap {
                coefficients.entries.forEach { (degs, c) ->
                    if (c.isNotZero()) degs.mapValuesTo(this) { (variable, deg) ->
                        max(getOrElse(variable) { 0u }, deg)
                    }
                }
            }
    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledPolynomial<C>.variables: Set<Variable>
        get() =
            buildSet {
                coefficients.entries.forEach { (degs, c) -> if (c.isNotZero()) addAll(degs.keys) }
            }
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledPolynomial<C>.countOfVariables: Int get() = variables.size

    /**
     * Checks if the instant is constant polynomial (of degree no more than 0) over considered ring.
     */
    override fun LabeledPolynomial<C>.isConstant(): Boolean =
        coefficients.all { (degs, c) -> degs.isEmpty() || c.isZero() }
    /**
     * Checks if the instant is constant non-zero polynomial (of degree no more than 0) over considered ring.
     */
    override fun LabeledPolynomial<C>.isNonZeroConstant(): Boolean =
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

    override fun LabeledPolynomial<C>.asConstantOrNull(): C? =
        with(coefficients) {
            if(isConstant()) getOrElse(emptyMap()) { constantZero }
            else null
        }

//    @Suppress("NOTHING_TO_INLINE")
//    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Variable, C>): LabeledPolynomial<C> = this.substitute(ring, argument)
//    @Suppress("NOTHING_TO_INLINE")
//    @JvmName("substitutePolynomial")
//    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Variable, LabeledPolynomial<C>>): LabeledPolynomial<C> = this.substitute(ring, argument)
//
//    @Suppress("NOTHING_TO_INLINE")
//    public inline fun LabeledPolynomial<C>.asFunction(): (Map<Variable, C>) -> LabeledPolynomial<C> = { this.substitute(ring, it) }
//    @Suppress("NOTHING_TO_INLINE")
//    public inline fun LabeledPolynomial<C>.asFunctionOnConstants(): (Map<Variable, C>) -> LabeledPolynomial<C> = { this.substitute(ring, it) }
//    @Suppress("NOTHING_TO_INLINE")
//    public inline fun LabeledPolynomial<C>.asFunctionOnPolynomials(): (Map<Variable, LabeledPolynomial<C>>) -> LabeledPolynomial<C> = { this.substitute(ring, it) }
//
//    @Suppress("NOTHING_TO_INLINE")
//    public inline operator fun LabeledPolynomial<C>.invoke(argument: Map<Variable, C>): LabeledPolynomial<C> = this.substitute(ring, argument)
//    @Suppress("NOTHING_TO_INLINE")
//    @JvmName("invokePolynomial")
//    public inline operator fun LabeledPolynomial<C>.invoke(argument: Map<Variable, LabeledPolynomial<C>>): LabeledPolynomial<C> = this.substitute(ring, argument)
    // endregion

    // region Utilities
    // TODO: Move to region internal utilities with context receiver
    @JvmName("applyAndRemoveZerosInternal")
    internal fun MutableMap<Map<Variable, UInt>, C>.applyAndRemoveZeros(block: MutableMap<Map<Variable, UInt>, C>.() -> Unit) : MutableMap<Map<Variable, UInt>, C> {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        block()
        for ((degs, c) in this) if (c.isZero()) this.remove(degs)
        return this
    }
    internal fun Map<Map<Variable, UInt>, C>.applyAndRemoveZeros(block: MutableMap<Map<Variable, UInt>, C>.() -> Unit) : Map<Map<Variable, UInt>, C> =
        toMutableMap().applyAndRemoveZeros(block)
    @OptIn(ExperimentalTypeInference::class)
    internal inline fun buildCoefficients(@BuilderInference builderAction: MutableMap<Map<Variable, UInt>, C>.() -> Unit): Map<Map<Variable, UInt>, C> {
        contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
        return buildMap {
            builderAction()
            for ((degs, c) in this) if (c.isZero()) this.remove(degs)
        }
    }
    @OptIn(ExperimentalTypeInference::class)
    internal inline fun buildCoefficients(capacity: Int, @BuilderInference builderAction: MutableMap<Map<Variable, UInt>, C>.() -> Unit): Map<Map<Variable, UInt>, C> {
        contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
        return buildMap(capacity) {
            builderAction()
            for ((degs, c) in this) if (c.isZero()) this.remove(degs)
        }
    }
    // endregion
}