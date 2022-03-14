/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke


public class LabeledRationalFunction<C>(
    public override val numerator: LabeledPolynomial<C>,
    public override val denominator: LabeledPolynomial<C>
) : AbstractRationalFunction<C, LabeledPolynomial<C>> {
    override fun toString(): String = "LabeledRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

// region Internal utilities

/**
 * Represents internal [LabeledRationalFunction] errors.
 */
internal class LabeledRationalFunctionError : Error {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

/**
 * Throws an [LabeledRationalFunctionError] with the given [message].
 */
internal fun labeledRationalFunctionError(message: Any): Nothing = throw LabeledRationalFunctionError(message.toString())

// endregion

// region Constructors and converters
// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>, denominator: Polynomial<C>): RationalFunction<C> =
//    if (denominator.isZero()) throw ArithmeticException("/ by zero")
//    else RationalFunction<C>(numerator, denominator)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, denominatorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction<C>(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
//        Polynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } ).also { if (it.isZero()) }
//    )
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>): RationalFunction<C> =
//    RationalFunction(numerator, onePolynomial)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } )
//    )

// TODO: Rewrite former constructors as fabrics
//constructor(numeratorCoefficients: Map<Map<Variable, Int>, C>, denominatorCoefficients: Map<Map<Variable, Int>, C>) : this(
//LabeledPolynomial(numeratorCoefficients),
//LabeledPolynomial(denominatorCoefficients)
//)
//
//constructor(numeratorCoefficients: Collection<Pair<Map<Variable, Int>, C>>, denominatorCoefficients: Collection<Pair<Map<Variable, Int>, C>>) : this(
//LabeledPolynomial(numeratorCoefficients),
//LabeledPolynomial(denominatorCoefficients)
//)
//
//constructor(numerator: LabeledPolynomial<C>) : this(numerator, numerator.getOne())
//constructor(numeratorCoefficients: Map<Map<Variable, Int>, C>) : this(
//LabeledPolynomial(numeratorCoefficients)
//)
//
//constructor(numeratorCoefficients: Collection<Pair<Map<Variable, Int>, C>>) : this(
//LabeledPolynomial(numeratorCoefficients)
//)

// endregion

public class LabeledRationalFunctionSpace<C, A: Ring<C>>(
    public val ring: A,
) : AbstractRationalFunctionalSpaceOverPolynomialSpace<C, LabeledPolynomial<C>, LabeledRationalFunction<C>, A> {

    override val polynomialRing : LabeledPolynomialSpace<C, A> = LabeledPolynomialSpace(ring)

    // region Rational-integer relation
    /**
     * Returns sum of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun LabeledRationalFunction<C>.plus(other: Int): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun LabeledRationalFunction<C>.minus(other: Int): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun LabeledRationalFunction<C>.times(other: Int): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Integer-Rational relation
    /**
     * Returns sum of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            this * other.numerator,
            other.denominator
        )
    // endregion

    // region Constant-rational relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun C.plus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the constant represented as polynomial and the rational function.
     */
    public override operator fun C.minus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the constant represented as polynomial and the rational function.
     */
    public override operator fun C.times(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            this * other.numerator,
            other.denominator
        )
    // endregion

    // region Rational-constant relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.plus(other: C): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the constant represented as rational function and the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.minus(other: C): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the constant represented as rational function and the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.times(other: C): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Polynomial-rational relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun LabeledPolynomial<C>.plus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the polynomial represented as polynomial and the rational function.
     */
    public override operator fun LabeledPolynomial<C>.minus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the polynomial represented as polynomial and the rational function.
     */
    public override operator fun LabeledPolynomial<C>.times(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            this * other.numerator,
            other.denominator
        )
    // endregion

    // region Rational-polynomial relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.plus(other: LabeledPolynomial<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the polynomial represented as rational function and the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.minus(other: LabeledPolynomial<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the polynomial represented as rational function and the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.times(other: LabeledPolynomial<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Rational-rational relation
    /**
     * Returns negation of the rational function.
     */
    public override operator fun LabeledRationalFunction<C>.unaryMinus(): LabeledRationalFunction<C> = LabeledRationalFunction(-numerator, denominator)
    /**
     * Returns sum of the rational functions.
     */
    public override operator fun LabeledRationalFunction<C>.plus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns difference of the rational functions.
     */
    public override operator fun LabeledRationalFunction<C>.minus(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns product of the rational functions.
     */
    public override operator fun LabeledRationalFunction<C>.times(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other.numerator,
            denominator * other.denominator
        )

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: LabeledRationalFunction<C> = LabeledRationalFunction(polynomialZero, polynomialOne)
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: LabeledRationalFunction<C> = LabeledRationalFunction(polynomialOne, polynomialOne)

    /**
     * Checks equality of the rational functions.
     */
    public override infix fun LabeledRationalFunction<C>.equalsTo(other: LabeledRationalFunction<C>): Boolean {
        if (this === other) return true

        if ( !(numerator.isZero() xor other.numerator.isZero()) ) return false

        val variables = this.variables union other.variables
        val thisNumeratorDegrees = this.numerator.degrees
        val thisDenominatorDegrees = this.denominator.degrees
        val otherNumeratorDegrees = other.numerator.degrees
        val otherDenominatorDegrees = other.denominator.degrees
        for (variable in variables)
            if (
                thisNumeratorDegrees.getOrElse(variable) { 0u } + otherDenominatorDegrees.getOrElse(variable) { 0u }
                != thisDenominatorDegrees.getOrElse(variable) { 0u } + otherNumeratorDegrees.getOrElse(variable) { 0u }
            ) return false

        return numerator * other.denominator equalsTo other.numerator * denominator
    }
    // endregion

    // region Polynomial properties
    /**
     * Map that associates variables (that appear in the polynomial in positive exponents) with their most exponents
     * in which they are appeared in the polynomial.
     *
     * As consequence all values in the map are positive integers. Also, if the polynomial is constant, the map is empty.
     * And keys of the map is the same as in [variables].
     */
    public val LabeledPolynomial<C>.degrees: Map<Variable, UInt> get() = polynomialRing { degrees }
    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledPolynomial<C>.variables: Set<Variable> get() = polynomialRing { variables }
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledPolynomial<C>.countOfVariables: Int get() = polynomialRing { countOfVariables }
    // endregion

    // region Rational properties
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledRationalFunction<C>.variables: Set<Variable>
        get() = numerator.variables union denominator.variables
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledRationalFunction<C>.countOfVariables: Int get() = variables.size
    // endregion

    // region REST TODO: Разобрать

    public operator fun LabeledRationalFunction<C>.div(other: LabeledRationalFunction<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )

    public operator fun LabeledRationalFunction<C>.div(other: LabeledPolynomial<C>): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator,
            denominator * other
        )

    public operator fun LabeledRationalFunction<C>.div(other: C): LabeledRationalFunction<C> =
        LabeledRationalFunction(
            numerator,
            denominator * other
        )

//    operator fun invoke(arg: Map<Variable, C>): LabeledRationalFunction<C> =
//        LabeledRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokeLabeledPolynomial")
//    operator fun invoke(arg: Map<Variable, LabeledPolynomial<C>>): LabeledRationalFunction<C> =
//        LabeledRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokeLabeledRationalFunction")
//    operator fun invoke(arg: Map<Variable, LabeledRationalFunction<C>>): LabeledRationalFunction<C> {
//        var num = numerator invokeRFTakeNumerator arg
//        var den = denominator invokeRFTakeNumerator arg
//        for (variable in variables) if (variable in arg) {
//            val degreeDif = degrees[variable]!!
//            if (degreeDif > 0)
//                den = multiplyByPower(den, arg[variable]!!.denominator, degreeDif)
//            else
//                num = multiplyByPower(num, arg[variable]!!.denominator, -degreeDif)
//        }
//        return LabeledRationalFunction(num, den)
//    }
//
//    override fun toString(): String = toString(emptyMap())
//
//    fun toString(names: Map<Variable, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(names)
//            else -> "${numerator.toStringWithBrackets(names)}/${denominator.toStringWithBrackets(names)}"
//        }
//
//    fun toString(namer: (Variable) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(namer)
//            else -> "${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)}"
//        }
//
//    fun toStringWithBrackets(names: Map<Variable, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(names)
//            else -> "(${numerator.toStringWithBrackets(names)}/${denominator.toStringWithBrackets(names)})"
//        }
//
//    fun toStringWithBrackets(namer: (Variable) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(namer)
//            else -> "(${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)})"
//        }
//
//    fun toReversedString(names: Map<Variable, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(names)
//            else -> "${numerator.toReversedStringWithBrackets(names)}/${denominator.toReversedStringWithBrackets(names)}"
//        }
//
//    fun toReversedString(namer: (Variable) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(namer)
//            else -> "${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)}"
//        }
//
//    fun toReversedStringWithBrackets(names: Map<Variable, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(names)
//            else -> "(${numerator.toReversedStringWithBrackets(names)}/${denominator.toReversedStringWithBrackets(names)})"
//        }
//
//    fun toReversedStringWithBrackets(namer: (Variable) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(namer)
//            else -> "(${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)})"
//        }
}