/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.js.JsName
import kotlin.jvm.JvmName


/**
 * Abstraction of rational function.
 */
public interface AbstractRationalFunction<C, P: AbstractPolynomial<C>> {
    public val numerator: P
    public val denominator: P
    public operator fun component1(): P = numerator
    public operator fun component2(): P = denominator
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public interface AbstractRationalFunctionalSpace<C, P: AbstractPolynomial<C>, R: AbstractRationalFunction<C, P>> : Ring<R> {
    // region Constant-integer relation
    /**
     * Returns sum of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    @JvmName("constantIntPlus")
    public operator fun C.plus(other: Int): C
    /**
     * Returns difference between the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    @JvmName("constantIntMinus")
    public operator fun C.minus(other: Int): C
    /**
     * Returns product of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("constantIntTimes")
    public operator fun C.times(other: Int): C
    // endregion

    // region Integer-constant relation
    /**
     * Returns sum of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    @JvmName("intConstantPlus")
    public operator fun Int.plus(other: C): C
    /**
     * Returns difference between the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    @JvmName("intConstantMinus")
    public operator fun Int.minus(other: C): C
    /**
     * Returns product of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("intConstantTimes")
    public operator fun Int.times(other: C): C
    // endregion

    // region Polynomial-integer relation
    /**
     * Returns sum of the constant and the integer represented as polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public operator fun P.plus(other: Int): P
    /**
     * Returns difference between the constant and the integer represented as polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public operator fun P.minus(other: Int): P
    /**
     * Returns product of the constant and the integer represented as polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun P.times(other: Int): P
    // endregion

    // region Integer-polynomial relation
    /**
     * Returns sum of the integer represented as polynomial and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public operator fun Int.plus(other: P): P
    /**
     * Returns difference between the integer represented as polynomial and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public operator fun Int.minus(other: P): P
    /**
     * Returns product of the integer represented as polynomial and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public operator fun Int.times(other: P): P
    // endregion

    // region Rational-integer relation
    /**
     * Returns sum of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public operator fun R.plus(other: Int): R = optimizedAddMultiplied(this, one, other)
    /**
     * Returns difference between the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public operator fun R.minus(other: Int): R = optimizedAddMultiplied(this, one, -other)
    /**
     * Returns product of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun R.times(other: Int): R = optimizedMultiply(this, other)
    // endregion

    // region Integer-Rational relation
    /**
     * Returns sum of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public operator fun Int.plus(other: R): R = optimizedAddMultiplied(other, one, this)
    /**
     * Returns difference between the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public operator fun Int.minus(other: R): R = optimizedAddMultiplied(-other, one, this)
    /**
     * Returns product of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public operator fun Int.times(other: R): R = optimizedMultiply(other, this)
    // endregion

    // region Constant-constant relation
    /**
     * Returns the same constant.
     */
    @JvmName("constantUnaryPlus")
    @JsName("constantUnaryPlus")
    public operator fun C.unaryPlus(): C = this
    /**
     * Returns negation of the constant.
     */
    @JvmName("constantUnaryMinus")
    @JsName("constantUnaryMinus")
    public operator fun C.unaryMinus(): C
    /**
     * Returns sum of the constants.
     */
    @JvmName("constantPlus")
    @JsName("constantPlus")
    public operator fun C.plus(other: C): C
    /**
     * Returns difference of the constants.
     */
    @JvmName("constantMinus")
    @JsName("constantMinus")
    public operator fun C.minus(other: C): C
    /**
     * Returns product of the constants.
     */
    @JvmName("constantTimes")
    @JsName("constantTimes")
    public operator fun C.times(other: C): C

    /**
     * Check if the instant is zero constant.
     */
    @JvmName("constantIsZero")
    public fun C.isZero(): Boolean = this == constantZero
    /**
     * Check if the instant is NOT zero constant.
     */
    @JvmName("constantIsNotZero")
    public fun C.isNotZero(): Boolean = !isZero()
    /**
     * Check if the instant is unit constant.
     */
    @JvmName("constantIsOne")
    public fun C.isOne(): Boolean =  this == constantOne
    /**
     * Check if the instant is NOT unit constant.
     */
    @JvmName("constantIsNotOne")
    public fun C.isNotOne(): Boolean = !isOne()
    /**
     * Check if the instant is minus unit constant.
     */
    @JvmName("constantIsMinusOne")
    public fun C.isMinusOne(): Boolean =  this == -constantOne
    /**
     * Check if the instant is NOT minus unit constant.
     */
    @JvmName("constantIsNotMinusOne")
    public fun C.isNotMinusOne(): Boolean = !isMinusOne()

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public val constantZero: C
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public val constantOne: C
    // endregion

    // region Constant-polynomial relation
    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    public operator fun C.plus(other: P): P
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    public operator fun C.minus(other: P): P
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    public operator fun C.times(other: P): P
    // endregion

    // region Polynomial-constant relation
    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    public operator fun P.plus(other: C): P
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    public operator fun P.minus(other: C): P
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    public operator fun P.times(other: C): P
    // endregion

    // region Polynomial-polynomial relation
    /**
     * Returns the same polynomial.
     */
    public operator fun P.unaryPlus(): P = this
    /**
     * Returns negation of the polynomial.
     */
    public operator fun P.unaryMinus(): P
    /**
     * Returns sum of the polynomials.
     */
    public operator fun P.plus(other: P): P
    /**
     * Returns difference of the polynomials.
     */
    public operator fun P.minus(other: P): P
    /**
     * Returns product of the polynomials.
     */
    public operator fun P.times(other: P): P

    /**
     * Check if the instant is zero polynomial.
     */
    public fun P.isZero(): Boolean = this equalsTo polynomialZero
    /**
     * Check if the instant is NOT zero polynomial.
     */
    public fun P.isNotZero(): Boolean = !isZero()
    /**
     * Check if the instant is unit polynomial.
     */
    public fun P.isOne(): Boolean = this equalsTo polynomialOne
    /**
     * Check if the instant is NOT unit polynomial.
     */
    public fun P.isNotOne(): Boolean = !isOne()
    /**
     * Check if the instant is minus unit polynomial.
     */
    public fun P.isMinusOne(): Boolean = this equalsTo -polynomialOne
    /**
     * Check if the instant is NOT minus unit polynomial.
     */
    public fun P.isNotMinusOne(): Boolean = !isMinusOne()

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    public val polynomialZero: P
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    public val polynomialOne: P

    /**
     * Checks equality of the polynomials.
     */
    public infix fun P.equalsTo(other: P): Boolean
    /**
     * Checks NOT equality of the polynomials.
     */
    public infix fun P.notEqualsTo(other: P): Boolean = !(this equalsTo other)
    // endregion

    // region Constant-rational relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public operator fun C.plus(other: R): R
    /**
     * Returns difference between the constant represented as polynomial and the rational function.
     */
    public operator fun C.minus(other: R): R
    /**
     * Returns product of the constant represented as polynomial and the rational function.
     */
    public operator fun C.times(other: R): R
    // endregion

    // region Rational-constant relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public operator fun R.plus(other: C): R
    /**
     * Returns difference between the constant represented as rational function and the rational function.
     */
    public operator fun R.minus(other: C): R
    /**
     * Returns product of the constant represented as rational function and the rational function.
     */
    public operator fun R.times(other: C): R
    // endregion

    // region Polynomial-rational relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public operator fun P.plus(other: R): R
    /**
     * Returns difference between the polynomial represented as polynomial and the rational function.
     */
    public operator fun P.minus(other: R): R
    /**
     * Returns product of the polynomial represented as polynomial and the rational function.
     */
    public operator fun P.times(other: R): R
    // endregion

    // region Rational-polynomial relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public operator fun R.plus(other: P): R
    /**
     * Returns difference between the polynomial represented as rational function and the rational function.
     */
    public operator fun R.minus(other: P): R
    /**
     * Returns product of the polynomial represented as rational function and the rational function.
     */
    public operator fun R.times(other: P): R
    // endregion

    // region Rational-rational relation
    /**
     * Returns the same rational function.
     */
    public override operator fun R.unaryPlus(): R = this
    /**
     * Returns negation of the rational function.
     */
    public override operator fun R.unaryMinus(): R
    /**
     * Returns sum of the rational functions.
     */
    public override operator fun R.plus(other: R): R
    /**
     * Returns difference of the rational functions.
     */
    public override operator fun R.minus(other: R): R
    /**
     * Returns product of the rational functions.
     */
    public override operator fun R.times(other: R): R

    /**
     * Check if the instant is zero rational function.
     */
    public fun R.isZero(): Boolean = numerator equalsTo polynomialZero
    /**
     * Check if the instant is NOT zero rational function.
     */
    public fun R.isNotZero(): Boolean = !isZero()
    /**
     * Check if the instant is unit rational function.
     */
    public fun R.isOne(): Boolean = numerator equalsTo denominator
    /**
     * Check if the instant is NOT unit rational function.
     */
    public fun R.isNotOne(): Boolean = !isOne()
    /**
     * Check if the instant is minus unit rational function.
     */
    public fun R.isMinusOne(): Boolean = (numerator + denominator).isZero()
    /**
     * Check if the instant is NOT minus unit rational function.
     */
    public fun R.isNotMinusOne(): Boolean = !isMinusOne()

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: R
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: R

    /**
     * Checks equality of the rational functions.
     */
    public infix fun R.equalsTo(other: R): Boolean
    /**
     * Checks NOT equality of the polynomials.
     */
    public infix fun R.notEqualsTo(other: R): Boolean = !(this equalsTo other)
    // endregion

    // Not sure is it necessary...
    // region Polynomial properties
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public val P.degree: Int

    /**
     * Checks if the instant is constant polynomial (of degree no more than 0) over considered ring.
     */
    public fun P.isConstant(): Boolean = degree <= 0
    /**
     * Checks if the instant is **not** constant polynomial (of degree no more than 0) over considered ring.
     */
    public fun P.isNotConstant(): Boolean = !isConstant()
    /**
     * Checks if the instant is constant non-zero polynomial (of degree no more than 0) over considered ring.
     */
    public fun P.isNonZeroConstant(): Boolean = degree == 0
    /**
     * Checks if the instant is **not** constant non-zero polynomial (of degree no more than 0) over considered ring.
     */
    public fun P.isNotNonZeroConstant(): Boolean = !isNonZeroConstant()
    /**
     * If polynomial is a constant polynomial represents and returns it as constant.
     * Otherwise, (when the polynomial is not constant polynomial) returns `null`.
     */
    public fun P.asConstantOrNull(): C?
    /**
     * If polynomial is a constant polynomial represents and returns it as constant.
     * Otherwise, (when the polynomial is not constant polynomial) raises corresponding exception.
     */
    public fun P.asConstant(): C = asConstantOrNull() ?: error("Can not represent non-constant polynomial as a constant")

    // endregion

    // Not sure is it necessary...
    // region Rational properties
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public val R.numeratorDegree: Int get() = numerator.degree
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public val R.denominatorDegree: Int get() = denominator.degree

    // TODO: Перенести в реализацию
//    fun R.substitute(argument: C): C
//    fun R.substitute(argument: P): R
//    fun R.substitute(argument: R): R
//
//    fun R.asFunction(): (C) -> C = /*this::substitute*/ { this.substitute(it) }
//    fun R.asFunctionOnConstants(): (C) -> C = /*this::substitute*/ { this.substitute(it) }
//    fun P.asFunctionOnPolynomials(): (P) -> R = /*this::substitute*/ { this.substitute(it) }
//    fun R.asFunctionOnRationalFunctions(): (R) -> R = /*this::substitute*/ { this.substitute(it) }
//
//    operator fun R.invoke(argument: C): C = this.substitute(argument)
//    operator fun R.invoke(argument: P): R = this.substitute(argument)
//    operator fun R.invoke(argument: R): R = this.substitute(argument)
    // endregion

    // region Legacy
    override fun add(left: R, right: R): R = left + right
    override fun multiply(left: R, right: R): R = left * right
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public interface AbstractRationalFunctionalSpaceOverRing<C, P: AbstractPolynomial<C>, R: AbstractRationalFunction<C, P>, A: Ring<C>> : AbstractRationalFunctionalSpace<C, P, R> {

    public val ring: A

    // region Constant-integer relation
    /**
     * Returns sum of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    @JvmName("constantIntPlus")
    public override operator fun C.plus(other: Int): C = ring { optimizedAddMultiplied(this@plus, one, other) }
    /**
     * Returns difference between the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    @JvmName("constantIntMinus")
    public override operator fun C.minus(other: Int): C = ring { optimizedAddMultiplied(this@minus, one, -other) }
    /**
     * Returns product of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("constantIntTimes")
    public override operator fun C.times(other: Int): C = ring { optimizedMultiply(this@times, other) }
    // endregion

    // region Integer-constant relation
    /**
     * Returns sum of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    @JvmName("intConstantPlus")
    public override operator fun Int.plus(other: C): C = ring { optimizedAddMultiplied(other, one, this@plus) }
    /**
     * Returns difference between the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    @JvmName("intConstantMinus")
    public override operator fun Int.minus(other: C): C = ring { optimizedAddMultiplied(-other, one, this@minus) }
    /**
     * Returns product of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("intConstantTimes")
    public override operator fun Int.times(other: C): C = ring { optimizedMultiply(other, this@times) }
    // endregion

    // region Constant-constant relation
    /**
     * Returns the same constant.
     */
    @JvmName("constantUnaryPlus")
    @JsName("constantUnaryPlus")
    public override operator fun C.unaryPlus(): C = ring { +this@unaryPlus }
    /**
     * Returns negation of the constant.
     */
    @JvmName("constantUnaryMinus")
    @JsName("constantUnaryMinus")
    public override operator fun C.unaryMinus(): C = ring { -this@unaryMinus }
    /**
     * Returns sum of the constants.
     */
    @JvmName("constantPlus")
    @JsName("constantPlus")
    public override operator fun C.plus(other: C): C = ring { this@plus + other }
    /**
     * Returns difference of the constants.
     */
    @JvmName("constantMinus")
    @JsName("constantMinus")
    public override operator fun C.minus(other: C): C = ring { this@minus - other }
    /**
     * Returns product of the constants.
     */
    @JvmName("constantTimes")
    @JsName("constantTimes")
    public override operator fun C.times(other: C): C = ring { this@times * other }

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public override val constantZero: C get() = ring.zero
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public override val constantOne: C get() = ring.one
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public interface AbstractRationalFunctionalSpaceOverPolynomialSpace<C, P: AbstractPolynomial<C>, R: AbstractRationalFunction<C, P>, A: Ring<C>> : AbstractRationalFunctionalSpace<C, P, R> {

    public val polynomialRing: AbstractPolynomialSpace<C, P>

    // region Constant-integer relation
    /**
     * Returns sum of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    @JvmName("constantIntPlus")
    public override operator fun C.plus(other: Int): C = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    @JvmName("constantIntMinus")
    public override operator fun C.minus(other: Int): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("constantIntTimes")
    public override operator fun C.times(other: Int): C = polynomialRing { this@times * other }
    // endregion

    // region Integer-constant relation
    /**
     * Returns sum of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    @JvmName("intConstantPlus")
    public override operator fun Int.plus(other: C): C = polynomialRing { this@plus + other }
    /**
     * Returns difference between the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    @JvmName("intConstantMinus")
    public override operator fun Int.minus(other: C): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("intConstantTimes")
    public override operator fun Int.times(other: C): C = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-integer relation
    /**
     * Returns sum of the constant and the integer represented as polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun P.plus(other: Int): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant and the integer represented as polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun P.minus(other: Int): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant and the integer represented as polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun P.times(other: Int): P = polynomialRing { this@times * other }
    // endregion

    // region Integer-polynomial relation
    /**
     * Returns sum of the integer represented as polynomial and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the integer represented as polynomial and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the integer represented as polynomial and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: P): P = polynomialRing { this@times * other }
    // endregion

    // region Constant-constant relation
    /**
     * Returns the same constant.
     */
    @JvmName("constantUnaryPlus")
    @JsName("constantUnaryPlus")
    public override operator fun C.unaryPlus(): C = polynomialRing { +this@unaryPlus }
    /**
     * Returns negation of the constant.
     */
    @JvmName("constantUnaryMinus")
    @JsName("constantUnaryMinus")
    public override operator fun C.unaryMinus(): C = polynomialRing { -this@unaryMinus }
    /**
     * Returns sum of the constants.
     */
    @JvmName("constantPlus")
    @JsName("constantPlus")
    public override operator fun C.plus(other: C): C = polynomialRing { this@plus + other }
    /**
     * Returns difference of the constants.
     */
    @JvmName("constantMinus")
    @JsName("constantMinus")
    public override operator fun C.minus(other: C): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the constants.
     */
    @JvmName("constantTimes")
    @JsName("constantTimes")
    public override operator fun C.times(other: C): C = polynomialRing { this@times * other }

    /**
     * Check if the instant is zero constant.
     */
    @JvmName("constantIsZero")
    public override fun C.isZero(): Boolean = polynomialRing { this@isZero.isZero() }
    /**
     * Check if the instant is NOT zero constant.
     */
    @JvmName("constantIsNotZero")
    public override fun C.isNotZero(): Boolean = polynomialRing { this@isNotZero.isNotZero() }
    /**
     * Check if the instant is unit constant.
     */
    @JvmName("constantIsOne")
    public override fun C.isOne(): Boolean = polynomialRing { this@isOne.isOne() }
    /**
     * Check if the instant is NOT unit constant.
     */
    @JvmName("constantIsNotOne")
    public override fun C.isNotOne(): Boolean = polynomialRing { this@isNotOne.isNotOne() }
    /**
     * Check if the instant is minus unit constant.
     */
    @JvmName("constantIsMinusOne")
    public override fun C.isMinusOne(): Boolean = polynomialRing { this@isMinusOne.isMinusOne() }
    /**
     * Check if the instant is NOT minus unit constant.
     */
    @JvmName("constantIsNotMinusOne")
    public override fun C.isNotMinusOne(): Boolean = polynomialRing { this@isNotMinusOne.isNotMinusOne() }

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public override val constantZero: C get() = polynomialRing.constantZero
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public override val constantOne: C get() = polynomialRing.constantOne
    // endregion

    // region Constant-polynomial relation
    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    public override operator fun C.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    public override operator fun C.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    public override operator fun C.times(other: P): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-constant relation
    /**
     * Returns sum of the constant represented as polynomial and the polynomial.
     */
    public override operator fun P.plus(other: C): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant represented as polynomial and the polynomial.
     */
    public override operator fun P.minus(other: C): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant represented as polynomial and the polynomial.
     */
    public override operator fun P.times(other: C): P = polynomialRing { this@times * other }
    // endregion

    // region Polynomial-polynomial relation
    /**
     * Returns the same polynomial.
     */
    public override operator fun P.unaryPlus(): P = polynomialRing { +this@unaryPlus }
    /**
     * Returns negation of the polynomial.
     */
    public override operator fun P.unaryMinus(): P = polynomialRing { -this@unaryMinus }
    /**
     * Returns sum of the polynomials.
     */
    public override operator fun P.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference of the polynomials.
     */
    public override operator fun P.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the polynomials.
     */
    public override operator fun P.times(other: P): P = polynomialRing { this@times * other }

    /**
     * Check if the instant is zero polynomial.
     */
    public override fun P.isZero(): Boolean = polynomialRing { this@isZero.isZero() }
    /**
     * Check if the instant is NOT zero polynomial.
     */
    public override fun P.isNotZero(): Boolean = polynomialRing { this@isNotZero.isNotZero() }
    /**
     * Check if the instant is unit polynomial.
     */
    public override fun P.isOne(): Boolean = polynomialRing { this@isOne.isOne() }
    /**
     * Check if the instant is NOT unit polynomial.
     */
    public override fun P.isNotOne(): Boolean = polynomialRing { this@isNotOne.isNotOne() }
    /**
     * Check if the instant is minus unit polynomial.
     */
    public override fun P.isMinusOne(): Boolean = polynomialRing { this@isMinusOne.isMinusOne() }
    /**
     * Check if the instant is NOT minus unit polynomial.
     */
    public override fun P.isNotMinusOne(): Boolean = polynomialRing { this@isNotMinusOne.isNotMinusOne() }

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    public override val polynomialZero: P get() = polynomialRing.zero
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    public override val polynomialOne: P get() = polynomialRing.one

    /**
     * Checks equality of the polynomials.
     */
    public override infix fun P.equalsTo(other: P): Boolean = polynomialRing { this@equalsTo equalsTo other }
    /**
     * Checks NOT equality of the polynomials.
     */
    public override infix fun P.notEqualsTo(other: P): Boolean = polynomialRing { this@notEqualsTo notEqualsTo other }
    // endregion

    // Not sure is it necessary...
    // region Polynomial properties
    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public override val P.degree: Int get() = polynomialRing { this@degree.degree }

    /**
     * Checks if the instant is constant polynomial (of degree no more than 0) over considered ring.
     */
    public override fun P.isConstant(): Boolean = polynomialRing { this@isConstant.isConstant() }
    /**
     * Checks if the instant is **not** constant polynomial (of degree no more than 0) over considered ring.
     */
    public override fun P.isNotConstant(): Boolean = polynomialRing { this@isNotConstant.isNotConstant() }
    /**
     * Checks if the instant is constant non-zero polynomial (of degree no more than 0) over considered ring.
     */
    public override fun P.isNonZeroConstant(): Boolean = polynomialRing { this@isNonZeroConstant.isNonZeroConstant() }
    /**
     * Checks if the instant is **not** constant non-zero polynomial (of degree no more than 0) over considered ring.
     */
    public override fun P.isNotNonZeroConstant(): Boolean = polynomialRing { this@isNotNonZeroConstant.isNotNonZeroConstant() }
    /**
     * If polynomial is a constant polynomial represents and returns it as constant.
     * Otherwise, (when the polynomial is not constant polynomial) returns `null`.
     */
    public override fun P.asConstantOrNull(): C? = polynomialRing { this@asConstantOrNull.asConstantOrNull() }
    /**
     * If polynomial is a constant polynomial represents and returns it as constant.
     * Otherwise, (when the polynomial is not constant polynomial) raises corresponding exception.
     */
    public override fun P.asConstant(): C = polynomialRing { this@asConstant.asConstant() }

    // endregion
}