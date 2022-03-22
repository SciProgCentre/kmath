/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke


public class LabeledRationalFunction<C>(
    public override val numerator: LabeledPolynomial<C>,
    public override val denominator: LabeledPolynomial<C>
) : RationalFunction<C, LabeledPolynomial<C>> {
    override fun toString(): String = "LabeledRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

@Suppress("FunctionName")
internal fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numerator: LabeledPolynomial<C>, denominator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    if (denominator.isZero()) throw ArithmeticException("/ by zero")
    else LabeledRationalFunction<C>(numerator, denominator)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.LabeledRationalFunction(numerator: LabeledPolynomial<C>, denominator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    if (denominator.coefficients.values.all { it == zero }) throw ArithmeticException("/ by zero")
    else LabeledRationalFunction<C>(numerator, denominator)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>, denominatorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    if (denominatorCoefficients.values.all { it == zero }) throw ArithmeticException("/ by zero")
    else LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomial(denominatorCoefficients)
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>, denominatorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    if (denominatorCoefficients.values.all { it == zero }) throw ArithmeticException("/ by zero")
    else LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomial(denominatorCoefficients)
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, polynomialOne)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, LabeledPolynomial(mapOf(emptyMap<Symbol, UInt>() to one)))
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        polynomialOne
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomial(mapOf(emptyMap<Symbol, UInt>() to one))
    )

public class LabeledRationalFunctionSpace<C, A: Ring<C>>(
    public val ring: A,
) :
    MultivariateRationalFunctionalSpaceOverMultivariatePolynomialSpace<
            C,
            Symbol,
            LabeledPolynomial<C>,
            LabeledRationalFunction<C>,
            LabeledPolynomialSpace<C, A>,
            >,
    MultivariatePolynomialSpaceOfFractions<
            C,
            Symbol,
            LabeledPolynomial<C>,
            LabeledRationalFunction<C>,
            >() {

    override val polynomialRing : LabeledPolynomialSpace<C, A> = LabeledPolynomialSpace(ring)
    override fun constructRationalFunction(
        numerator: LabeledPolynomial<C>,
        denominator: LabeledPolynomial<C>
    ): LabeledRationalFunction<C> =
        LabeledRationalFunction(numerator, denominator)

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

        if (numerator.isZero() != other.numerator.isZero()) return false

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

    // TODO: Разобрать

//    operator fun invoke(arg: Map<Symbol, C>): LabeledRationalFunction<C> =
//        LabeledRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokeLabeledPolynomial")
//    operator fun invoke(arg: Map<Symbol, LabeledPolynomial<C>>): LabeledRationalFunction<C> =
//        LabeledRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokeLabeledRationalFunction")
//    operator fun invoke(arg: Map<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> {
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
//    fun toString(names: Map<Symbol, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(names)
//            else -> "${numerator.toStringWithBrackets(names)}/${denominator.toStringWithBrackets(names)}"
//        }
//
//    fun toString(namer: (Symbol) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(namer)
//            else -> "${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)}"
//        }
//
//    fun toStringWithBrackets(names: Map<Symbol, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(names)
//            else -> "(${numerator.toStringWithBrackets(names)}/${denominator.toStringWithBrackets(names)})"
//        }
//
//    fun toStringWithBrackets(namer: (Symbol) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(namer)
//            else -> "(${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)})"
//        }
//
//    fun toReversedString(names: Map<Symbol, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(names)
//            else -> "${numerator.toReversedStringWithBrackets(names)}/${denominator.toReversedStringWithBrackets(names)}"
//        }
//
//    fun toReversedString(namer: (Symbol) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(namer)
//            else -> "${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)}"
//        }
//
//    fun toReversedStringWithBrackets(names: Map<Symbol, String> = emptyMap()): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(names)
//            else -> "(${numerator.toReversedStringWithBrackets(names)}/${denominator.toReversedStringWithBrackets(names)})"
//        }
//
//    fun toReversedStringWithBrackets(namer: (Symbol) -> String): String =
//        when (true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(namer)
//            else -> "(${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)})"
//        }
}