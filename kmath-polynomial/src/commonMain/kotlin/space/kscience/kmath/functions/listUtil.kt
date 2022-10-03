/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.pow


/**
 * Creates a [ListPolynomialSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.listPolynomialSpace: ListPolynomialSpace<C, A>
    get() = ListPolynomialSpace(this)

/**
 * Creates a [ListPolynomialSpace]'s scope over a received ring.
 */ // TODO: When context will be ready move [ListPolynomialSpace] and add [A] to context receivers of [block]
public inline fun <C, A : Ring<C>, R> A.listPolynomialSpace(block: ListPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ListPolynomialSpace(this).block()
}

/**
 * Creates a [ScalableListPolynomialSpace] over a received scalable ring.
 */
public inline val <C, A> A.scalableListPolynomialSpace: ScalableListPolynomialSpace<C, A> where A : Ring<C>, A : ScaleOperations<C>
    get() = ScalableListPolynomialSpace(this)

/**
 * Creates a [ScalableListPolynomialSpace]'s scope over a received scalable ring.
 */ // TODO: When context will be ready move [ListPolynomialSpace] and add [A] to context receivers of [block]
public inline fun <C, A, R> A.scalableListPolynomialSpace(block: ScalableListPolynomialSpace<C, A>.() -> R): R where A : Ring<C>, A : ScaleOperations<C> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ScalableListPolynomialSpace(this).block()
}

/**
 * Creates a [ListRationalFunctionSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.listRationalFunctionSpace: ListRationalFunctionSpace<C, A>
    get() = ListRationalFunctionSpace(this)

/**
 * Creates a [ListRationalFunctionSpace]'s scope over a received ring.
 */ // TODO: When context will be ready move [ListRationalFunctionSpace] and add [A] to context receivers of [block]
public inline fun <C, A : Ring<C>, R> A.listRationalFunctionSpace(block: ListRationalFunctionSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ListRationalFunctionSpace(this).block()
}


/**
 * Evaluates value of [this] Double polynomial on provided Double argument.
 */
public fun ListPolynomial<Double>.substitute(arg: Double): Double =
    coefficients.reduceIndexedOrNull { index, acc, c ->
        acc + c * arg.pow(index)
    } ?: .0

/**
 * Evaluates value of [this] polynomial on provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: C): C = ring {
    if (coefficients.isEmpty()) return zero
    var result: C = coefficients.last()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Substitutes provided polynomial [arg] into [this] polynomial.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */ // TODO: To optimize boxing
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: ListPolynomial<C>) : ListPolynomial<C> =
    ring.listPolynomialSpace {
        if (coefficients.isEmpty()) return zero
        var result: ListPolynomial<C> = coefficients.last().asPolynomial()
        for (j in coefficients.size - 2 downTo 0) {
            result = (arg * result) + coefficients[j]
        }
        return result
    }

/**
 * Substitutes provided rational function [arg] into [this] polynomial.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */ // TODO: To optimize boxing
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: ListRationalFunction<C>) : ListRationalFunction<C> =
    ring.listRationalFunctionSpace {
        if (coefficients.isEmpty()) return zero
        var result: ListRationalFunction<C> = coefficients.last().asRationalFunction()
        for (j in coefficients.size - 2 downTo 0) {
            result = (arg * result) + coefficients[j]
        }
        return result
    }

/**
 * Evaluates value of [this] Double rational function in provided Double argument.
 */
public fun ListRationalFunction<Double>.substitute(arg: Double): Double =
    numerator.substitute(arg) / denominator.substitute(arg)

/**
 * Evaluates value of [this] polynomial for provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> ListRationalFunction<C>.substitute(ring: Field<C>, arg: C): C = ring {
    numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
}

/**
 * Substitutes provided polynomial [arg] into [this] rational function.
 */ // TODO: To optimize boxing
public fun <C> ListRationalFunction<C>.substitute(ring: Ring<C>, arg: ListPolynomial<C>) : ListRationalFunction<C> =
    ring.listRationalFunctionSpace {
        numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
    }

/**
 * Substitutes provided rational function [arg] into [this] rational function.
 */ // TODO: To optimize boxing
public fun <C> ListRationalFunction<C>.substitute(ring: Ring<C>, arg: ListRationalFunction<C>) : ListRationalFunction<C> =
    ring.listRationalFunctionSpace {
        numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
    }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOver(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfConstantOver(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfPolynomialOver(ring: A): (ListPolynomial<C>) -> ListPolynomial<C> = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListPolynomial<C>.asFunctionOfRationalFunctionOver(ring: A): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Field<C>> ListRationalFunction<C>.asFunctionOver(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Field<C>> ListRationalFunction<C>.asFunctionOfConstantOver(ring: A): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListRationalFunction<C>.asFunctionOfPolynomialOver(ring: A): (ListPolynomial<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Ring<C>> ListRationalFunction<C>.asFunctionOfRationalFunctionOver(ring: A): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.derivative(
    ring: A,
): ListPolynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = ring {
    ListPolynomial(
        buildList(max(0, coefficients.size - 1)) {
            for (deg in 1 .. coefficients.lastIndex) add(number(deg) * coefficients[deg])
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial of specified [order]. The [order] should be non-negative integer.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.nthDerivative(
    ring: A,
    order: Int,
): ListPolynomial<C> where A : Ring<C>, A : NumericAlgebra<C> = ring {
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
    ring: A,
): ListPolynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = ring {
    ListPolynomial(
        buildList(coefficients.size + 1) {
            add(zero)
            coefficients.mapIndexedTo(this) { index, t -> t / number(index + 1) }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial of specified [order]. The [order] should be non-negative integer.
 */
@UnstableKMathAPI
public fun <C, A> ListPolynomial<C>.nthAntiderivative(
    ring: A,
    order: Int,
): ListPolynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = ring {
    require(order >= 0) { "Order of antiderivative must be non-negative" }
    ListPolynomial(
        buildList(coefficients.size + order) {
            repeat(order) { add(zero) }
            coefficients.mapIndexedTo(this) { index, c -> (1..order).fold(c) { acc, i -> acc / number(index + i) } }
        }
    )
}

/**
 * Computes a definite integral of [this] polynomial in the specified [range].
 */
@UnstableKMathAPI
public fun <C : Comparable<C>> ListPolynomial<C>.integrate(
    ring: Field<C>,
    range: ClosedRange<C>,
): C = ring {
    val antiderivative = antiderivative(ring)
    antiderivative.substitute(ring, range.endInclusive) - antiderivative.substitute(ring, range.start)
}