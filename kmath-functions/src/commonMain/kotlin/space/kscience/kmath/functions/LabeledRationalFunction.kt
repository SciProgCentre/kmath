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
) : AbstractRationalFunction<C, LabeledPolynomial<C>> {
    override fun toString(): String = "LabeledRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

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
//constructor(numeratorCoefficients: Map<Map<Symbol, Int>, C>, denominatorCoefficients: Map<Map<Symbol, Int>, C>) : this(
//LabeledPolynomial(numeratorCoefficients),
//LabeledPolynomial(denominatorCoefficients)
//)
//
//constructor(numeratorCoefficients: Collection<Pair<Map<Symbol, Int>, C>>, denominatorCoefficients: Collection<Pair<Map<Symbol, Int>, C>>) : this(
//LabeledPolynomial(numeratorCoefficients),
//LabeledPolynomial(denominatorCoefficients)
//)
//
//constructor(numerator: LabeledPolynomial<C>) : this(numerator, numerator.getOne())
//constructor(numeratorCoefficients: Map<Map<Symbol, Int>, C>) : this(
//LabeledPolynomial(numeratorCoefficients)
//)
//
//constructor(numeratorCoefficients: Collection<Pair<Map<Symbol, Int>, C>>) : this(
//LabeledPolynomial(numeratorCoefficients)
//)

public class LabeledRationalFunctionSpace<C, A: Ring<C>>(
    public val ring: A,
) :
    AbstractRationalFunctionalSpaceOverPolynomialSpace<
            C,
            LabeledPolynomial<C>,
            LabeledRationalFunction<C>,
            LabeledPolynomialSpace<C, A>,
            >,
    PolynomialSpaceOfFractions<
            C,
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

    /**
     * Map that associates variables (that appear in the polynomial in positive exponents) with their most exponents
     * in which they are appeared in the polynomial.
     *
     * As consequence all values in the map are positive integers. Also, if the polynomial is constant, the map is empty.
     * And keys of the map is the same as in [variables].
     */
    public val LabeledPolynomial<C>.degrees: Map<Symbol, UInt> get() = polynomialRing { degrees }
    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledPolynomial<C>.variables: Set<Symbol> get() = polynomialRing { variables }
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledPolynomial<C>.countOfVariables: Int get() = polynomialRing { countOfVariables }

    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledRationalFunction<C>.variables: Set<Symbol>
        get() = numerator.variables union denominator.variables
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val LabeledRationalFunction<C>.countOfVariables: Int get() = variables.size

    // TODO: Разобрать

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