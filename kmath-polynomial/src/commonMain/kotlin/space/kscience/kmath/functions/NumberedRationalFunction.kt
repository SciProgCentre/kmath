/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.jvm.JvmName
import kotlin.math.max


/**
 * Represents multivariate rational function that stores its numerator and denominator as [NumberedPolynomial]s.
 */
public data class NumberedRationalFunction<C>(
    public override val numerator: NumberedPolynomial<C>,
    public override val denominator: NumberedPolynomial<C>
) : RationalFunction<C, NumberedPolynomial<C>> {
    override fun toString(): String = "NumberedRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

/**
 * Arithmetic context for univariate rational functions with numerator and denominator represented as [NumberedPolynomial]s.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class NumberedRationalFunctionSpace<C, A: Ring<C>> (
    public val ring: A,
) :
    RationalFunctionSpaceOverPolynomialSpace<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            NumberedPolynomialSpace<C, A>,
            >,
    PolynomialSpaceOfFractions<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            >() {

    /**
     * Underlying polynomial ring. Its polynomial operations are inherited by local polynomial operations.
     */
    public override val polynomialRing : NumberedPolynomialSpace<C, A> = NumberedPolynomialSpace(ring)
    /**
     * Constructor of rational functions (of type [NumberedRationalFunction]) from numerator and denominator (of type [NumberedPolynomial]).
     */
    protected override fun constructRationalFunction(
        numerator: NumberedPolynomial<C>,
        denominator: NumberedPolynomial<C>
    ): NumberedRationalFunction<C> =
        NumberedRationalFunction(numerator, denominator)

    /**
     * Maximal index (ID) of variable occurring in the polynomial with positive power. If there is no such variable,
     * the result is `-1`.
     */
    public val NumberedPolynomial<C>.lastVariable: Int get() = polynomialRing { lastVariable }
    /**
     * List that associates indices of variables (that appear in the polynomial in positive exponents) with their most
     * exponents in which the variables are appeared in the polynomial.
     *
     * As consequence all values in the list are non-negative integers. Also, if the polynomial is constant, the list is empty.
     * And last index of the list is [lastVariable].
     */
    public val NumberedPolynomial<C>.degrees: List<UInt> get() = polynomialRing { degrees }
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public fun NumberedPolynomial<C>.degreeBy(variable: Int): UInt = polynomialRing { degreeBy(variable) }
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public fun NumberedPolynomial<C>.degreeBy(variables: Collection<Int>): UInt = polynomialRing { degreeBy(variables) }
    /**
     * Count of variables occurring in the polynomial with positive power. If there is no such variable,
     * the result is `0`.
     */
    public val NumberedPolynomial<C>.countOfVariables: Int get() = polynomialRing { countOfVariables }

    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val NumberedRationalFunction<C>.lastVariable: Int
        get() = polynomialRing { max(numerator.lastVariable, denominator.lastVariable) }
    /**
     * Count of variables occurring in the rational function with positive power. If there is no such variable,
     * the result is `0`.
     */
    public val NumberedRationalFunction<C>.countOfVariables: Int
        get() =
            MutableList(lastVariable + 1) { false }.apply {
                numerator.coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
                denominator.coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
            }.count { it }

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Substitutes provided constant [argument] into [this] polynomial.
     */
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, C>): NumberedPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] polynomial.
     */
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] polynomial.
     */
    @JvmName("substituteRationalFunction")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided constant [argument] into [this] rational function.
     */
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, C>): NumberedRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] rational function.
     */
    @JvmName("substitutePolynomial")
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] rational function.
     */
    @JvmName("substituteRationalFunction")
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided constant [argument] into [this] polynomial.
     */
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<C>): NumberedPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] polynomial.
     */
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] polynomial.
     */
    @JvmName("substituteRationalFunction")
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided constant [argument] into [this] rational function.
     */
    public inline fun NumberedRationalFunction<C>.substitute(argument: Buffer<C>): NumberedRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [arguments] into [this] rational function.
     */
    @JvmName("substitutePolynomial")
    public inline fun NumberedRationalFunction<C>.substitute(arguments: Buffer<NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    /**
     * Substitutes provided rational function [arguments] into [this] rational function.
     */
    @JvmName("substituteRationalFunction")
    public inline fun NumberedRationalFunction<C>.substitute(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    /**
     * Substitutes provided constant [arguments] into [this] polynomial.
     */
    public inline fun NumberedPolynomial<C>.substituteFully(arguments: Buffer<C>): C = substituteFully(ring, arguments)

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
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun NumberedPolynomial<C>.asFunctionOfRationalFunction(): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = asFunctionOfRationalFunctionOver(ring)
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun NumberedRationalFunction<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedRationalFunction<C> = asFunctionOfPolynomialOver(ring)
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun NumberedRationalFunction<C>.asFunctionOfRationalFunction(): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = asFunctionOfRationalFunctionOver(ring)

    /**
     * Evaluates value of [this] polynomial on provided [arguments].
     */
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<C>): C = substituteFully(ring, arguments)
    /**
     * Substitutes provided [arguments] into [this] polynomial.
     */
    @JvmName("invokePolynomial")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, arguments)
    /**
     * Substitutes provided [arguments] into [this] polynomial.
     */
    @JvmName("invokeRationalFunction")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    /**
     * Substitutes provided [arguments] into [this] rational function.
     */
    @JvmName("invokePolynomial")
    public inline operator fun NumberedRationalFunction<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    /**
     * Substitutes provided [arguments] into [this] rational function.
     */
    @JvmName("invokeRationalFunction")
    public inline operator fun NumberedRationalFunction<C>.invoke(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
}