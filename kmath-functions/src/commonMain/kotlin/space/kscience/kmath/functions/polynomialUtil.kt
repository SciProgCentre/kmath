/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.pow


/**
 * Creates a [PolynomialSpace] over a received ring.
 */
public inline val <C, A> A.polynomialSpace: PolynomialSpace<C, A> where A : Ring<C>, A : ScaleOperations<C>
    get() = PolynomialSpace(this)

/**
 * Creates a [PolynomialSpace]'s scope over a received ring.
 */ // TODO: When context will be ready move [ListPolynomialSpace] and add [A] to context receivers of [block]
public inline fun <C, A, R> A.polynomialSpace(block: PolynomialSpace<C, A>.() -> R): R where A : Ring<C>, A : ScaleOperations<C> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return PolynomialSpace(this).block()
}


/**
 * Evaluates value of [this] Double polynomial on provided Double argument.
 */
public fun Polynomial<Double>.value(arg: Double): Double =
    coefficients.reduceIndexedOrNull { index, acc, c ->
        acc + c * arg.pow(index)
    } ?: .0

/**
 * Evaluates value of [this] polynomial on provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> Polynomial<C>.value(ring: Ring<C>, arg: C): C = ring {
    if (coefficients.isEmpty()) return zero
    var result: C = coefficients.last()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> Polynomial<C>.asFunctionOver(ring: A): (C) -> C = { value(ring, it) }

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.differentiate(
    ring: A,
): Polynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = ring {
    Polynomial(
        buildList(max(0, coefficients.size - 1)) {
            for (deg in 1 .. coefficients.lastIndex) add(number(deg) * coefficients[deg])
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> Polynomial<C>.integrate(
    ring: A,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = ring {
    Polynomial(
        buildList(coefficients.size + 1) {
            add(zero)
            coefficients.mapIndexedTo(this) { index, t -> t / number(index + 1) }
        }
    )
}

/**
 * Computes a definite integral of [this] polynomial in the specified [range].
 */
@UnstableKMathAPI
public fun <C : Comparable<C>> Polynomial<C>.integrate(
    ring: Field<C>,
    range: ClosedRange<C>,
): C = ring {
    val antiderivative = integrate(ring)
    return antiderivative.value(ring, range.endInclusive) - antiderivative.value(ring, range.start)
}