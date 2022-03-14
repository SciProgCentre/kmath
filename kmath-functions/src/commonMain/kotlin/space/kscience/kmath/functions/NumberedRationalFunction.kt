/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.math.max


public class NumberedRationalFunction<C> internal constructor(
    public override val numerator: NumberedPolynomial<C>,
    public override val denominator: NumberedPolynomial<C>
) : AbstractRationalFunction<C, NumberedPolynomial<C>> {
    override fun toString(): String = "NumberedRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

// region Internal utilities

/**
 * Represents internal [NumberedRationalFunction] errors.
 */
internal class NumberedRationalFunctionError : Error {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

/**
 * Throws an [NumberedRationalFunctionError] with the given [message].
 */
internal fun numberedRationalFunctionError(message: Any): Nothing = throw NumberedRationalFunctionError(message.toString())

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
//constructor(numeratorCoefficients: Map<List<Int>, C>, denominatorCoefficients: Map<List<Int>, C>) : this(
//Polynomial(numeratorCoefficients),
//Polynomial(denominatorCoefficients)
//)
//constructor(numeratorCoefficients: Collection<Pair<List<Int>, C>>, denominatorCoefficients: Collection<Pair<List<Int>, C>>) : this(
//Polynomial(numeratorCoefficients),
//Polynomial(denominatorCoefficients)
//)
//constructor(numerator: Polynomial<C>) : this(numerator, numerator.getOne())
//constructor(numeratorCoefficients: Map<List<Int>, C>) : this(
//Polynomial(numeratorCoefficients)
//)
//constructor(numeratorCoefficients: Collection<Pair<List<Int>, C>>) : this(
//Polynomial(numeratorCoefficients)
//)

// endregion

public class NumberedRationalFunctionSpace<C, A: Ring<C>> (
    public val ring: A,
) : AbstractRationalFunctionalSpaceOverPolynomialSpace<C, NumberedPolynomial<C>, NumberedRationalFunction<C>, A> {

    override val polynomialRing : NumberedPolynomialSpace<C, A> = NumberedPolynomialSpace(ring)

    // region Rational-integer relation
    /**
     * Returns sum of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun NumberedRationalFunction<C>.plus(other: Int): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun NumberedRationalFunction<C>.minus(other: Int): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun NumberedRationalFunction<C>.times(other: Int): NumberedRationalFunction<C> =
        NumberedRationalFunction(
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
    public override operator fun Int.plus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            this * other.numerator,
            other.denominator
        )
    // endregion

    // region Constant-rational relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun C.plus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the constant represented as polynomial and the rational function.
     */
    public override operator fun C.minus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the constant represented as polynomial and the rational function.
     */
    public override operator fun C.times(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            this * other.numerator,
            other.denominator
        )
    // endregion

    // region Rational-constant relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.plus(other: C): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the constant represented as rational function and the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.minus(other: C): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the constant represented as rational function and the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.times(other: C): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Polynomial-rational relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun NumberedPolynomial<C>.plus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the polynomial represented as polynomial and the rational function.
     */
    public override operator fun NumberedPolynomial<C>.minus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the polynomial represented as polynomial and the rational function.
     */
    public override operator fun NumberedPolynomial<C>.times(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            this * other.numerator,
            other.denominator
        )
    // endregion

    // region Rational-polynomial relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.plus(other: NumberedPolynomial<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the polynomial represented as rational function and the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.minus(other: NumberedPolynomial<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the polynomial represented as rational function and the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.times(other: NumberedPolynomial<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Rational-rational relation
    /**
     * Returns negation of the rational function.
     */
    public override operator fun NumberedRationalFunction<C>.unaryMinus(): NumberedRationalFunction<C> = NumberedRationalFunction(-numerator, denominator)
    /**
     * Returns sum of the rational functions.
     */
    public override operator fun NumberedRationalFunction<C>.plus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns difference of the rational functions.
     */
    public override operator fun NumberedRationalFunction<C>.minus(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns product of the rational functions.
     */
    public override operator fun NumberedRationalFunction<C>.times(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other.numerator,
            denominator * other.denominator
        )

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: NumberedRationalFunction<C> = NumberedRationalFunction(polynomialZero, polynomialOne)
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: NumberedRationalFunction<C> = NumberedRationalFunction(polynomialOne, polynomialOne)

    /**
     * Checks equality of the rational functions.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER", "CovariantEquals")
    public override infix fun NumberedRationalFunction<C>.equalsTo(other: NumberedRationalFunction<C>): Boolean {
        if (this === other) return true

        if ( !(numerator.isZero() xor other.numerator.isZero()) ) return false

        val countOfVariables = max(this.countOfVariables, other.countOfVariables)
        val thisNumeratorDegrees = this.numerator.degrees
        val thisDenominatorDegrees = this.denominator.degrees
        val otherNumeratorDegrees = other.numerator.degrees
        val otherDenominatorDegrees = other.denominator.degrees
        for (variable in 0 until countOfVariables)
            if (
                thisNumeratorDegrees.getOrElse(variable) { 0u } + otherDenominatorDegrees.getOrElse(variable) { 0u }
                != thisDenominatorDegrees.getOrElse(variable) { 0u } + otherNumeratorDegrees.getOrElse(variable) { 0u }
            ) return false

        return numerator * other.denominator equalsTo other.numerator * denominator
    }
    // endregion

    // region Polynomial properties
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val NumberedPolynomial<C>.countOfVariables: Int get() = polynomialRing { countOfVariables }
    /**
     * List that associates indices of variables (that appear in the polynomial in positive exponents) with their most
     * exponents in which the variables are appeared in the polynomial.
     *
     * As consequence all values in the list are non-negative integers. Also, if the polynomial is constant, the list is empty.
     * And size of the list is [countOfVariables].
     */
    public val NumberedPolynomial<C>.degrees: List<UInt> get() = polynomialRing { degrees }
    // endregion

    // region Rational properties
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val NumberedRationalFunction<C>.countOfVariables: Int
        get() = polynomialRing { max(numerator.countOfVariables, denominator.countOfVariables) }
    // endregion

    // region REST TODO: Разобрать

    public operator fun NumberedRationalFunction<C>.div(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )

    public operator fun NumberedRationalFunction<C>.div(other: NumberedPolynomial<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator,
            denominator * other
        )

    public operator fun NumberedRationalFunction<C>.div(other: C): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator,
            denominator * other
        )

//    operator fun invoke(arg: Map<Int, C>): NumberedRationalFunction<C> =
//        NumberedRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokePolynomial")
//    operator fun invoke(arg: Map<Int, Polynomial<C>>): NumberedRationalFunction<C> =
//        NumberedRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokeRationalFunction")
//    operator fun invoke(arg: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> {
//        var num = numerator invokeRFTakeNumerator arg
//        var den = denominator invokeRFTakeNumerator arg
//        for (variable in 0 until max(numerator.countOfVariables, denominator.countOfVariables)) if (variable in arg) {
//            val degreeDif = numerator.degrees.getOrElse(variable) { 0 } - denominator.degrees.getOrElse(variable) { 0 }
//            if (degreeDif > 0)
//                den = multiplyByPower(den, arg[variable]!!.denominator, degreeDif)
//            else
//                num = multiplyByPower(num, arg[variable]!!.denominator, -degreeDif)
//        }
//        return NumberedRationalFunction(num, den)
//    }
//
//    override fun toString(): String = toString(Polynomial.variableName)
//
//    fun toString(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(withVariableName)
//            else -> "${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)}"
//        }
//
//    fun toString(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(namer)
//            else -> "${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)}"
//        }
//
//    fun toStringWithBrackets(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(withVariableName)
//            else -> "(${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)})"
//        }
//
//    fun toStringWithBrackets(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(namer)
//            else -> "(${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)})"
//        }
//
//    fun toReversedString(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(withVariableName)
//            else -> "${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)}"
//        }
//
//    fun toReversedString(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(namer)
//            else -> "${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)}"
//        }
//
//    fun toReversedStringWithBrackets(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(withVariableName)
//            else -> "(${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)})"
//        }
//
//    fun toReversedStringWithBrackets(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(namer)
//            else -> "(${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)})"
//        }
}