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
import kotlin.math.pow

/**
 * Polynomial coefficients model without fixation on specific context they are applied to.
 *
 * @param coefficients constant is the leftmost coefficient.
 */
public class Polynomial<T>(public val coefficients: List<T>) {
    override fun toString(): String = "Polynomial$coefficients"
}

/**
 * Returns a [Polynomial] instance with given [coefficients].
 */
@Suppress("FunctionName")
public fun <T> Polynomial(vararg coefficients: T): Polynomial<T> = Polynomial(coefficients.toList())

/**
 * Evaluates the value of the given double polynomial for given double argument.
 */
public fun Polynomial<Double>.value(arg: Double): Double = coefficients.reduceIndexed { index, acc, c ->
    acc + c * arg.pow(index)
}

/**
 * Evaluates the value of the given polynomial for given argument.
 * https://en.wikipedia.org/wiki/Horner%27s_method
 */
public fun <T, C : Ring<T>> Polynomial<T>.value(ring: C, arg: T): T = ring {
    if (coefficients.isEmpty()) return@ring zero
    var result: T = coefficients.last()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <T, C : Ring<T>> Polynomial<T>.asFunction(ring: C): (T) -> T = { value(ring, it) }

/**
 * Create a polynomial witch represents differentiated version of this polynomial
 */
@UnstableKMathAPI
public fun <T, A> Polynomial<T>.differentiate(
    algebra: A,
): Polynomial<T> where  A : Ring<T>, A : NumericAlgebra<T> = algebra {
    Polynomial(coefficients.drop(1).mapIndexed { index, t -> number(index) * t })
}

/**
 * Create a polynomial witch represents indefinite integral version of this polynomial
 */
@UnstableKMathAPI
public fun <T, A> Polynomial<T>.integrate(
    algebra: A,
): Polynomial<T> where  A : Field<T>, A : NumericAlgebra<T> = algebra {
    Polynomial(coefficients.mapIndexed { index, t -> t / number(index) })
}

/**
 * Compute a definite integral of a given polynomial in a [range]
 */
@UnstableKMathAPI
public fun <T : Comparable<T>, A> Polynomial<T>.integrate(
    algebra: A,
    range: ClosedRange<T>,
): T where  A : Field<T>, A : NumericAlgebra<T> = algebra {
    value(algebra, range.endInclusive) - value(algebra, range.start)
}

/**
 * Space of polynomials.
 *
 * @param T the type of operated polynomials.
 * @param C the intersection of [Ring] of [T] and [ScaleOperations] of [T].
 * @param ring the [C] instance.
 */
public class PolynomialSpace<T, C>(
    private val ring: C,
) : Group<Polynomial<T>>, ScaleOperations<Polynomial<T>> where C : Ring<T>, C : ScaleOperations<T> {
    public override val zero: Polynomial<T> = Polynomial(emptyList())

    override fun Polynomial<T>.unaryMinus(): Polynomial<T> = ring {
        Polynomial(coefficients.map { -it })
    }

    public override fun add(a: Polynomial<T>, b: Polynomial<T>): Polynomial<T> {
        val dim = max(a.coefficients.size, b.coefficients.size)

        return ring {
            Polynomial(List(dim) { index ->
                a.coefficients.getOrElse(index) { zero } + b.coefficients.getOrElse(index) { zero }
            })
        }
    }

    public override fun scale(a: Polynomial<T>, value: Double): Polynomial<T> =
        ring { Polynomial(List(a.coefficients.size) { index -> a.coefficients[index] * value }) }

    /**
     * Evaluates the polynomial for the given value [arg].
     */
    public operator fun Polynomial<T>.invoke(arg: T): T = value(ring, arg)

    public fun Polynomial<T>.asFunction(): (T) -> T = asFunction(ring)

}

public inline fun <T, C, R> C.polynomial(block: PolynomialSpace<T, C>.() -> R): R where C : Ring<T>, C : ScaleOperations<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return PolynomialSpace(this).block()
}
