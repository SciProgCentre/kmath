/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlin.math.pow


// region Utilities

/**
 * Removes zeros on the end of the coefficient list of polynomial.
 */
//context(PolynomialSpace<C, A>)
//fun <C, A: Ring<C>> Polynomial<C>.removeZeros() : Polynomial<C> =
//    if (degree > -1) Polynomial(coefficients.subList(0, degree + 1)) else zero

/**
 * Crates a [PolynomialSpace] over received ring.
 */
public fun <C, A : Ring<C>> A.polynomial(): PolynomialSpace<C, A> =
    PolynomialSpace(this)

/**
 * Crates a [PolynomialSpace]'s scope over received ring.
 */
public inline fun <C, A : Ring<C>, R> A.polynomial(block: PolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return PolynomialSpace(this).block()
}

/**
 * Crates a [ScalablePolynomialSpace] over received scalable ring.
 */
public fun <C, A> A.scalablePolynomial(): ScalablePolynomialSpace<C, A> where A : Ring<C>, A : ScaleOperations<C> =
    ScalablePolynomialSpace(this)

/**
 * Crates a [ScalablePolynomialSpace]'s scope over received scalable ring.
 */
public inline fun <C, A, R> A.scalablePolynomial(block: ScalablePolynomialSpace<C, A>.() -> R): R where A : Ring<C>, A : ScaleOperations<C> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ScalablePolynomialSpace(this).block()
}

// endregion

// region Polynomial substitution and functional representation

// TODO: May be apply Horner's method too?
/**
 * Evaluates the value of the given double polynomial for given double argument.
 */
public fun Polynomial<Double>.substitute(arg: Double): Double =
    coefficients.reduceIndexedOrNull { index, acc, c ->
        acc + c * arg.pow(index)
    } ?: .0

/**
 * Evaluates the value of the given polynomial for given argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> Polynomial<C>.substitute(ring: Ring<C>, arg: C): C = ring {
    if (coefficients.isEmpty()) return@ring zero
    var result: C = coefficients.last()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

// TODO: (Waiting for hero) Replace with optimisation: the [result] may be unboxed, and all operations may be performed
//  as soon as possible on it
public fun <C> Polynomial<C>.substitute(ring: Ring<C>, arg: Polynomial<C>) : Polynomial<C> = ring.polynomial {
    if (coefficients.isEmpty()) return zero
    var result: Polynomial<C> = coefficients.last().asPolynomial()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> Polynomial<C>.asFunction(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> Polynomial<C>.asPolynomialFunctionOver(ring: A): (Polynomial<C>) -> Polynomial<C> = { substitute(ring, it) }

// endregion

// region Algebraic derivative and antiderivative

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.derivative(
    algebra: A,
): Polynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = algebra {
    Polynomial(coefficients.drop(1).mapIndexed { index, t -> number(index) * t })
}

/**
 * Create a polynomial witch represents indefinite integral version of this polynomial
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.antiderivative(
    algebra: A,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    val integratedCoefficients = buildList(coefficients.size + 1) {
        add(zero)
        coefficients.forEachIndexed{ index, t -> add(t / (number(index) + one)) }
    }
    Polynomial(integratedCoefficients)
}

/**
 * Compute a definite integral of a given polynomial in a [range]
 */
@UnstableKMathAPI
public fun <C : Comparable<C>> Polynomial<C>.integrate(
    algebra: Field<C>,
    range: ClosedRange<C>,
): C = algebra {
    val integral = antiderivative(algebra)
    integral.substitute(algebra, range.endInclusive) - integral.substitute(algebra, range.start)
}