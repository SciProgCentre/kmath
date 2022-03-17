/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


/**
 * Removes zeros on the end of the coefficient list of polynomial.
 */
//context(PolynomialSpace<C, A>)
//fun <C, A: Ring<C>> Polynomial<C>.removeZeros() : Polynomial<C> =
//    if (degree > -1) Polynomial(coefficients.subList(0, degree + 1)) else zero

/**
 * Creates a [PolynomialSpace] over a received ring.
 */
public fun <C, A : Ring<C>> A.polynomial(): PolynomialSpace<C, A> =
    PolynomialSpace(this)

/**
 * Creates a [PolynomialSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.polynomial(block: PolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return PolynomialSpace(this).block()
}

/**
 * Creates a [ScalablePolynomialSpace] over a received scalable ring.
 */
public fun <C, A> A.scalablePolynomial(): ScalablePolynomialSpace<C, A> where A : Ring<C>, A : ScaleOperations<C> =
    ScalablePolynomialSpace(this)

/**
 * Creates a [ScalablePolynomialSpace]'s scope over a received scalable ring.
 */
public inline fun <C, A, R> A.scalablePolynomial(block: ScalablePolynomialSpace<C, A>.() -> R): R where A : Ring<C>, A : ScaleOperations<C> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ScalablePolynomialSpace(this).block()
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <C> iadd(
    ring: Ring<C>,
    augend: MutableList<C>,
    addend: List<C>,
    degree: Int
) = ring {
    for (deg in 0 .. degree) augend[deg] += addend[deg]
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <C> addTo(
    ring: Ring<C>,
    augend: List<C>,
    addend: List<C>,
    degree: Int,
    target: MutableList<C>
) = ring {
    for (deg in 0 .. degree) target[deg] = augend[deg] + addend[deg]
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <C> multiplyAddingTo(
    ring: Ring<C>,
    multiplicand: List<C>,
    multiplicandDegree: Int,
    multiplier: List<C>,
    multiplierDegree: Int,
    target: MutableList<C>
) = ring {
    for (d in 0 .. multiplicandDegree + multiplierDegree)
        for (k in max(0, d - multiplierDegree)..min(multiplicandDegree, d))
            target[d] += multiplicand[k] * multiplier[d - k]
}

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

public fun <C> Polynomial<C>.substitute(ring: Ring<C>, arg: Polynomial<C>) : Polynomial<C> = ring.polynomial {
    if (coefficients.isEmpty()) return zero

    val thisDegree = degree
    if (thisDegree == -1) return zero
    val argDegree = arg.degree
    if (argDegree == -1) return coefficients[0].asPolynomial()
    val constantZero = constantZero
    val resultCoefs: MutableList<C> = MutableList(thisDegree * argDegree + 1) { constantZero }
    val resultCoefsUpdate: MutableList<C> = MutableList(thisDegree * argDegree + 1) { constantZero }
    var resultDegree = 0
    for (deg in thisDegree downTo 0) {
        resultCoefsUpdate[0] = coefficients[deg]
        multiplyAddingTo(
            ring=ring,
            multiplicand = resultCoefs,
            multiplicandDegree = resultDegree,
            multiplier = arg.coefficients,
            multiplierDegree = argDegree,
            target = resultCoefsUpdate
        )
        resultDegree += argDegree
        for (updateDeg in 0 .. resultDegree) {
            resultCoefs[updateDeg] = resultCoefsUpdate[updateDeg]
            resultCoefsUpdate[updateDeg] = constantZero
        }
    }
    return Polynomial<C>(resultCoefs)
}

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> Polynomial<C>.asFunction(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> Polynomial<C>.asPolynomialFunctionOver(ring: A): (Polynomial<C>) -> Polynomial<C> = { substitute(ring, it) }

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.derivative(
    algebra: A,
): Polynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = algebra {
    Polynomial(coefficients.drop(1).mapIndexed { index, c -> number(index + 1) * c })
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.nthDerivative(
    algebra: A,
    order: UInt,
): Polynomial<C> where A : Ring<C>, A : NumericAlgebra<C> = algebra {
    Polynomial(coefficients.drop(order.toInt()).mapIndexed { index, c -> (index + 1..index + order.toInt()).fold(c) { acc, i -> acc * number(i) } })
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.antiderivative(
    algebra: A,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    val integratedCoefficients = buildList(coefficients.size + 1) {
        add(zero)
        coefficients.mapIndexedTo(this) { index, t -> t / number(index + 1) }
    }
    Polynomial(integratedCoefficients)
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.nthAntiderivative(
    algebra: A,
    order: UInt,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    val newCoefficients = buildList(coefficients.size + order.toInt()) {
        repeat(order.toInt()) { add(zero) }
        coefficients.mapIndexedTo(this) { index, c -> (1..order.toInt()).fold(c) { acc, i -> acc / number(index + i) } }
    }
    return Polynomial(newCoefficients)
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