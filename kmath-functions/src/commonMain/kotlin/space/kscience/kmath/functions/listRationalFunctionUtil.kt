/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max


/**
 * Creates a [ListRationalFunctionSpace] over a received ring.
 */
public fun <C, A : Ring<C>> A.listRationalFunction(): ListRationalFunctionSpace<C, A> =
    ListRationalFunctionSpace(this)

/**
 * Creates a [ListRationalFunctionSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.listRationalFunction(block: ListRationalFunctionSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ListRationalFunctionSpace(this).block()
}

/**
 * Evaluates the value of the given double polynomial for given double argument.
 */
public fun ListRationalFunction<Double>.substitute(arg: Double): Double =
    numerator.substitute(arg) / denominator.substitute(arg)

/**
 * Evaluates the value of the given polynomial for given argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> ListRationalFunction<C>.substitute(ring: Field<C>, arg: C): C = ring {
    numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
}

/**
 * Returns numerator (polynomial) of rational function gotten by substitution rational function [arg] to the polynomial instance.
 * More concrete, if [arg] is a fraction `f(x)/g(x)` and the receiving instance is `p(x)`, then
 * ```
 * p(f/g) * g^deg(p)
 * ```
 * is returned.
 *
 * Used in [ListPolynomial.substitute] and [ListRationalFunction.substitute] for performance optimisation.
 */ // TODO: Дописать
