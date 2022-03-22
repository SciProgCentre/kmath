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
public interface RationalFunction<C, P: Polynomial<C>> {
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
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 */ // TODO: Add support of field
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public interface RationalFunctionalSpace<C, P: Polynomial<C>, R: RationalFunction<C, P>> : Ring<R> {
    /**
     * Returns sum of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    public operator fun C.plus(other: Int): C
    /**
     * Returns difference between the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    public operator fun C.minus(other: Int): C
    /**
     * Returns product of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun C.times(other: Int): C

    /**
     * Returns sum of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    public operator fun Int.plus(other: C): C
    /**
     * Returns difference between the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    public operator fun Int.minus(other: C): C
    /**
     * Returns product of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
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

    /**
     * Converts the integer [value] to polynomial.
     */
    public fun polynomialNumber(value: Int): P = polynomialOne * value
    /**
     * Converts the integer to polynomial.
     */
    public fun Int.asPolynomial(): P = polynomialNumber(this)

    /**
     * Returns sum of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public operator fun R.plus(other: Int): R = addMultipliedBySquaring(this, one, other)
    /**
     * Returns difference between the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public operator fun R.minus(other: Int): R = addMultipliedBySquaring(this, one, -other)
    /**
     * Returns product of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun R.times(other: Int): R = multiplyBySquaring(this, other)
    /**
     * Returns quotient of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to creating a new rational function by preserving numerator of [this] and
     * multiplication denominator of [this] to [other].
     */
    public operator fun R.div(other: Int): R = this / multiplyBySquaring(one, other)

    /**
     * Returns sum of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public operator fun Int.plus(other: R): R = addMultipliedBySquaring(other, one, this)
    /**
     * Returns difference between the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public operator fun Int.minus(other: R): R = addMultipliedBySquaring(-other, one, this)
    /**
     * Returns product of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public operator fun Int.times(other: R): R = multiplyBySquaring(other, this)
    /**
     * Returns quotient of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to creating a new rational function which numerator is [this] times denominator of
     * [other] and which denominator is [other]'s numerator.
     */
    public operator fun Int.div(other: R): R = multiplyBySquaring(one / other, this)

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
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("constantPower")
    @JsName("constantPower")
    public fun power(arg: C, exponent: UInt) : C

    /**
     * Check if the instant is zero constant.
     */
    public fun C.isZero(): Boolean = this == constantZero
    /**
     * Check if the instant is NOT zero constant.
     */
    public fun C.isNotZero(): Boolean = !isZero()
    /**
     * Check if the instant is unit constant.
     */
    public fun C.isOne(): Boolean =  this == constantOne
    /**
     * Check if the instant is NOT unit constant.
     */
    public fun C.isNotOne(): Boolean = !isOne()
    /**
     * Check if the instant is minus unit constant.
     */
    public fun C.isMinusOne(): Boolean =  this == -constantOne
    /**
     * Check if the instant is NOT minus unit constant.
     */
    public fun C.isNotMinusOne(): Boolean = !isMinusOne()

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public val constantZero: C
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public val constantOne: C

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
     * Returns quotient of the polynomials as rational function.
     */
    public operator fun P.div(other: P): R
    /**
     * Raises [arg] to the integer power [exponent].
     */
    public fun power(arg: P, exponent: UInt) : P

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
    /**
     * Returns quotient of the constant represented as polynomial and the rational function.
     */
    public operator fun C.div(other: R): R

    /**
     * Returns sum of the rational function and the constant represented as rational function.
     */
    public operator fun R.plus(other: C): R
    /**
     * Returns difference between the rational function and the constant represented as rational function.
     */
    public operator fun R.minus(other: C): R
    /**
     * Returns product of the rational function and the constant represented as rational function.
     */
    public operator fun R.times(other: C): R
    /**
     * Returns quotient of the rational function and the constant represented as rational function.
     */
    public operator fun R.div(other: C): R

    /**
     * Converts the constant [value] to rational function.
     */
    public fun number(value: C): R = one * value
    /**
     * Converts the constant to rational function.
     */
    public fun C.asRationalFunction(): R = number(this)

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
    /**
     * Returns quotient of the polynomial represented as polynomial and the rational function.
     */
    public operator fun P.div(other: R): R

    /**
     * Returns sum of the rational function and the polynomial represented as rational function.
     */
    public operator fun R.plus(other: P): R
    /**
     * Returns difference between the rational function and the polynomial represented as rational function.
     */
    public operator fun R.minus(other: P): R
    /**
     * Returns product of the rational function and the polynomial represented as rational function.
     */
    public operator fun R.times(other: P): R
    /**
     * Returns quotient of the rational function and the polynomial represented as rational function.
     */
    public operator fun R.div(other: P): R

    /**
     * Converts the polynomial [value] to rational function.
     */
    public fun number(value: P): R = one * value
    /**
     * Converts the polynomial to rational function.
     */
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
    public override fun power(arg: R, exponent: UInt) : R = exponentiationBySquaring(arg, exponent)

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
    public infix fun R.equalsTo(other: R): Boolean =
        when {
            this === other -> true
            numerator.isZero() != other.numerator.isZero() -> false
            numeratorDegree - denominatorDegree != with(other) { numeratorDegree - denominatorDegree } -> false
            else -> numerator * other.denominator equalsTo other.numerator * denominator
        }
    /**
     * Checks NOT equality of the polynomials.
     */
    public infix fun R.notEqualsTo(other: R): Boolean = !(this equalsTo other)

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
    public fun P.asConstant(): C = requireNotNull(asConstantOrNull()) { "Can not represent non-constant polynomial as a constant" }

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
 */ // TODO: Add support of field
@Suppress("INAPPLICABLE_JVM_NAME")
public interface RationalFunctionalSpaceOverRing<C, P: Polynomial<C>, R: RationalFunction<C, P>, A: Ring<C>> : RationalFunctionalSpace<C, P, R> {

    public val ring: A

    /**
     * Returns sum of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    public override operator fun C.plus(other: Int): C = ring { addMultipliedBySquaring(this@plus, one, other) }
    /**
     * Returns difference between the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    public override operator fun C.minus(other: Int): C = ring { addMultipliedBySquaring(this@minus, one, -other) }
    /**
     * Returns product of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun C.times(other: Int): C = ring { multiplyBySquaring(this@times, other) }

    /**
     * Returns sum of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    public override operator fun Int.plus(other: C): C = ring { addMultipliedBySquaring(other, one, this@plus) }
    /**
     * Returns difference between the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    public override operator fun Int.minus(other: C): C = ring { addMultipliedBySquaring(-other, one, this@minus) }
    /**
     * Returns product of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: C): C = ring { multiplyBySquaring(other, this@times) }

    /**
     * Returns the same constant.
     */
    @JvmName("constantUnaryPlus")
    public override operator fun C.unaryPlus(): C = ring { +this@unaryPlus }
    /**
     * Returns negation of the constant.
     */
    @JvmName("constantUnaryMinus")
    public override operator fun C.unaryMinus(): C = ring { -this@unaryMinus }
    /**
     * Returns sum of the constants.
     */
    @JvmName("constantPlus")
    public override operator fun C.plus(other: C): C = ring { this@plus + other }
    /**
     * Returns difference of the constants.
     */
    @JvmName("constantMinus")
    public override operator fun C.minus(other: C): C = ring { this@minus - other }
    /**
     * Returns product of the constants.
     */
    @JvmName("constantTimes")
    public override operator fun C.times(other: C): C = ring { this@times * other }
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("constantPower")
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
 */ // TODO: Add support of field
@Suppress("INAPPLICABLE_JVM_NAME")
public interface RationalFunctionalSpaceOverPolynomialSpace<
        C,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        AP: PolynomialSpace<C, P>,
        > : RationalFunctionalSpace<C, P, R> {

    public val polynomialRing: AP

    /**
     * Returns sum of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to adding [other] copies of unit of underlying ring to [this].
     */
    public override operator fun C.plus(other: Int): C = polynomialRing { this@plus + other }
    /**
     * Returns difference between the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to subtraction [other] copies of unit of underlying ring from [this].
     */
    public override operator fun C.minus(other: Int): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the constant and the integer represented as constant (member of underlying ring).
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun C.times(other: Int): C = polynomialRing { this@times * other }

    /**
     * Returns sum of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to adding [this] copies of unit of underlying ring to [other].
     */
    public override operator fun Int.plus(other: C): C = polynomialRing { this@plus + other }
    /**
     * Returns difference between the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to subtraction [this] copies of unit of underlying ring from [other].
     */
    public override operator fun Int.minus(other: C): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the integer represented as constant (member of underlying ring) and the constant.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
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
    @JvmName("constantUnaryPlus")
    public override operator fun C.unaryPlus(): C = polynomialRing { +this@unaryPlus }
    /**
     * Returns negation of the constant.
     */
    @JvmName("constantUnaryMinus")
    public override operator fun C.unaryMinus(): C = polynomialRing { -this@unaryMinus }
    /**
     * Returns sum of the constants.
     */
    @JvmName("constantPlus")
    public override operator fun C.plus(other: C): C = polynomialRing { this@plus + other }
    /**
     * Returns difference of the constants.
     */
    @JvmName("constantMinus")
    public override operator fun C.minus(other: C): C = polynomialRing { this@minus - other }
    /**
     * Returns product of the constants.
     */
    @JvmName("constantTimes")
    public override operator fun C.times(other: C): C = polynomialRing { this@times * other }
    /**
     * Raises [arg] to the integer power [exponent].
     */
    @JvmName("constantPower")
    public override fun power(arg: C, exponent: UInt) : C = polynomialRing { power(arg, exponent) }

    /**
     * Check if the instant is zero constant.
     */
    public override fun C.isZero(): Boolean = polynomialRing { this@isZero.isZero() }
    /**
     * Check if the instant is NOT zero constant.
     */
    public override fun C.isNotZero(): Boolean = polynomialRing { this@isNotZero.isNotZero() }
    /**
     * Check if the instant is unit constant.
     */
    public override fun C.isOne(): Boolean = polynomialRing { this@isOne.isOne() }
    /**
     * Check if the instant is NOT unit constant.
     */
    public override fun C.isNotOne(): Boolean = polynomialRing { this@isNotOne.isNotOne() }
    /**
     * Check if the instant is minus unit constant.
     */
    public override fun C.isMinusOne(): Boolean = polynomialRing { this@isMinusOne.isMinusOne() }
    /**
     * Check if the instant is NOT minus unit constant.
     */
    public override fun C.isNotMinusOne(): Boolean = polynomialRing { this@isNotMinusOne.isNotMinusOne() }

    /**
     * Instance of zero constant (zero of the underlying ring).
     */
    public override val constantZero: C get() = polynomialRing.constantZero
    /**
     * Instance of unit constant (unit of the underlying ring).
     */
    public override val constantOne: C get() = polynomialRing.constantOne

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
     * Raises [arg] to the integer power [exponent].
     */
    public override fun power(arg: P, exponent: UInt) : P = polynomialRing { power(arg, exponent) }

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
}

/**
 * Abstraction of field of rational functions of type [R] with respect to polynomials of type [P] and constants of type
 * [C]. It also assumes that there is provided constructor
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials. Rational functions have them as numerators and denominators in them.
 * @param R the type of rational functions.
 * @param AP the type of algebraic structure (precisely, of ring) provided for polynomials.
 */ // TODO: Add support of field
@Suppress("INAPPLICABLE_JVM_NAME")
public abstract class PolynomialSpaceOfFractions<
        C,
        P: Polynomial<C>,
        R: RationalFunction<C, P>,
        > : RationalFunctionalSpace<C, P, R> {
    protected abstract fun constructRationalFunction(numerator: P, denominator: P = polynomialOne) : R

    /**
     * Returns sum of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun R.plus(other: Int): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun R.minus(other: Int): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun R.times(other: Int): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )

    public override operator fun R.div(other: Int): R {
        val otherAsConstant = constantNumber(other)
        require(otherAsConstant.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            numerator,
            (denominator * other).also {
                check(it.isNotZero()) {
                    "Got zero denominator during division of rational functions to constant. It means underlying ring of polynomials is not integral domain."
                }
            }
        )
    }

    /**
     * Returns sum of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )

    public override operator fun Int.div(other: R): R {
        require(other.numerator.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    }

    /**
     * Converts the integer [value] to rational function.
     */
    public override fun number(value: Int): R = constructRationalFunction(polynomialNumber(value))

    /**
     * Returns quotient of the polynomials as rational function.
     */
    public override operator fun P.div(other: P): R = constructRationalFunction(this, other)

    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun C.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the constant represented as polynomial and the rational function.
     */
    public override operator fun C.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the constant represented as polynomial and the rational function.
     */
    public override operator fun C.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )

    public override operator fun C.div(other: R): R {
        require(other.numerator.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    }

    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun R.plus(other: C): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the constant represented as rational function and the rational function.
     */
    public override operator fun R.minus(other: C): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the constant represented as rational function and the rational function.
     */
    public override operator fun R.times(other: C): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )

    public override operator fun R.div(other: C): R {
        require(other.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            numerator,
            (denominator * other).also {
                check(it.isNotZero()) {
                    "Got zero denominator during division of rational functions to constant. It means underlying ring of polynomials is not integral domain."
                }
            }
        )
    }

    /**
     * Converts the constant [value] to rational function.
     */
    public override fun number(value: C): R = constructRationalFunction(polynomialNumber(value))

    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun P.plus(other: R): R =
        constructRationalFunction(
            other.denominator * this + other.numerator,
            other.denominator
        )
    /**
     * Returns difference between the polynomial represented as polynomial and the rational function.
     */
    public override operator fun P.minus(other: R): R =
        constructRationalFunction(
            other.denominator * this - other.numerator,
            other.denominator
        )
    /**
     * Returns product of the polynomial represented as polynomial and the rational function.
     */
    public override operator fun P.times(other: R): R =
        constructRationalFunction(
            this * other.numerator,
            other.denominator
        )

    public override operator fun P.div(other: R): R {
        require(other.numerator.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            this * other.denominator,
            other.numerator
        )
    }

    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun R.plus(other: P): R =
        constructRationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the polynomial represented as rational function and the rational function.
     */
    public override operator fun R.minus(other: P): R =
        constructRationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the polynomial represented as rational function and the rational function.
     */
    public override operator fun R.times(other: P): R =
        constructRationalFunction(
            numerator * other,
            denominator
        )

    public override operator fun R.div(other: P): R {
        require(other.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            numerator,
            (denominator * other).also {
                require(it.isNotZero()) {
                    "Got zero denominator during division of rational functions to polynomial. It means underlying ring of polynomials is not integral domain."
                }
            }
        )
    }

    /**
     * Converts the polynomial [value] to rational function.
     */
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
            (denominator * other.denominator).also {
                check(it.isNotZero()) {
                    "Got zero denominator during addition of rational functions. It means underlying ring of polynomials is not integral domain."
                }
            }
        )
    /**
     * Returns difference of the rational functions.
     */
    public override operator fun R.minus(other: R): R =
        constructRationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            (denominator * other.denominator).also {
                check(it.isNotZero()) {
                    "Got zero denominator during subtraction of rational functions. It means underlying ring of polynomials is not integral domain."
                }
            }
        )
    /**
     * Returns product of the rational functions.
     */
    public override operator fun R.times(other: R): R =
        constructRationalFunction(
            numerator * other.numerator,
            (denominator * other.denominator).also {
                check(it.isNotZero()) {
                    "Got zero denominator during multiplication of rational functions. It means underlying ring of polynomials is not integral domain."
                }
            }
        )

    public override operator fun R.div(other: R): R {
        require(other.isNotZero()) { "/ by zero." }
        return constructRationalFunction(
            numerator * other.denominator,
            (denominator * other.numerator).also {
                check(it.isNotZero()) {
                    "Got zero denominator during division of rational functions. It means underlying ring of polynomials is not integral domain."
                }
            }
        )
    }

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: R get() = constructRationalFunction(polynomialZero)

    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: R get() = constructRationalFunction(polynomialOne)
}