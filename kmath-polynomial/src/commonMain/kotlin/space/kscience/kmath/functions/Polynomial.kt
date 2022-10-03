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
 * Abstraction of ring of polynomials of type [P] over ring of constants of type [C].
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials.
 */
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<C, P> : Ring<P> {
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
     * Returns sum of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public operator fun P.plus(other: Int): P = addMultipliedByDoubling(this, one, other)
    /**
     * Returns difference between the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public operator fun P.minus(other: Int): P = addMultipliedByDoubling(this, one, -other)
    /**
     * Returns product of the polynomial and the integer represented as a polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun P.times(other: Int): P = multiplyByDoubling(this, other)

    /**
     * Returns sum of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public operator fun Int.plus(other: P): P = addMultipliedByDoubling(other, one, this)
    /**
     * Returns difference between the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public operator fun Int.minus(other: P): P = addMultipliedByDoubling(-other, one, this)
    /**
     * Returns product of the integer represented as a polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public operator fun Int.times(other: P): P = multiplyByDoubling(other, this)

    /**
     * Converts the integer [value] to polynomial.
     */
    public fun number(value: Int): P = number(constantNumber(value))
    /**
     * Converts the integer to polynomial.
     */
    public fun Int.asPolynomial(): P = number(this)

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
    public fun number(value: C): P = one * value
    /**
     * Converts the constant to polynomial.
     */
    public fun C.asPolynomial(): P = number(this)

    /**
     * Returns the same polynomial.
     */
    public override operator fun P.unaryPlus(): P = this
    /**
     * Returns negation of the polynomial.
     */
    public override operator fun P.unaryMinus(): P
    /**
     * Returns sum of the polynomials.
     */
    public override operator fun P.plus(other: P): P
    /**
     * Returns difference of the polynomials.
     */
    public override operator fun P.minus(other: P): P
    /**
     * Returns product of the polynomials.
     */
    public override operator fun P.times(other: P): P
    /**
     * Raises [arg] to the integer power [exponent].
     */
    public override fun power(arg: P, exponent: UInt) : P = exponentiateBySquaring(arg, exponent)

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    public override val zero: P
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    public override val one: P

    /**
     * Degree of the polynomial, [see also](https://en.wikipedia.org/wiki/Degree_of_a_polynomial). If the polynomial is
     * zero, degree is -1.
     */
    public val P.degree: Int

    override fun add(left: P, right: P): P = left + right
    override fun multiply(left: P, right: P): P = left * right
}

/**
 * Abstraction of ring of polynomials of type [P] over ring of constants of type [C]. It also assumes that there is
 * provided [ring] (of type [A]), that provides constant-wise operations.
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param P the type of polynomials.
 * @param A the type of algebraic structure (precisely, of ring) provided for constants.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface PolynomialSpaceOverRing<C, P, out A: Ring<C>> : PolynomialSpace<C, P> {

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
    override fun power(arg: C, exponent: UInt): C = ring { power(arg, exponent) }

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
 * Abstraction of ring of polynomials of type [P] of variables of type [V] and over ring of constants of type [C].
 *
 * @param C the type of constants. Polynomials have them as coefficients in their terms.
 * @param V the type of variables. Polynomials have them in representations of terms.
 * @param P the type of polynomials.
 */
@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<C, V, P>: PolynomialSpace<C, P> {
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
    public operator fun V.unaryPlus(): P
    /**
     * Returns negation of representation of the variable as a monic monomial.
     */
    @JvmName("unaryMinusVariable")
    public operator fun V.unaryMinus(): P
    /**
     * Returns sum of the variables represented as monic monomials.
     */
    @JvmName("plusVariableVariable")
    public operator fun V.plus(other: V): P
    /**
     * Returns difference between the variables represented as monic monomials.
     */
    @JvmName("minusVariableVariable")
    public operator fun V.minus(other: V): P
    /**
     * Returns product of the variables represented as monic monomials.
     */
    @JvmName("timesVariableVariable")
    public operator fun V.times(other: V): P

    /**
     * Represents the [variable] as a monic monomial.
     */
    @JvmName("numberVariable")
    public fun number(variable: V): P = +variable
    /**
     * Represents the variable as a monic monomial.
     */
    @JvmName("asPolynomialVariable")
    public fun V.asPolynomial(): P = number(this)

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
}