/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import kotlin.math.max
import kotlin.math.min


// TODO: Optimized copies of substitution and invocation
@UnstablePolynomialBoxingOptimization
@Suppress("NOTHING_TO_INLINE")
internal inline fun <C> copyTo(
    origin: List<C>,
    originDegree: Int,
    target: MutableList<C>,
) {
    for (deg in 0 .. originDegree) target[deg] = origin[deg]
}

@UnstablePolynomialBoxingOptimization
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

@UnstablePolynomialBoxingOptimization
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

@UnstablePolynomialBoxingOptimization
public fun <C> ListPolynomial<C>.substitute2(ring: Ring<C>, arg: ListPolynomial<C>) : ListPolynomial<C> = ring {
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
 * Returns numerator (polynomial) of rational function gotten by substitution rational function [arg] to the polynomial instance.
 * More concrete, if [arg] is a fraction `f(x)/g(x)` and the receiving instance is `p(x)`, then
 * ```
 * p(f/g) * g^deg(p)
 * ```
 * is returned.
 *
 * Used in [ListPolynomial.substitute] and [ListRationalFunction.substitute] for performance optimisation.
 */ // TODO: Дописать
@UnstablePolynomialBoxingOptimization
internal fun <C> ListPolynomial<C>.substituteRationalFunctionTakeNumerator(ring: Ring<C>, arg: ListRationalFunction<C>): ListPolynomial<C> = ring {
    if (coefficients.isEmpty()) return ListPolynomial(emptyList())

    val thisDegree = coefficients.lastIndex
    if (thisDegree == -1) return ListPolynomial(emptyList())
    val thisDegreeLog2 = 31 - thisDegree.countLeadingZeroBits()
    val numeratorDegree = arg.numerator.coefficients.lastIndex
    val denominatorDegree = arg.denominator.coefficients.lastIndex
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