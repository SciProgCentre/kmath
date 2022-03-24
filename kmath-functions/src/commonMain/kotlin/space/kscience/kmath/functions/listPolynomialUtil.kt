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
 * Creates a [ListPolynomialSpace] over a received ring.
 */
public fun <C, A : Ring<C>> A.listPolynomial(): ListPolynomialSpace<C, A> =
    ListPolynomialSpace(this)

/**
 * Creates a [ListPolynomialSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.listPolynomial(block: ListPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ListPolynomialSpace(this).block()
}

/**
 * Creates a [ScalableListPolynomialSpace] over a received scalable ring.
 */
public fun <C, A> A.scalableListPolynomial(): ScalableListPolynomialSpace<C, A> where A : Ring<C>, A : ScaleOperations<C> =
    ScalableListPolynomialSpace(this)

/**
 * Creates a [ScalableListPolynomialSpace]'s scope over a received scalable ring.
 */
public inline fun <C, A, R> A.scalableListPolynomial(block: ScalableListPolynomialSpace<C, A>.() -> R): R where A : Ring<C>, A : ScaleOperations<C> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ScalableListPolynomialSpace(this).block()
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
public fun ListPolynomial<Double>.substitute(arg: Double): Double =
    coefficients.reduceIndexedOrNull { index, acc, c ->
        acc + c * arg.pow(index)
    } ?: .0

/**
 * Evaluates the value of the given polynomial for given argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: C): C = ring {
    if (coefficients.isEmpty()) return@ring zero
    var result: C = coefficients.last()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: ListPolynomial<C>) : ListPolynomial<C> = ring {
    if (coefficients.isEmpty()) return ListPolynomial(emptyList())

    val thisDegree = coefficients.lastIndex
    if (thisDegree == -1) return ListPolynomial(emptyList())
    val argDegree = arg.coefficients.lastIndex
    if (argDegree == -1) return coefficients[0].asListPolynomial()
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

    return ListPolynomial<C>(resultCoefs)
}

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListPolynomial<C>.asFunction(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListPolynomial<C>.asPolynomialFunctionOver(ring: A): (ListPolynomial<C>) -> ListPolynomial<C> = { substitute(ring, it) }

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.derivative(
    algebra: A,
): ListPolynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = algebra {
    ListPolynomial(
        buildList(max(0, coefficients.size - 1)) {
            for (deg in 1 .. coefficients.lastIndex) add(number(deg) * coefficients[deg])
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.nthDerivative(
    algebra: A,
    order: Int,
): ListPolynomial<C> where A : Ring<C>, A : NumericAlgebra<C> = algebra {
    require(order >= 0) { "Order of derivative must be non-negative" }
    ListPolynomial(
        buildList(max(0, coefficients.size - order)) {
            for (deg in order.. coefficients.lastIndex)
                add((deg - order + 1 .. deg).fold(coefficients[deg]) { acc, d -> acc * number(d) })
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.antiderivative(
    algebra: A,
): ListPolynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    ListPolynomial(
        buildList(coefficients.size + 1) {
            add(zero)
            coefficients.mapIndexedTo(this) { index, t -> t / number(index + 1) }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.nthAntiderivative(
    algebra: A,
    order: Int,
): ListPolynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    require(order >= 0) { "Order of antiderivative must be non-negative" }
    ListPolynomial(
        buildList(coefficients.size + order) {
            repeat(order) { add(zero) }
            coefficients.mapIndexedTo(this) { index, c -> (1..order).fold(c) { acc, i -> acc / number(index + i) } }
        }
    )
}

/**
 * Compute a definite integral of a given polynomial in a [range]
 */
@UnstableKMathAPI
public fun <C : Comparable<C>> ListPolynomial<C>.integrate(
    algebra: Field<C>,
    range: ClosedRange<C>,
): C = algebra {
    val integral = antiderivative(algebra)
    integral.substitute(algebra, range.endInclusive) - integral.substitute(algebra, range.start)
}