internal fun <C> ListPolynomial<C>.substituteRationalFunctionTakeNumerator(ring: Ring<C>, arg: ListRationalFunction<C>): ListPolynomial<C> = ring {
    if (coefficients.isEmpty()) return ListPolynomial(emptyList())

    val thisDegree = coefficients.indexOfLast { it != zero }
    if (thisDegree == -1) return ListPolynomial(emptyList())
    val thisDegreeLog2 = 31 - thisDegree.countLeadingZeroBits()
    val numeratorDegree = arg.numerator.coefficients.indexOfLast { it != zero }
    val denominatorDegree = arg.denominator.coefficients.indexOfLast { it != zero }
    val argDegree = max(numeratorDegree, denominatorDegree)
    val constantZero = zero
    val powersOf2 = buildList<Int>(thisDegreeLog2 + 1) {
        var result = 1
        for (exp in 0 .. thisDegreeLog2) {
            add(result)
            result = result shl 1
        }
    }
    val hashes = powersOf2.runningReduce { acc, i -> acc + i }
    val numeratorPowers = buildList<List<C>>(thisDegreeLog2 + 1) {
        add(arg.numerator.coefficients)
        repeat(thisDegreeLog2) {
            val next = MutableList<C>(powersOf2[it + 1] * numeratorDegree + 1) { constantZero }
            add(next)
            val last = last()
            multiplyAddingTo(
                ring = ring,
                multiplicand = last,
                multiplicandDegree = powersOf2[it] * numeratorDegree + 1,
                multiplier = last,
                multiplierDegree = powersOf2[it] * numeratorDegree + 1,
                target = next,
            )
        }
    }
    val denominatorPowers = buildList<List<C>>(thisDegreeLog2 + 1) {
        add(arg.denominator.coefficients)
        repeat(thisDegreeLog2) {
            val next = MutableList<C>(powersOf2[it + 1] * denominatorDegree + 1) { constantZero }
            add(next)
            val last = last()
            multiplyAddingTo(
                ring = ring,
                multiplicand = last,
                multiplicandDegree = powersOf2[it] * denominatorDegree + 1,
                multiplier = last,
                multiplierDegree = powersOf2[it] * denominatorDegree + 1,
                target = next,
            )
        }
    }
    val levelResultCoefsPool = buildList<MutableList<C>>(thisDegreeLog2 + 1) {
        repeat(thisDegreeLog2 + 1) {
            add(MutableList(hashes[it] * argDegree) { constantZero })
        }
    }
    val edgedMultiplier = MutableList<C>(0) { TODO() }
    val edgedMultiplierUpdater = MutableList<C>(0) { TODO() }

    fun MutableList<C>.reset() {
        for (i in indices) set(i, constantZero)
    }

    fun processLevel(level: Int, start: Int, end: Int) : List<C> {
        val levelResultCoefs = levelResultCoefsPool[level + 1]

        if (level == -1) {
            levelResultCoefs[0] = coefficients[start]
        } else {
            levelResultCoefs.reset()
            multiplyAddingTo(
                ring = ring,
                multiplicand = processLevel(level = level - 1, start = start, end = (start + end) / 2),
                multiplicandDegree =  hashes[level] * argDegree,
                multiplier = denominatorPowers[level],
                multiplierDegree = powersOf2[level] * denominatorDegree,
                target = levelResultCoefs
            )
            multiplyAddingTo(
                ring = ring,
                multiplicand = processLevel(level = level - 1, start = (start + end) / 2, end = end),
                multiplicandDegree = hashes[level] * argDegree,
                multiplier = numeratorPowers[level],
                multiplierDegree = powersOf2[level] * numeratorDegree,
                target = levelResultCoefs
            )
        }

        return levelResultCoefs
    }

    fun processLevelEdged(level: Int, start: Int, end: Int) : List<C> {
        val levelResultCoefs = levelResultCoefsPool[level + 1]

        if (level == -1) {
            levelResultCoefs[0] = coefficients[start]
        } else {
            val levelsPowerOf2 = powersOf2[level]
            if (end - start >= levelsPowerOf2) {
                multiplyAddingTo(
                    ring = ring,
                    multiplicand = processLevelEdged(level = level - 1, start = start + levelsPowerOf2, end = end),
                    multiplicandDegree = hashes[level] * argDegree, // TODO: Ввести переменную
                    multiplier = numeratorPowers[level],
                    multiplierDegree = powersOf2[level] * numeratorDegree,
                    target = levelResultCoefs
                )
                multiplyAddingTo(
                    ring = ring,
                    multiplicand = processLevel(level = level - 1, start = start, end = start + levelsPowerOf2),
                    multiplicandDegree = hashes[level] * argDegree,
                    multiplier = edgedMultiplier,
                    multiplierDegree = max((hashes[level] and thisDegree) - powersOf2[level] + 1, 0) * denominatorDegree, // TODO: Ввести переменную
                    target = levelResultCoefs
                )
                if (level != thisDegreeLog2) {
                    multiplyAddingToUpdater(
                        ring = ring,
                        multiplicand = edgedMultiplier,
                        multiplicandDegree = max((hashes[level] and thisDegree) - powersOf2[level] + 1, 0) * denominatorDegree, // TODO: Ввести переменную
                        multiplier = denominatorPowers[level],
                        multiplierDegree = powersOf2[level] * denominatorDegree,
                        updater = edgedMultiplierUpdater,
                        zero = constantZero
                    )
                }
            } else {
                copyTo(
                    origin = processLevelEdged(level = level - 1, start = start + levelsPowerOf2, end = end),
                    originDegree = hashes[level] * argDegree, // TODO: Ввести переменную
                    target = levelResultCoefs
                )
            }
        }

        return levelResultCoefs
    }

    return ListPolynomial(
        processLevelEdged(
            level = thisDegreeLog2,
            start = 0,
            end = thisDegree + 1
        )
    )
}

//operator fun <T: Field<T>> RationalFunction<T>.invoke(arg: T): T = numerator(arg) / denominator(arg)
//
//fun <T: Field<T>> RationalFunction<T>.reduced(): RationalFunction<T> =
//    polynomialGCD(numerator, denominator).let {
//        RationalFunction(
//            numerator / it,
//            denominator / it
//        )
//    }

///**
// * Returns result of applying formal derivative to the polynomial.
// *
// * @param T Field where we are working now.
// * @return Result of the operator.
// */
//fun <T: Ring<T>> RationalFunction<T>.derivative() =
//    RationalFunction(
//        numerator.derivative() * denominator - denominator.derivative() * numerator,
//        denominator * denominator
//    )