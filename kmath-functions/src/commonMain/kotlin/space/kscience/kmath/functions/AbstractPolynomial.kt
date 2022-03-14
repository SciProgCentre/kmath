/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.js.JsName
import kotlin.jvm.JvmName


/**
 * Abstraction of polynomials.
 */
public interface AbstractPolynomial<C>

/**
 * Abstraction of ring of polynomials of type [P] over ring of constants of type [C].
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param P the type of polynomials.
 */
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public interface AbstractPolynomialSpace<C, P: AbstractPolynomial<C>> : Ring<P> {
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
     * Returns sum of the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public operator fun P.plus(other: Int): P = optimizedAddMultiplied(this, one, other)
    /**
     * Returns difference between the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public operator fun P.minus(other: Int): P = optimizedAddMultiplied(this, one, -other)
    /**
     * Returns product of the polynomial and the integer represented as polynomial.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public operator fun P.times(other: Int): P = optimizedMultiply(this, other)
    // endregion

    // region Integer-polynomial relation
    /**
     * Returns sum of the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public operator fun Int.plus(other: P): P = optimizedAddMultiplied(other, one, this)
    /**
     * Returns difference between the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public operator fun Int.minus(other: P): P = optimizedAddMultiplied(-other, one, this)
    /**
     * Returns product of the integer represented as polynomial and the polynomial.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public operator fun Int.times(other: P): P = optimizedMultiply(other, this)
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
    public fun C.isZero(): Boolean
    /**
     * Check if the instant is NOT zero constant.
     */
    @JvmName("constantIsNotZero")
    public fun C.isNotZero(): Boolean = !isZero()
    /**
     * Check if the instant is unit constant.
     */
    @JvmName("constantIsOne")
    public fun C.isOne(): Boolean
    /**
     * Check if the instant is NOT unit constant.
     */
    @JvmName("constantIsNotOne")
    public fun C.isNotOne(): Boolean = !isOne()
    /**
     * Check if the instant is minus unit constant.
     */
    @JvmName("constantIsMinusOne")
    public fun C.isMinusOne(): Boolean
    /**
     * Check if the instant is NOT minus unit constant.
     */
    @JvmName("constantIsNotMinusOne")
    public fun C.isNotMinusOne(): Boolean = !isMinusOne()
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
     * Check if the instant is zero polynomial.
     */
    public fun P.isZero(): Boolean = this equalsTo zero
    /**
     * Check if the instant is NOT zero polynomial.
     */
    public fun P.isNotZero(): Boolean = !isZero()
    /**
     * Check if the instant is unit polynomial.
     */
    public fun P.isOne(): Boolean = this equalsTo one
    /**
     * Check if the instant is NOT unit polynomial.
     */
    public fun P.isNotOne(): Boolean = !isOne()
    /**
     * Check if the instant is minus unit polynomial.
     */
    public fun P.isMinusOne(): Boolean = this equalsTo -one
    /**
     * Check if the instant is NOT minus unit polynomial.
     */
    public fun P.isNotMinusOne(): Boolean = !isMinusOne()

    /**
     * Instance of zero polynomial (zero of the polynomial ring).
     */
    public override val zero: P
    /**
     * Instance of unit polynomial (unit of the polynomial ring).
     */
    public override val one: P

    /**
     * Checks equality of the polynomials.
     */
    public infix fun P.equalsTo(other: P): Boolean
    /**
     * Checks NOT equality of the polynomials.
     */
    public infix fun P.notEqualsTo(other: P): Boolean = !(this equalsTo other)
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

    // region Legacy of Ring interface
    override fun add(left: P, right: P): P = left + right
    override fun multiply(left: P, right: P): P = left * right
    // endregion
}

/**
 * Abstraction of ring of polynomials of type [P] over ring of constants of type [C].
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param P the type of polynomials.
 */
@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public interface AbstractPolynomialSpaceOverRing<C, P: AbstractPolynomial<C>, A: Ring<C>> : AbstractPolynomialSpace<C, P> {

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
     * Check if the instant is zero constant.
     */
    @JvmName("constantIsZero")
    public override fun C.isZero(): Boolean = ring { this == zero }
    /**
     * Check if the instant is unit constant.
     */
    @JvmName("constantIsOne")
    public override fun C.isOne(): Boolean = ring { this == one }
    /**
     * Check if the instant is minus unit constant.
     */
    @JvmName("constantIsMinusOne")
    public override fun C.isMinusOne(): Boolean = ring { this == -one }
    // endregion
}