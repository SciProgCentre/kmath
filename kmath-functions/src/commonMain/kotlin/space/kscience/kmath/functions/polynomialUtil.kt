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
internal inline fun <C> copyTo(
    origin: List<C>,
    originDegree: Int,
    target: MutableList<C>,
) {
    for (deg in 0 .. originDegree) target[deg] = origin[deg]
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <C> multiplyAddingToUpdater(
    ring: Ring<C>,
    multiplicand: MutableList<C>,
    multiplicandDegree: Int,
    multiplier: List<C>,
    multiplierDegree: Int,
    updater: MutableList<C>,
    zero: C,
) {
    multiplyAddingTo(
        ring = ring,
        multiplicand = multiplicand,
        multiplicandDegree = multiplicandDegree,
        multiplier = multiplier,
        multiplierDegree = multiplierDegree,
        target = updater
    )
    for (updateDeg in 0 .. multiplicandDegree + multiplierDegree) {
        multiplicand[updateDeg] = updater[updateDeg]
        updater[updateDeg] = zero
    }
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

public fun <C> Polynomial<C>.substitute(ring: Ring<C>, arg: Polynomial<C>) : Polynomial<C> = ring {
    if (coefficients.isEmpty()) return Polynomial(emptyList())

    val thisDegree = coefficients.indexOfLast { it != zero }
    if (thisDegree == -1) return Polynomial(emptyList())
    val argDegree = arg.coefficients.indexOfLast { it != zero }
    if (argDegree == -1) return coefficients[0].asPolynomial()
    val constantZero = zero
    val resultCoefs: MutableList<C> = MutableList(thisDegree * argDegree + 1) { constantZero }
    resultCoefs[0] = coefficients[thisDegree]
    val resultCoefsUpdate: MutableList<C> = MutableList(thisDegree * argDegree + 1) { constantZero }
    var resultDegree = 0
    for (deg in thisDegree - 1 downTo 0) {
        resultCoefsUpdate[0] = coefficients[deg]
        multiplyAddingToUpdater(
            ring = ring,
            multiplicand = resultCoefs,
            multiplicandDegree = resultDegree,
            multiplier = arg.coefficients,
            multiplierDegree = argDegree,
            updater = resultCoefsUpdate,
            zero = constantZero
        )
        resultDegree += argDegree
    }

    with(resultCoefs) { while (isNotEmpty() && elementAt(lastIndex) == constantZero) removeAt(lastIndex) }
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
    Polynomial(
        buildList(max(0, coefficients.size - 1)) {
            for (deg in 1 .. coefficients.lastIndex) add(number(deg) * coefficients[deg])
            while (isNotEmpty() && elementAt(lastIndex) == algebra.zero) removeAt(lastIndex)
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.nthDerivative(
    algebra: A,
    order: Int,
): Polynomial<C> where A : Ring<C>, A : NumericAlgebra<C> = algebra {
    require(order >= 0) { "Order of derivative must be non-negative" }
    Polynomial(
        buildList(max(0, coefficients.size - order)) {
            for (deg in order.. coefficients.lastIndex)
                add((deg - order + 1 .. deg).fold(coefficients[deg]) { acc, d -> acc * number(d) })
            while (isNotEmpty() && elementAt(lastIndex) == algebra.zero) removeAt(lastIndex)
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.antiderivative(
    algebra: A,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    Polynomial(
        buildList(coefficients.size + 1) {
            add(zero)
            coefficients.mapIndexedTo(this) { index, t -> t / number(index + 1) }
            while (isNotEmpty() && elementAt(lastIndex) == algebra.zero) removeAt(lastIndex)
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.nthAntiderivative(
    algebra: A,
    order: Int,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    require(order >= 0) { "Order of antiderivative must be non-negative" }
    Polynomial(
        buildList(coefficients.size + order) {
            repeat(order) { add(zero) }
            coefficients.mapIndexedTo(this) { index, c -> (1..order).fold(c) { acc, i -> acc / number(index + i) } }
            while (isNotEmpty() && elementAt(lastIndex) == algebra.zero) removeAt(lastIndex)
        }
    )
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