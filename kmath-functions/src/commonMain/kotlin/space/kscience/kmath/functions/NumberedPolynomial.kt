package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
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
) : AbstractPolynomial<C> {
    override fun toString(): String = "NumberedPolynomial$coefficients"
}

// region Internal utilities

/**
 * Represents internal [Polynomial] errors.
 */
internal class NumberedPolynomialError : Error {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

/**
 * Throws an [PolynomialError] with the given [message].
 */
internal fun numberedPolynomialError(message: Any): Nothing = throw PolynomialError(message.toString())

/**
 * Returns the same degrees description of the monomial, but without extra zero degrees on the end.
 */
internal fun List<UInt>.cleanUp() = subList(0, indexOfLast { it != 0U } + 1)

// endregion

// region Constructors and converters
// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

//context(A)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean): NumberedPolynomial<C> {
//    if (!toCheckInput) return NumberedPolynomial<C>(coefs)
//
//    val fixedCoefs = mutableMapOf<List<UInt>, C>()
//
//    for (entry in coefs) {
//        val key = entry.key.cleanUp()
//        val value = entry.value
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    return NumberedPolynomial<C>(
//        fixedCoefs
//            .filter { it.value.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean): NumberedPolynomial<C> {
//    if (!toCheckInput) return NumberedPolynomial(pairs.toMap())
//
//    val fixedCoefs = mutableMapOf<List<UInt>, C>()
//
//    for (entry in pairs) {
//        val key = entry.first.cleanUp()
//        val value = entry.second
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    return NumberedPolynomial<C>(
//        fixedCoefs
//            .filter { it.value.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean): NumberedPolynomial<C> =
//    NumberedPolynomial(pairs.toMap(), toCheckInput)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param coefs Coefficients of the instants.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//public fun <C, A: Ring<C>> NumberedPolynomial(coefs: Map<List<UInt>, C>) = NumberedPolynomial(coefs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//public fun <C, A: Ring<C>> NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) = NumberedPolynomial(pairs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(A)
//public fun <C, A: Ring<C>> NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) = NumberedPolynomial(*pairs, toCheckInput = true)
//
//context(NumberedPolynomialSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean): NumberedPolynomial<C> {
//    if (!toCheckInput) return NumberedPolynomial(coefs)
//
//    val fixedCoefs = mutableMapOf<List<UInt>, C>()
//
//    for (entry in coefs) {
//        val key = entry.key.cleanUp()
//        val value = entry.value
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    return NumberedPolynomial<C>(
//        fixedCoefs
//            .filter { it.value.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(NumberedPolynomialSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean): NumberedPolynomial<C> {
//    if (!toCheckInput) return NumberedPolynomial(pairs.toMap())
//
//    val fixedCoefs = mutableMapOf<List<UInt>, C>()
//
//    for (entry in pairs) {
//        val key = entry.first.cleanUp()
//        val value = entry.second
//        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
//    }
//
//    return NumberedPolynomial<C>(
//        fixedCoefs
//            .filter { it.value.isNotZero() }
//    )
//}
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// * @param toCheckInput If it's `true` cleaning of [coefficients] is executed otherwise it is not.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(NumberedPolynomialSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean): NumberedPolynomial<C> =
//    NumberedPolynomial(pairs.toList(), toCheckInput)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param coefs Coefficients of the instants.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial(coefs: Map<List<UInt>, C>) = NumberedPolynomial(coefs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) = NumberedPolynomial(pairs, toCheckInput = true)
///**
// * Gets the coefficients in format of [coefficients] field and cleans it: removes zero degrees from end of received
// * lists, sums up proportional monomials, removes zero monomials, and if result is empty map adds only element in it.
// *
// * @param pairs Collection of pairs that represent monomials.
// *
// * @throws PolynomialError If no coefficient received or if any of degrees in any monomial is negative.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) = NumberedPolynomial(*pairs, toCheckInput = true)

public fun <C, A: Ring<C>> C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this))

// endregion

/**
 * Space of polynomials.
 *
 * @param C the type of operated polynomials.
 * @param A the intersection of [Ring] of [C] and [ScaleOperations] of [C].
 * @param ring the [A] instance.
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "INAPPLICABLE_JVM_NAME")
public open class NumberedPolynomialSpace<C, A : Ring<C>>(
    public final override val ring: A,
) : AbstractPolynomialSpaceOverRing<C, NumberedPolynomial<C>, A> {
    // region Polynomial-integer relation
    public override operator fun NumberedPolynomial<C>.plus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else
            NumberedPolynomial(
                coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = getOrElse(degs) { ring.zero } + other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun NumberedPolynomial<C>.minus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else
            NumberedPolynomial(
                coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = getOrElse(degs) { ring.zero } - other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun NumberedPolynomial<C>.times(other: Int): NumberedPolynomial<C> =
        if (other == 0) zero
        else NumberedPolynomial(
            coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> c * other }
                }
        )
    // endregion

    // region Integer-polynomial relation
    public override operator fun Int.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) other
        else
            NumberedPolynomial(
                other.coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = this@plus + getOrElse(degs) { ring.zero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun Int.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) other
        else
            NumberedPolynomial(
                other.coefficients
                    .toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = this@minus - getOrElse(degs) { ring.zero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
    public override operator fun Int.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) zero
        else NumberedPolynomial(
            other.coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> this@times * c }
                }
        )
    // endregion

    // region Constant-polynomial relation
    override operator fun C.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this.isZero()) other
        else with(other.coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this@plus))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = this@plus + getOrElse(degs) { ring.zero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    override operator fun C.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this.isZero()) -other
        else with(other.coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(listOf<UInt>() to this@minus))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        forEach { (degs, c) -> if(degs.isNotEmpty()) this[degs] = -c }

                        val degs = emptyList<UInt>()

                        val result = this@minus - getOrElse(degs) { ring.zero }

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    override operator fun C.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this.isZero()) zero
        else NumberedPolynomial<C>(
            other.coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> this@times * c }
                }
        )
    // endregion

    // region Polynomial-constant relation
    /**
     * Returns sum of the polynomials. [other] is interpreted as [NumberedPolynomial].
     */
    override operator fun NumberedPolynomial<C>.plus(other: C): NumberedPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(listOf<UInt>() to other))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = getOrElse(degs) { ring.zero } + other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns difference of the polynomials. [other] is interpreted as [NumberedPolynomial].
     */
    override operator fun NumberedPolynomial<C>.minus(other: C): NumberedPolynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) NumberedPolynomial<C>(mapOf(listOf<UInt>() to other))
            else NumberedPolynomial<C>(
                toMutableMap()
                    .apply {
                        val degs = emptyList<UInt>()

                        val result = getOrElse(degs) { ring.zero } - other

                        if (result.isZero()) remove(degs)
                        else this[degs] = result
                    }
            )
        }
    /**
     * Returns product of the polynomials. [other] is interpreted as [NumberedPolynomial].
     */
    override operator fun NumberedPolynomial<C>.times(other: C): NumberedPolynomial<C> =
        if (other.isZero()) zero
        else NumberedPolynomial<C>(
            coefficients
                .applyAndRemoveZeros {
                    mapValues { (_, c) -> c * other }
                }
        )
    // endregion

    // region Polynomial-polynomial relation
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
            coefficients
                .applyAndRemoveZeros {
                    other.coefficients
                        .mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! + value else value }
                }
        )
    /**
     * Returns difference of the polynomials.
     */
    override operator fun NumberedPolynomial<C>.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            coefficients
                .applyAndRemoveZeros {
                    other.coefficients
                        .mapValuesTo(this) { (key, value) -> if (key in this) this[key]!! - value else -value }
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

    public override fun NumberedPolynomial<C>.isZero(): Boolean = coefficients.values.all { it.isZero() }
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

    override val zero: NumberedPolynomial<C> = NumberedPolynomial<C>(emptyMap())
    override val one: NumberedPolynomial<C> =
        NumberedPolynomial<C>(
            mapOf(
                listOf<UInt>() to ring.one // 1 * x_1^0 * x_2^0 * ...
            )
        )

    // TODO: Docs
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER", "CovariantEquals")
    override infix fun NumberedPolynomial<C>.equalsTo(other: NumberedPolynomial<C>): Boolean =
        when {
            this === other -> true
            else -> coefficients.size == other.coefficients.size &&
                    coefficients.all { (key, value) -> with(other.coefficients) { key in this && this[key] == value } }
        }
    // endregion

    // Not sure is it necessary...
    // region Polynomial properties
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val NumberedPolynomial<C>.countOfVariables: Int
        get() = coefficients.entries.maxOfOrNull { (degs, c) -> if (c.isZero()) 0 else degs.size } ?: 0
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
     * And size of the list is [countOfVariables].
     */
    public val NumberedPolynomial<C>.degrees: List<UInt>
        get() =
            buildList(countOfVariables) {
                repeat(countOfVariables) { add(0U) }
                coefficients.entries.forEach { (degs, c) ->
                    if (c.isNotZero()) degs.forEachIndexed { index, deg ->
                        this[index] = max(this[index], deg)
                    }
                }
            }

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

    override fun NumberedPolynomial<C>.asConstantOrNull(): C? =
        with(coefficients) {
            if(isConstant()) getOrElse(emptyList()) { ring.zero }
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
    // endregion

    // region Utilities
    // TODO: Move to region internal utilities with context receiver
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
    // endregion
}