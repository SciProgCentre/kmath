/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring


/**
 * Represents univariate rational function that stores its numerator and denominator as [ListPolynomial]s.
 */
public data class ListRationalFunction<C>(
    public override val numerator: ListPolynomial<C>,
    public override val denominator: ListPolynomial<C>
) : RationalFunction<C, ListPolynomial<C>> {
    override fun toString(): String = "ListRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

/**
 * Arithmetic context for univariate rational functions with numerator and denominator represented as [ListPolynomial]s.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class ListRationalFunctionSpace<C, A : Ring<C>> (
    public val ring: A,
) :
    RationalFunctionSpaceOverPolynomialSpace<
            C,
            ListPolynomial<C>,
            ListRationalFunction<C>,
            ListPolynomialSpace<C, A>,
            >,
    PolynomialSpaceOfFractions<
            C,
            ListPolynomial<C>,
            ListRationalFunction<C>,
            >() {

    /**
     * Underlying polynomial ring. Its polynomial operations are inherited by local polynomial operations.
     */
    override val polynomialRing : ListPolynomialSpace<C, A> = ListPolynomialSpace(ring)
    /**
     * Constructor of [ListRationalFunction] from numerator and denominator [ListPolynomial].
     */
    override fun constructRationalFunction(numerator: ListPolynomial<C>, denominator: ListPolynomial<C>): ListRationalFunction<C> =
        ListRationalFunction(numerator, denominator)

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Evaluates value of [this] polynomial on provided argument.
     */
    public inline fun ListPolynomial<C>.substitute(argument: C): C = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] polynomial.
     */
    public inline fun ListPolynomial<C>.substitute(argument: ListPolynomial<C>): ListPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] polynomial.
     */
    public inline fun ListPolynomial<C>.substitute(argument: ListRationalFunction<C>): ListRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] rational function.
     */
    public inline fun ListRationalFunction<C>.substitute(argument: ListPolynomial<C>): ListRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] rational function.
     */
    public inline fun ListRationalFunction<C>.substitute(argument: ListRationalFunction<C>): ListRationalFunction<C> = substitute(ring, argument)

    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunction(): (C) -> C = { substitute(ring, it) }
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunctionOfConstant(): (C) -> C = { substitute(ring, it) }
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListPolynomial<C> = { substitute(ring, it) }
    /**
     * Represent [this] polynomial as a regular context-less function.
     */
    public inline fun ListPolynomial<C>.asFunctionOfRationalFunction(): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(ring, it) }
    /**
     * Represent [this] rational function as a regular context-less function.
     */
    public inline fun ListRationalFunction<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListRationalFunction<C> = { substitute(ring, it) }
    /**
     * Represent [this] rational function as a regular context-less function.
     */
    public inline fun ListRationalFunction<C>.asFunctionOfRationalFunction(): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

    /**
     * Evaluates value of [this] polynomial on provided argument.
     */
    public inline operator fun ListPolynomial<C>.invoke(argument: C): C = substitute(ring, argument)
    /**
     * Evaluates value of [this] polynomial on provided argument.
     */
    public inline operator fun ListPolynomial<C>.invoke(argument: ListPolynomial<C>): ListPolynomial<C> = substitute(ring, argument)
    /**
     * Evaluates value of [this] polynomial on provided argument.
     */
    public inline operator fun ListPolynomial<C>.invoke(argument: ListRationalFunction<C>): ListRationalFunction<C> = substitute(ring, argument)
    /**
     * Evaluates value of [this] rational function on provided argument.
     */
    public inline operator fun ListRationalFunction<C>.invoke(argument: ListPolynomial<C>): ListRationalFunction<C> = substitute(ring, argument)
    /**
     * Evaluates value of [this] rational function on provided argument.
     */
    public inline operator fun ListRationalFunction<C>.invoke(argument: ListRationalFunction<C>): ListRationalFunction<C> = substitute(ring, argument)
}