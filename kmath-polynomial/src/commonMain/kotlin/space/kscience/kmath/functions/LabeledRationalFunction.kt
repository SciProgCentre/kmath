/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Ring
import kotlin.jvm.JvmName


/**
 * Represents multivariate rational function that stores its numerator and denominator as [LabeledPolynomial]s.
 */
public data class LabeledRationalFunction<C>(
    public override val numerator: LabeledPolynomial<C>,
    public override val denominator: LabeledPolynomial<C>
) : RationalFunction<C, LabeledPolynomial<C>> {
    override fun toString(): String = "LabeledRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

/**
 * Arithmetic context for univariate rational functions with numerator and denominator represented as [LabeledPolynomial]s.
 *
 * @param C the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class LabeledRationalFunctionSpace<C, A: Ring<C>>(
    public val ring: A,
) :
    MultivariateRationalFunctionSpaceOverMultivariatePolynomialSpace<
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

    /**
     * Underlying polynomial ring. Its polynomial operations are inherited by local polynomial operations.
     */
    override val polynomialRing : LabeledPolynomialSpace<C, A> = LabeledPolynomialSpace(ring)
    /**
     * Constructor of rational functions (of type [LabeledRationalFunction]) from numerator and denominator (of type [LabeledPolynomial]).
     */
    override fun constructRationalFunction(
        numerator: LabeledPolynomial<C>,
        denominator: LabeledPolynomial<C>
    ): LabeledRationalFunction<C> =
        LabeledRationalFunction<C>(numerator, denominator)

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    /**
     * Substitutes provided constant [argument] into [this] polynomial.
     */
    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Symbol, C>): LabeledPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] polynomial.
     */
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Symbol, LabeledPolynomial<C>>): LabeledPolynomial<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] polynomial.
     */
    @JvmName("substituteRationalFunction")
    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided constant [argument] into [this] rational function.
     */
    public inline fun LabeledRationalFunction<C>.substitute(argument: Map<Symbol, C>): LabeledRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided polynomial [argument] into [this] rational function.
     */
    @JvmName("substitutePolynomial")
    public inline fun LabeledRationalFunction<C>.substitute(argument: Map<Symbol, LabeledPolynomial<C>>): LabeledRationalFunction<C> = substitute(ring, argument)
    /**
     * Substitutes provided rational function [argument] into [this] rational function.
     */
    @JvmName("substituteRationalFunction")
    public inline fun LabeledRationalFunction<C>.substitute(argument: Map<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> = substitute(ring, argument)
}