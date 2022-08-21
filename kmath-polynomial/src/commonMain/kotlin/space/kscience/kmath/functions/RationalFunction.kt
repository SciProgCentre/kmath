/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import kotlin.js.JsName
import kotlin.jvm.JvmName


/**
 * Abstraction of rational function.
 */
public interface RationalFunction<C, P> {
    public val numerator: P
    public val denominator: P
    public operator fun component1(): P = numerator
    public operator fun component2(): P = denominator
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] and constants of type
 * [C].
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators.
 * @param R the type of rational functions.
 */
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpace<C, P, R: RationalFunction<C, P>> : Ring<R> {
    /**
     * Returns sum of the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    @JvmName("plusConstantInt")
    public operator fun C.plus(other: Int): C
    /**
     * Returns difference between the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    @JvmName("minusConstantInt")
    public operator fun C.minus(other: Int): C
    /**
     * Returns product of the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("timesConstantInt")
    public operator fun C.times(other: Int): C

    /**
     * Returns sum of the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    @JvmName("plusIntConstant")
    public operator fun Int.plus(other: C): C
    /**
     * Returns difference between the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    @JvmName("minusIntConstant")
    public operator fun Int.minus(other: C): C
    /**
     * Returns product of the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("timesIntConstant")
    public operator fun Int.times(other: C): C

    /**
     * Converts the integer [value] to constant.
     */
    public fun constantNumber(value: Int): C = constantOne * value
    /**
     * Converts the integer to constant.
     */
    public fun Int.asConstant(): C = constantNumber(this)

    /**
     * Returns sum of the constant and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    @JvmName("plusPolynomialInt")
    public operator fun P.plus(other: Int): P
    /**
     * Returns difference between the constant and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    @JvmName("minusPolynomialInt")
    public operator fun P.minus(other: Int): P
    /**
     * Returns product of the constant and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("timesPolynomialInt")
    public operator fun P.times(other: Int): P

    /**
     * Returns sum of the integer represented as a polynomial and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    @JvmName("plusIntPolynomial")
    public operator fun Int.plus(other: P): P
    /**
     * Returns difference between the integer represented as a polynomial and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    @JvmName("minusIntPolynomial")
    public operator fun Int.minus(other: P): P
    /**
     * Returns product of the integer represented as a polynomial and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("timesIntPolynomial")
    public operator fun Int.times(other: P): P

    /**
     * Converts the integer [value] to polynomial.
     */
    public fun polynomialNumber(value: Int): P = polynomialOne * value
    /**
     * Converts the integer to polynomial.
     */
    public fun Int.asPolynomial(): P = polynomialNumber(this)

    /**
     * Returns sum of the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public operator fun R.plus(other: Int): R = addMultipliedByDoubling(this, one, other)
    /**
     * Returns difference between the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public operator fun R.minus(other: Int): R = addMultipliedByDoubling(this, one, -other)
    /**
     * Returns product of the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun R.times(other: Int): R = multiplyByDoubling(this, other)
    /**
     * Returns quotient of the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to creating a new rational function by preserving numerator of [this] and
     * multiplication denominator of [this] to [other].
     */
    public operator fun R.div(other: Int): R = this / multiplyByDoubling(one, other)

    /**
     * Returns sum of the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public operator fun Int.plus(other: R): R = addMultipliedByDoubling(other, one, this)
    /**
     * Returns difference between the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public operator fun Int.minus(other: R): R = addMultipliedByDoubling(-other, one, this)
    /**
     * Returns product of the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public operator fun Int.times(other: R): R = multiplyByDoubling(other, this)
    /**
     * Returns quotient of the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to creating a new rational function which numerator is [this] times denominator of
     * [other] and which denominator is [other]'s numerator.
     */
    public operator fun Int.div(other: R): R = multiplyByDoubling(one / other, this)

    /**
     * Converts the integer [value] to rational function.
     */
    public fun number(value: Int): R = one * value
    /**
     * Converts the integer to rational function.
     */
    public fun Int.asRationalFunction(): R = number(this)

    /**
     * Returns the same constant.
     */
    @JvmName("unaryPlusConstant")
    @JsName("unaryPlusConstant")
    public operator fun C.unaryPlus(): C = this
    /**
     * Returns negation of the constant.
     */
    @JvmName("unaryMinusConstant")
    @JsName("unaryMinusConstant")
    public operator fun C.unaryMinus(): C
    /**
     * Returns sum of the constants.
     */
    @JvmName("plusConstantConstant")
    @JsName("plusConstantConstant")
    public operator fun C.plus(other: C): C
    /**
     * Returns difference of the constants.
     */
    @JvmName("minusConstantConstant")
    @JsName("minusConstantConstant")
    public operator fun C.minus(other: C): C
    /**
     * Returns product of the constants.
     */
    @JvmName("timesConstantConstant")
    @JsName("timesConstantConstant")
    public operator fun C.times(other: C): C
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("powerConstant")
    @JsName("powerConstant")
    public fun power(arg: C, exponent: UInt) : C

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public val constantZero: C
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public val constantOne: C

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("plusConstantPolynomial")
    public operator fun C.plus(other: P): P
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    @JvmName("minusConstantPolynomial")
    public operator fun C.minus(other: P): P
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("timesConstantPolynomial")
    public operator fun C.times(other: P): P

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("plusPolynomialConstant")
    public operator fun P.plus(other: C): P
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    @JvmName("minusPolynomialConstant")
    public operator fun P.minus(other: C): P
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("timesPolynomialConstant")
    public operator fun P.times(other: C): P

    /**
     * Converts the constant [value] to polynomial.
     */
    public fun polynomialNumber(value: C): P = polynomialOne * value
    /**
     * Converts the constant to polynomial.
     */
    public fun C.asPolynomial(): P = polynomialNumber(this)

    /**
     * Returns the same polynomial.
     */
    @JvmName("unaryPlusPolynomial")
    public operator fun P.unaryPlus(): P = this
    /**
     * Returns negation of the polynomial.
     */
    @JvmName("unaryMinusPolynomial")
    public operator fun P.unaryMinus(): P
    /**
     * Returns sum of the polynomials.
     */
    @JvmName("plusPolynomialPolynomial")
    public operator fun P.plus(other: P): P
    /**
     * Returns difference of the polynomials.
     */
    @JvmName("minusPolynomialPolynomial")
    public operator fun P.minus(other: P): P
    /**
     * Returns product of the polynomials.
     */
    @JvmName("timesPolynomialPolynomial")
    public operator fun P.times(other: P): P
    /**
     * Returns quotient of the polynomials as rational function.
     */
    @JvmName("divPolynomialPolynomial")
    public operator fun P.div(other: P): R
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("powerPolynomial")
    public fun power(arg: P, exponent: UInt) : P

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    public val polynomialZero: P
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    public val polynomialOne: P

    /**
     * Returns sum of the constant represented as a rational function and the rational function.
     */
    @JvmName("plusConstantRational")
    public operator fun C.plus(other: R): R
    /**
     * Returns difference between the constant represented as a polynomial and the rational function.
     */
    @JvmName("minusConstantRational")
    public operator fun C.minus(other: R): R
    /**
     * Returns product of the constant represented as a polynomial and the rational function.
     */
    @JvmName("timesConstantRational")
    public operator fun C.times(other: R): R
    /**
     * Returns quotient of the constant represented as a polynomial and the rational function.
     */
    @JvmName("divConstantRational")
    public operator fun C.div(other: R): R

    /**
     * Returns sum of the rational function and the constant represented as a rational function.
     */
    @JvmName("plusRationalConstant")
    public operator fun R.plus(other: C): R
    /**
     * Returns difference between the rational function and the constant represented as a rational function.
     */
    @JvmName("minusRationalConstant")
    public operator fun R.minus(other: C): R
    /**
     * Returns product of the rational function and the constant represented as a rational function.
     */
    @JvmName("timesRationalConstant")
    public operator fun R.times(other: C): R
    /**
     * Returns quotient of the rational function and the constant represented as a rational function.
     */
    @JvmName("divRationalConstant")
    public operator fun R.div(other: C): R

    /**
     * Converts the constant [value] to rational function.
     */
    @JvmName("numberConstant")
    public fun number(value: C): R = one * value
    /**
     * Converts the constant to rational function.
     */
    @JvmName("asRationalFunctionConstant")
    public fun C.asRationalFunction(): R = number(this)

    /**
     * Returns sum of the polynomial represented as a rational function and the rational function.
     */
    @JvmName("plusPolynomialRational")
    public operator fun P.plus(other: R): R
    /**
     * Returns difference between the polynomial represented as a polynomial and the rational function.
     */
    @JvmName("minusPolynomialRational")
    public operator fun P.minus(other: R): R
    /**
     * Returns product of the polynomial represented as a polynomial and the rational function.
     */
    @JvmName("timesPolynomialRational")
    public operator fun P.times(other: R): R
    /**
     * Returns quotient of the polynomial represented as a polynomial and the rational function.
     */
    @JvmName("divPolynomialRational")
    public operator fun P.div(other: R): R

    /**
     * Returns sum of the rational function and the polynomial represented as a rational function.
     */
    @JvmName("plusRationalPolynomial")
    public operator fun R.plus(other: P): R
    /**
     * Returns difference between the rational function and the polynomial represented as a rational function.
     */
    @JvmName("minusRationalPolynomial")
    public operator fun R.minus(other: P): R
    /**
     * Returns product of the rational function and the polynomial represented as a rational function.
     */
    @JvmName("timesRationalPolynomial")
    public operator fun R.times(other: P): R
    /**
     * Returns quotient of the rational function and the polynomial represented as a rational function.
     */
    @JvmName("divRationalPolynomial")
    public operator fun R.div(other: P): R

    /**
     * Converts the polynomial [value] to rational function.
     */
    @JvmName("numberPolynomial")
    public fun number(value: P): R = one * value
    /**
     * Converts the polynomial to rational function.
     */
    @JvmName("asRationalFunctionPolynomial")
    public fun P.asRationalFunction(): R = number(this)

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
     * Returns quotient of the rational functions.
     */
    public operator fun R.div(other: R): R
    /**
     * Raises [arg] to the integer power [exponent].
     */
    public override fun power(arg: R, exponent: UInt) : R = exponentiateBySquaring(arg, exponent)

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: R
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: R

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public val P.degree: Int

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

    override fun add(left: R, right: R): R = left + right
    override fun multiply(left: R, right: R): R = left * right
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] and constants of type
 * [C]. It also assumes that there is provided [ring] (of type [A]), that provides constant-wise operations.
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 * @param A the type of algebraic structure (precisely, of ring) provided for constants.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverRing<
        C,
        P,
        R: RationalFunction<C, P>,
        out A: Ring<C>
        > : RationalFunctionSpace<C, P, R> {

    /**
     * Underlying ring of constants. Its operations on constants are inherited by local operations on constants.
     */
    public val ring: A

    /**
     * Returns sum of the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Int): C = ring { addMultipliedByDoubling(this@plus, one, other) }
    /**
     * Returns difference between the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Int): C = ring { addMultipliedByDoubling(this@minus, one, -other) }
    /**
     * Returns product of the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Int): C = ring { multiplyByDoubling(this@times, other) }

    /**
     * Returns sum of the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    @JvmName("plusIntConstant")
    public override operator fun Int.plus(other: C): C = ring { addMultipliedByDoubling(other, one, this@plus) }
    /**
     * Returns difference between the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    @JvmName("minusIntConstant")
    public override operator fun Int.minus(other: C): C = ring { addMultipliedByDoubling(-other, one, this@minus) }
    /**
     * Returns product of the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("timesIntConstant")
    public override operator fun Int.times(other: C): C = ring { multiplyByDoubling(other, this@times) }

    /**
     * Returns the same constant.
     */
    @JvmName("unaryPlusConstant")
    public override operator fun C.unaryPlus(): C = ring { +this@unaryPlus }
    /**
     * Returns negation of the constant.
     */
    @JvmName("unaryMinusConstant")
    public override operator fun C.unaryMinus(): C = ring { -this@unaryMinus }
    /**
     * Returns sum of the constants.
     */
    @JvmName("plusConstantConstant")
    public override operator fun C.plus(other: C): C = ring { this@plus + other }
    /**
     * Returns difference of the constants.
     */
    @JvmName("minusConstantConstant")
    public override operator fun C.minus(other: C): C = ring { this@minus - other }
    /**
     * Returns product of the constants.
     */
    @JvmName("timesConstantConstant")
    public override operator fun C.times(other: C): C = ring { this@times * other }
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("powerConstant")
    public override fun power(arg: C, exponent: UInt) : C = ring { power(arg, exponent) }

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public override val constantZero: C get() = ring.zero
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public override val constantOne: C get() = ring.one
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] and constants of type
 * [C]. It also assumes that there is provided [polynomialRing] (of type [AP]), that provides constant- and
 * polynomial-wise operations.
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 * @param AP the type of algebraic structure (precisely, of ring) provided for polynomials.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface RationalFunctionSpaceOverPolynomialSpace<
        C,
        P,
        R: RationalFunction<C, P>,
        out AP: PolynomialSpace<C, P>,
        > : RationalFunctionSpace<C, P, R> {

    /**
     * Underlying polynomial ring. Its polynomial operations are inherited by local polynomial operations.
     */
    public val polynomialRing: AP

    /**
     * Returns sum of the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    @JvmName("plusConstantInt")
    public override operator fun C.plus(other: Int): C = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    @JvmName("minusConstantInt")
    public override operator fun C.minus(other: Int): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant and the integer represented as a constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("timesConstantInt")
    public override operator fun C.times(other: Int): C = polynomialRing { this@times * other }

    /**
     * Returns sum of the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    @JvmName("plusIntConstant")
    public override operator fun Int.plus(other: C): C = polynomialRing { this@plus + other }
    /**
     * Returns difference between the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    @JvmName("minusIntConstant")
    public override operator fun Int.minus(other: C): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the integer represented as a constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("timesIntConstant")
    public override operator fun Int.times(other: C): C = polynomialRing { this@times * other }

    /**
     * Converts the integer [value] to constant.
     */
    public override fun constantNumber(value: Int): C = polynomialRing { constantNumber(value) }
    /**
     * Converts the integer to constant.
     */
    override fun Int.asConstant(): C = polynomialRing { asConstant() }

    /**
     * Returns sum of the constant and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    @JvmName("plusPolynomialInt")
    public override operator fun P.plus(other: Int): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    @JvmName("minusPolynomialInt")
    public override operator fun P.minus(other: Int): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    @JvmName("timesPolynomialInt")
    public override operator fun P.times(other: Int): P = polynomialRing { this@times * other }

    /**
     * Returns sum of the integer represented as a polynomial and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    @JvmName("plusIntPolynomial")
    public override operator fun Int.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the integer represented as a polynomial and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    @JvmName("minusIntPolynomial")
    public override operator fun Int.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the integer represented as a polynomial and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    @JvmName("timesIntPolynomial")
    public override operator fun Int.times(other: P): P = polynomialRing { this@times * other }

    /**
     * Converts the integer [value] to polynomial.
     */
    public override fun polynomialNumber(value: Int): P = polynomialRing { number(value) }
    /**
     * Converts the integer to polynomial.
     */
    public override fun Int.asPolynomial(): P = polynomialRing { asPolynomial() }

    /**
     * Returns the same constant.
     */
    @JvmName("unaryPlusConstant")
    public override operator fun C.unaryPlus(): C = polynomialRing { +this@unaryPlus }
    /**
     * Returns negation of the constant.
     */
    @JvmName("unaryMinusConstant")
    public override operator fun C.unaryMinus(): C = polynomialRing { -this@unaryMinus }
    /**
     * Returns sum of the constants.
     */
    @JvmName("plusConstantConstant")
    public override operator fun C.plus(other: C): C = polynomialRing { this@plus + other }
    /**
     * Returns difference of the constants.
     */
    @JvmName("minusConstantConstant")
    public override operator fun C.minus(other: C): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the constants.
     */
    @JvmName("timesConstantConstant")
    public override operator fun C.times(other: C): C = polynomialRing { this@times * other }
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("powerConstant")
    public override fun power(arg: C, exponent: UInt) : C = polynomialRing { power(arg, exponent) }

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public override val constantZero: C get() = polynomialRing.constantZero
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public override val constantOne: C get() = polynomialRing.constantOne

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("plusConstantPolynomial")
    public override operator fun C.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    @JvmName("minusConstantPolynomial")
    public override operator fun C.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("timesConstantPolynomial")
    public override operator fun C.times(other: P): P = polynomialRing { this@times * other }

    /**
     * Returns sum of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("plusPolynomialConstant")
    public override operator fun P.plus(other: C): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant represented as a polynomial and the polynomial.
     */
    @JvmName("minusPolynomialConstant")
    public override operator fun P.minus(other: C): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant represented as a polynomial and the polynomial.
     */
    @JvmName("timesPolynomialConstant")
    public override operator fun P.times(other: C): P = polynomialRing { this@times * other }

    /**
     * Converts the constant [value] to polynomial.
     */
    public override fun polynomialNumber(value: C): P = polynomialRing { number(value) }
    /**
     * Converts the constant to polynomial.
     */
    public override fun C.asPolynomial(): P = polynomialRing { asPolynomial() }

    /**
     * Returns the same polynomial.
     */
    @JvmName("unaryPlusPolynomial")
    public override operator fun P.unaryPlus(): P = polynomialRing { +this@unaryPlus }
    /**
     * Returns negation of the polynomial.
     */
    @JvmName("unaryMinusPolynomial")
    public override operator fun P.unaryMinus(): P = polynomialRing { -this@unaryMinus }
    /**
     * Returns sum of the polynomials.
     */
    @JvmName("plusPolynomialPolynomial")
    public override operator fun P.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference of the polynomials.
     */
    @JvmName("minusPolynomialPolynomial")
    public override operator fun P.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the polynomials.
     */
    @JvmName("timesPolynomialPolynomial")
    public override operator fun P.times(other: P): P = polynomialRing { this@times * other }
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("powerPolynomial")
    public override fun power(arg: P, exponent: UInt) : P = polynomialRing { power(arg, exponent) }

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    public override val polynomialZero: P get() = polynomialRing.zero
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    public override val polynomialOne: P get() = polynomialRing.one

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public override val P.degree: Int get() = polynomialRing { this@degree.degree }
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] and constants of type
 * [C]. It also assumes that there is provided constructor [constructRationalFunction] of rational functions from
 * polynomial numerator and denominator.
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class PolynomialSpaceOfFractions<
        C,
        P,
        R: RationalFunction<C, P>,
        > : RationalFunctionSpace<C, P, R> {

    /**
     * Constructor of rational functions (of type [R]) from numerator and denominator (of type [P]).
     */
    protected abstract fun constructRationalFunction(numerator: P, denominator: P = polynomialOne) : R

    /**
     * Returns sum of the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun R.plus(other: Int): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun R.minus(other: Int): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun R.times(other: Int): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    /**
     * Returns quotient of the rational function and the integer represented as a rational function.
     *
     * The operation is equivalent to creating a new rational function by preserving numerator of [this] and
     * multiplication denominator of [this] to [other].
     */
    public override operator fun R.div(other: Int): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    /**
     * Returns sum of the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    /**
     * Returns quotient of the integer represented as a rational function and the rational function.
     *
     * The operation is equivalent to creating a new rational function which numerator is [this] times denominator of
     * [other] and which denominator is [other]'s numerator.
     */
    public override operator fun Int.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    /**
     * Converts the integer [value] to rational function.
     */
    public override fun number(value: Int): R = constructRationalFunction(polynomialNumber(value))

    /**
     * Returns quotient of the polynomials as rational function.
     */
    @JvmName("divPolynomialPolynomial")
    public override operator fun P.div(other: P): R = constructRationalFunction(this, other)

    /**
     * Returns sum of the constant represented as a rational function and the rational function.
     */
    @JvmName("plusConstantRational")
    public override operator fun C.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the constant represented as a polynomial and the rational function.
     */
    @JvmName("minusConstantRational")
    public override operator fun C.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the constant represented as a polynomial and the rational function.
     */
    @JvmName("timesConstantRational")
    public override operator fun C.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    /**
     * Returns quotient of the constant represented as a polynomial and the rational function.
     */
    @JvmName("divConstantRational")
    public override operator fun C.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    /**
     * Returns sum of the constant represented as a rational function and the rational function.
     */
    @JvmName("plusRationalConstant")
    public override operator fun R.plus(other: C): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the constant represented as a rational function and the rational function.
     */
    @JvmName("minusRationalConstant")
    public override operator fun R.minus(other: C): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the constant represented as a rational function and the rational function.
     */
    @JvmName("timesRationalConstant")
    public override operator fun R.times(other: C): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    /**
     * Returns quotient of the rational function and the constant represented as a rational function.
     */
    @JvmName("divRationalConstant")
    public override operator fun R.div(other: C): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    /**
     * Converts the constant [value] to rational function.
     */
    @JvmName("numberConstant")
    public override fun number(value: C): R = constructRationalFunction(polynomialNumber(value))

    /**
     * Returns sum of the polynomial represented as a rational function and the rational function.
     */
    @JvmName("plusPolynomialRational")
    public override operator fun P.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the polynomial represented as a polynomial and the rational function.
     */
    @JvmName("minusPolynomialRational")
    public override operator fun P.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the polynomial represented as a polynomial and the rational function.
     */
    @JvmName("timesPolynomialRational")
    public override operator fun P.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )
    /**
     * Returns quotient of the polynomial represented as a polynomial and the rational function.
     */
    @JvmName("divPolynomialRational")
    public override operator fun P.div(other: R): R =
        constructRationalFunction(
            this * other.denominator,
            other.numerator
        )

    /**
     * Returns sum of the polynomial represented as a rational function and the rational function.
     */
    @JvmName("plusRationalPolynomial")
    public override operator fun R.plus(other: P): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the polynomial represented as a rational function and the rational function.
     */
    @JvmName("minusRationalPolynomial")
    public override operator fun R.minus(other: P): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the polynomial represented as a rational function and the rational function.
     */
    @JvmName("timesRationalPolynomial")
    public override operator fun R.times(other: P): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
    /**
     * Returns quotient of the rational function and the polynomial represented as a rational function.
     */
    @JvmName("divRationalPolynomial")
    public override operator fun R.div(other: P): R =
        constructRationalFunction(
            numerator,
            denominator * other
        )

    /**
     * Converts the polynomial [value] to rational function.
     */
    @JvmName("numberPolynomial")
    public override fun number(value: P): R = constructRationalFunction(value)

    /**
     * Returns negation of the rational function.
     */
    public override operator fun R.unaryMinus(): R = constructRationalFunction(-numerator, denominator)
    /**
     * Returns sum of the rational functions.
     */
    public override operator fun R.plus(other: R): R =
        constructRationalFunction(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns difference of the rational functions.
     */
    public override operator fun R.minus(other: R): R =
        constructRationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns product of the rational functions.
     */
    public override operator fun R.times(other: R): R =
        constructRationalFunction(
            numerator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns quotient of the rational functions.
     */
    public override operator fun R.div(other: R): R =
        constructRationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )
    /**
     * Raises [arg] to the integer power [exponent].
     */
    public override fun power(arg: R, exponent: UInt): R =
        constructRationalFunction(
            power(arg.numerator, exponent),
            power(arg.denominator, exponent),
        )

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: R by lazy { constructRationalFunction(polynomialZero) }

    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: R by lazy { constructRationalFunction(polynomialOne) }
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] of variables of type
 * [V] and over ring of constants of type [C].
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param V the type of variables. Polynomials have them in representations of terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpace<
        C,
        V,
        P,
        R: RationalFunction<C, P>
        >: RationalFunctionSpace<C, P, R> {
    /**
     * Returns sum of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    @JvmName("plusVariableInt")
    @JsName("plusVariableInt")
    public operator fun V.plus(other: Int): P
    /**
     * Returns difference between the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    @JvmName("minusVariableInt")
    @JsName("minusVariableInt")
    public operator fun V.minus(other: Int): P
    /**
     * Returns product of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    @JvmName("timesVariableInt")
    @JsName("timesVariableInt")
    public operator fun V.times(other: Int): P

    /**
     * Returns sum of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("plusIntVariable")
    @JsName("plusIntVariable")
    public operator fun Int.plus(other: V): P
    /**
     * Returns difference between the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("minusIntVariable")
    @JsName("minusIntVariable")
    public operator fun Int.minus(other: V): P
    /**
     * Returns product of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("timesIntVariable")
    @JsName("timesIntVariable")
    public operator fun Int.times(other: V): P

    /**
     * Returns sum of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    @JvmName("plusVariableConstant")
    @JsName("plusVariableConstant")
    public operator fun V.plus(other: C): P
    /**
     * Returns difference between the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    @JvmName("minusVariableConstant")
    @JsName("minusVariableConstant")
    public operator fun V.minus(other: C): P
    /**
     * Returns product of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    @JvmName("timesVariableConstant")
    @JsName("timesVariableConstant")
    public operator fun V.times(other: C): P

    /**
     * Returns sum of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("plusConstantVariable")
    @JsName("plusConstantVariable")
    public operator fun C.plus(other: V): P
    /**
     * Returns difference between the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("minusConstantVariable")
    @JsName("minusConstantVariable")
    public operator fun C.minus(other: V): P
    /**
     * Returns product of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("timesConstantVariable")
    @JsName("timesConstantVariable")
    public operator fun C.times(other: V): P

    /**
     * Represents the variable as a monic monomial.
     */
    @JvmName("unaryPlusVariable")
    @JsName("unaryPlusVariable")
    public operator fun V.unaryPlus(): P
    /**
     * Returns negation of representation of the variable as a monic monomial.
     */
    @JvmName("unaryMinusVariable")
    @JsName("unaryMinusVariable")
    public operator fun V.unaryMinus(): P
    /**
     * Returns sum of the variables represented as monic monomials.
     */
    @JvmName("plusVariableVariable")
    @JsName("plusVariableVariable")
    public operator fun V.plus(other: V): P
    /**
     * Returns difference between the variables represented as monic monomials.
     */
    @JvmName("minusVariableVariable")
    @JsName("minusVariableVariable")
    public operator fun V.minus(other: V): P
    /**
     * Returns product of the variables represented as monic monomials.
     */
    @JvmName("timesVariableVariable")
    @JsName("timesVariableVariable")
    public operator fun V.times(other: V): P

    /**
     * Represents the [variable] as a monic monomial.
     */
    @JvmName("polynomialNumberVariable")
    public fun polynomialNumber(variable: V): P = +variable
    /**
     * Represents the variable as a monic monomial.
     */
    @JvmName("asPolynomialVariable")
    public fun V.asPolynomial(): P = polynomialNumber(this)

    /**
     * Represents the [variable] as a rational function.
     */
    @JvmName("numberVariable")
    @JsName("numberVariable")
    public fun number(variable: V): R = number(polynomialNumber(variable))
    /**
     * Represents the variable as a rational function.
     */
    @JvmName("asRationalFunctionVariable")
    @JsName("asRationalFunctionVariable")
    public fun V.asRationalFunction(): R = number(this)

    /**
     * Returns sum of the variable represented as a monic monomial and the polynomial.
     */
    @JvmName("plusVariablePolynomial")
    public operator fun V.plus(other: P): P
    /**
     * Returns difference between the variable represented as a monic monomial and the polynomial.
     */
    @JvmName("minusVariablePolynomial")
    public operator fun V.minus(other: P): P
    /**
     * Returns product of the variable represented as a monic monomial and the polynomial.
     */
    @JvmName("timesVariablePolynomial")
    public operator fun V.times(other: P): P

    /**
     * Returns sum of the polynomial and the variable represented as a monic monomial.
     */
    @JvmName("plusPolynomialVariable")
    public operator fun P.plus(other: V): P
    /**
     * Returns difference between the polynomial and the variable represented as a monic monomial.
     */
    @JvmName("minusPolynomialVariable")
    public operator fun P.minus(other: V): P
    /**
     * Returns product of the polynomial and the variable represented as a monic monomial.
     */
    @JvmName("timesPolynomialVariable")
    public operator fun P.times(other: V): P

    /**
     * Returns sum of the variable represented as a rational function and the rational function.
     */
    @JvmName("plusVariableRational")
    public operator fun V.plus(other: R): R
    /**
     * Returns difference between the variable represented as a rational function and the rational function.
     */
    @JvmName("minusVariableRational")
    public operator fun V.minus(other: R): R
    /**
     * Returns product of the variable represented as a rational function and the rational function.
     */
    @JvmName("timesVariableRational")
    public operator fun V.times(other: R): R

    /**
     * Returns sum of the rational function and the variable represented as a rational function.
     */
    @JvmName("plusRationalVariable")
    public operator fun R.plus(other: V): R
    /**
     * Returns difference between the rational function and the variable represented as a rational function.
     */
    @JvmName("minusRationalVariable")
    public operator fun R.minus(other: V): R
    /**
     * Returns product of the rational function and the variable represented as a rational function.
     */
    @JvmName("timesRationalVariable")
    public operator fun R.times(other: V): R

    /**
     * Map that associates variables (that appear in the polynomial in positive exponents) with their most exponents
     * in which they are appeared in the polynomial.
     *
     * As consequence all values in the map are positive integers. Also, if the polynomial is constant, the map is empty.
     * And keys of the map is the same as in [variables].
     */
    public val P.degrees: Map<V, UInt>
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public fun P.degreeBy(variable: V): UInt = degrees.getOrElse(variable) { 0u }
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public fun P.degreeBy(variables: Collection<V>): UInt
    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public val P.variables: Set<V> get() = degrees.keys
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val P.countOfVariables: Int get() = variables.size

    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public val R.variables: Set<V> get() = numerator.variables union denominator.variables
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val R.countOfVariables: Int get() = variables.size
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] of variables of type
 * [V] and over ring of constants of type [C]. It also assumes that there is provided [polynomialRing] (of type [AP]),
 * that provides constant-, variable- and polynomial-wise operations.
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param V the type of variables. Polynomials have them in representations of terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 * @param AP the type of algebraic structure (precisely, of ring) provided for polynomials.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariateRationalFunctionSpaceOverMultivariatePolynomialSpace<
        C,
        V,
        P,
        R: RationalFunction<C, P>,
        out AP: MultivariatePolynomialSpace<C, V, P>,
        > : RationalFunctionSpaceOverPolynomialSpace<C, P, R, AP>, MultivariateRationalFunctionSpace<C, V, P, R> {
    /**
     * Returns sum of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    @JvmName("plusVariableInt")
    public override operator fun V.plus(other: Int): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    @JvmName("minusVariableInt")
    public override operator fun V.minus(other: Int): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the variable represented as a monic monomial and the integer represented as a constant polynomial.
     */
    @JvmName("timesVariableInt")
    public override operator fun V.times(other: Int): P = polynomialRing { this@times * other }

    /**
     * Returns sum of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("plusIntVariable")
    public override operator fun Int.plus(other: V): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("minusIntVariable")
    public override operator fun Int.minus(other: V): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the integer represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("timesIntVariable")
    public override operator fun Int.times(other: V): P = polynomialRing { this@times * other }

    /**
     * Returns sum of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    @JvmName("plusVariableConstant")
    public override operator fun V.plus(other: C): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    @JvmName("minusVariableConstant")
    public override operator fun V.minus(other: C): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the variable represented as a monic monomial and the constant represented as a constant polynomial.
     */
    @JvmName("timesVariableConstant")
    public override operator fun V.times(other: C): P = polynomialRing { this@times * other }

    /**
     * Returns sum of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("plusConstantVariable")
    public override operator fun C.plus(other: V): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("minusConstantVariable")
    public override operator fun C.minus(other: V): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant represented as a constant polynomial and the variable represented as a monic monomial.
     */
    @JvmName("timesConstantVariable")
    public override operator fun C.times(other: V): P = polynomialRing { this@times * other }

    /**
     * Represents the variable as a monic monomial.
     */
    @JvmName("unaryPlusVariable")
    public override operator fun V.unaryPlus(): P = polynomialRing { +this@unaryPlus }
    /**
     * Returns negation of representation of the variable as a monic monomial.
     */
    @JvmName("unaryMinusVariable")
    public override operator fun V.unaryMinus(): P = polynomialRing { -this@unaryMinus }
    /**
     * Returns sum of the variables represented as monic monomials.
     */
    @JvmName("plusVariableVariable")
    public override operator fun V.plus(other: V): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the variables represented as monic monomials.
     */
    @JvmName("minusVariableVariable")
    public override operator fun V.minus(other: V): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the variables represented as monic monomials.
     */
    @JvmName("timesVariableVariable")
    public override operator fun V.times(other: V): P = polynomialRing { this@times * other }

    /**
     * Represents the [variable] as a monic monomial.
     */
    @JvmName("polynomialNumberVariable")
    public override fun polynomialNumber(variable: V): P = polynomialRing { number(variable) }
    /**
     * Represents the variable as a monic monomial.
     */
    @JvmName("asPolynomialVariable")
    public override fun V.asPolynomial(): P = polynomialRing { this@asPolynomial.asPolynomial() }

    /**
     * Returns sum of the variable represented as a monic monomial and the polynomial.
     */
    @JvmName("plusVariablePolynomial")
    public override operator fun V.plus(other: P): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the variable represented as a monic monomial and the polynomial.
     */
    @JvmName("minusVariablePolynomial")
    public override operator fun V.minus(other: P): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the variable represented as a monic monomial and the polynomial.
     */
    @JvmName("timesVariablePolynomial")
    public override operator fun V.times(other: P): P = polynomialRing { this@times * other }

    /**
     * Returns sum of the polynomial and the variable represented as a monic monomial.
     */
    @JvmName("plusPolynomialVariable")
    public override operator fun P.plus(other: V): P = polynomialRing { this@plus + other }
    /**
     * Returns difference between the polynomial and the variable represented as a monic monomial.
     */
    @JvmName("minusPolynomialVariable")
    public override operator fun P.minus(other: V): P = polynomialRing { this@minus - other }
    /**
     * Returns product of the polynomial and the variable represented as a monic monomial.
     */
    @JvmName("timesPolynomialVariable")
    public override operator fun P.times(other: V): P = polynomialRing { this@times * other }

    /**
     * Map that associates variables (that appear in the polynomial in positive exponents) with their most exponents
     * in which they are appeared in the polynomial.
     *
     * As consequence all values in the map are positive integers. Also, if the polynomial is constant, the map is empty.
     * And keys of the map is the same as in [variables].
     */
    public override val P.degrees: Map<V, UInt> get() = polynomialRing { degrees }
    /**
     * Counts degree of the polynomial by the specified [variable].
     */
    public override fun P.degreeBy(variable: V): UInt = polynomialRing { degreeBy(variable) }
    /**
     * Counts degree of the polynomial by the specified [variables].
     */
    public override fun P.degreeBy(variables: Collection<V>): UInt = polynomialRing { degreeBy(variables) }
    /**
     * Set of all variables that appear in the polynomial in positive exponents.
     */
    public override val P.variables: Set<V> get() = polynomialRing { variables }
    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public override val P.countOfVariables: Int get() = polynomialRing { countOfVariables }
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] of variables of type
 * [V] and over ring of constants of type [C]. It also assumes that there is provided constructor
 * [constructRationalFunction] of rational functions from polynomial numerator and denominator.
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param V the type of variables. Polynomials have them in representations of terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public abstract class MultivariatePolynomialSpaceOfFractions<
        C,
        V,
        P,
        R: RationalFunction<C, P>,
        > : MultivariateRationalFunctionSpace<C, V, P, R>,  PolynomialSpaceOfFractions<C, P, R>() {
    /**
     * Returns sum of the variable represented as a rational function and the rational function.
     */
    @JvmName("plusVariableRational")
    public override operator fun V.plus(other: R): R =
        constructRationalFunction(
            this * other.denominator + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the variable represented as a rational function and the rational function.
     */
    @JvmName("minusVariableRational")
    public override operator fun V.minus(other: R): R =
        constructRationalFunction(
            this * other.denominator - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the variable represented as a rational function and the rational function.
     */
    @JvmName("timesVariableRational")
    public override operator fun V.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )

    /**
     * Returns sum of the rational function and the variable represented as a rational function.
     */
    @JvmName("plusRationalVariable")
    public override operator fun R.plus(other: V): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the rational function and the variable represented as a rational function.
     */
    @JvmName("minusRationalVariable")
    public override operator fun R.minus(other: V): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the rational function and the variable represented as a rational function.
     */
    public override operator fun R.times(other: V): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )
}