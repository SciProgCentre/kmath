/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.contracts.*


// TODO: Docs

// region Sort of legacy

//// region Constants
//
//// TODO: Reuse underlying ring extensions
//
//context(LabeledPolynomialSpace<C, A>)
//@Suppress("NOTHING_TO_INLINE")
//fun <C, A: Ring<C>> numberConstant(value: Int): C = ring { number<C>(value) }
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> power(arg: C, pow: UInt): C = ring { power(arg, pow) }
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> multiplyWithPower(base: C, arg: C, pow: UInt): C = ring { multiplyWithPower<C>(base, arg, pow) }
//
//// endregion

//// region Variables
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> power(arg: Variable, pow: UInt): LabeledPolynomial<C> =
//    if (pow == 0U) one
//    else LabeledPolynomial<C>(mapOf(
//        mapOf(arg to pow) to constantOne
//    ))
//
//// endregion

//// region Polynomials
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> number(value: Int): LabeledPolynomial<C> = ring { LabeledPolynomial<C>(mapOf(emptyMap<Variable, UInt>() to number<C>(value))) }
//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> multiplyWithPower(base: LabeledPolynomial<C>, arg: LabeledPolynomial<C>, pow: UInt): LabeledPolynomial<C> =
//    when {
//        arg.isZero() && pow > 0U -> base
//        arg.isOne() -> base
//        arg.isMinusOne() -> if (pow % 2U == 0U) base else -base
//        else -> multiplyWithPowerInternalLogic(base, arg, pow)
//    }
//
//// Trivial but very slow
//context(LabeledPolynomialSpace<C, A>)
//internal tailrec fun <C, A: Ring<C>> multiplyWithPowerInternalLogic(base: LabeledPolynomial<C>, arg: LabeledPolynomial<C>, exponent: UInt): LabeledPolynomial<C> =
//    when {
//        exponent == 0U -> base
//        exponent == 1U -> base * arg
//        exponent % 2U == 0U -> multiplyWithPowerInternalLogic(base, arg * arg, exponent / 2U)
//        exponent % 2U == 1U -> multiplyWithPowerInternalLogic(base * arg, arg * arg, exponent / 2U)
//        else -> error("Error in raising ring instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
//    }
//
//// endregion

// endregion

// region Utilities

/**
 * Crates a [LabeledPolynomialSpace] over received ring.
 */
public fun <C, A : Ring<C>> A.labeledPolynomial(): LabeledPolynomialSpace<C, A> =
    LabeledPolynomialSpace(this)

/**
 * Crates a [LabeledPolynomialSpace]'s scope over received ring.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <C, A : Ring<C>, R> A.labeledPolynomial(block: LabeledPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return LabeledPolynomialSpace(this).block()
}

// endregion

//// region String representations
//
///**
// * Represents the polynomial as a [String] with names of variables substituted with names from [names].
// * Consider that monomials are sorted in lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.represent(names: Map<Variable, String> = emptyMap()): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> LabeledPolynomial.monomialComparator.compare(o1.key, o2.key) }
//        .asSequence()
//        .map { (degs, t) ->
//            if (degs.isEmpty()) "$t"
//            else {
//                when {
//                    t.isOne() -> ""
//                    t.isMinusOne() -> "-"
//                    else -> "$t "
//                } +
//                        degs
//                            .toSortedMap()
//                            .filter { it.value > 0U }
//                            .map { (variable, deg) ->
//                                val variableName = names.getOrDefault(variable, variable.toString())
//                                when (deg) {
//                                    1U -> variableName
//                                    else -> "$variableName^$deg"
//                                }
//                            }
//                            .joinToString(separator = " ") { it }
//            }
//        }
//        .joinToString(separator = " + ") { it }
//        .ifEmpty { "0" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer].
// * Consider that monomials are sorted in lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.represent(namer: (Variable) -> String): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> LabeledPolynomial.monomialComparator.compare(o1.key, o2.key) }
//        .asSequence()
//        .map { (degs, t) ->
//            if (degs.isEmpty()) "$t"
//            else {
//                when {
//                    t.isOne() -> ""
//                    t.isMinusOne() -> "-"
//                    else -> "$t "
//                } +
//                        degs
//                            .toSortedMap()
//                            .filter { it.value > 0U }
//                            .map { (variable, deg) ->
//                                when (deg) {
//                                    1U -> namer(variable)
//                                    else -> "${namer(variable)}^$deg"
//                                }
//                            }
//                            .joinToString(separator = " ") { it }
//            }
//        }
//        .joinToString(separator = " + ") { it }
//        .ifEmpty { "0" }
//
///**
// * Represents the polynomial as a [String] with names of variables substituted with names from [names] and with
// * brackets around the string if needed (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representWithBrackets(names: Map<Variable, String> = emptyMap()): String =
//    with(represent(names)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer] and with brackets around the string if needed
// * (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representWithBrackets(namer: (Variable) -> String): String =
//    with(represent(namer)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] with names of variables substituted with names from [names].
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversed(names: Map<Variable, String> = emptyMap()): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> -LabeledPolynomial.monomialComparator.compare(o1.key, o2.key) }
//        .asSequence()
//        .map { (degs, t) ->
//            if (degs.isEmpty()) "$t"
//            else {
//                when {
//                    t.isOne() -> ""
//                    t.isMinusOne() -> "-"
//                    else -> "$t "
//                } +
//                        degs
//                            .toSortedMap()
//                            .filter { it.value > 0U }
//                            .map { (variable, deg) ->
//                                val variableName = names.getOrDefault(variable, variable.toString())
//                                when (deg) {
//                                    1U -> variableName
//                                    else -> "$variableName^$deg"
//                                }
//                            }
//                            .joinToString(separator = " ") { it }
//            }
//        }
//        .joinToString(separator = " + ") { it }
//        .ifEmpty { "0" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer].
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversed(namer: (Variable) -> String): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> -LabeledPolynomial.monomialComparator.compare(o1.key, o2.key) }
//        .asSequence()
//        .map { (degs, t) ->
//            if (degs.isEmpty()) "$t"
//            else {
//                when {
//                    t.isOne() -> ""
//                    t.isMinusOne() -> "-"
//                    else -> "$t "
//                } +
//                        degs
//                            .toSortedMap()
//                            .filter { it.value > 0U }
//                            .map { (variable, deg) ->
//                                when (deg) {
//                                    1U -> namer(variable)
//                                    else -> "${namer(variable)}^$deg"
//                                }
//                            }
//                            .joinToString(separator = " ") { it }
//            }
//        }
//        .joinToString(separator = " + ") { it }
//        .ifEmpty { "0" }
//
///**
// * Represents the polynomial as a [String] with names of variables substituted with names from [names] and with
// * brackets around the string if needed (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversedWithBrackets(names: Map<Variable, String> = emptyMap()): String =
//    with(representReversed(names)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer] and with brackets around the string if needed
// * (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversedWithBrackets(namer: (Variable) -> String): String =
//    with(representReversed(namer)) { if (coefficients.count() == 1) this else "($this)" }
//
//// endregion

// region Operator extensions

//// region Field case
//
//operator fun <T: Field<T>> Polynomial<T>.div(other: T): Polynomial<T> =
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    else
//        Polynomial(
//            coefficients
//                .mapValues { it.value / other },
//            toCheckInput = false
//        )
//
//// endregion

// endregion

//// region Polynomial substitution and functional representation
//
//public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Variable, C>): LabeledPolynomial<C> = ring {
//    if (coefficients.isEmpty()) return this@substitute
//    LabeledPolynomial<C>(
//        buildMap {
//            coefficients.forEach { (degs, c) ->
//                val newDegs = degs.filterKeys { it !in args }
//                val newC = degs.entries.asSequence().filter { it.key in args }.fold(c) { acc, (variable, deg) ->
//                    multiplyWithPower(acc, args[variable]!!, deg)
//                }
//                this[newDegs] = if (newDegs in this) this[newDegs]!! + newC else newC
//            }
//        }
//    )
//}
//
//// TODO: Replace with optimisation: the [result] may be unboxed, and all operations may be performed as soon as
////  possible on it
//@JvmName("substitutePolynomial")
//fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, arg: Map<Variable, LabeledPolynomial<C>>) : LabeledPolynomial<C> =
//    ring.labeledPolynomial {
//        if (coefficients.isEmpty()) return zero
//        coefficients
//            .asSequence()
//            .map { (degs, c) ->
//                degs.entries
//                    .asSequence()
//                    .filter { it.key in arg }
//                    .fold(LabeledPolynomial(mapOf(degs.filterKeys { it !in arg } to c))) { acc, (index, deg) ->
//                        multiplyWithPower(acc, arg[index]!!, deg)
//                    }
//            }
//            .reduce { acc, polynomial -> acc + polynomial } // TODO: Rewrite. Might be slow.
//    }
//
//// TODO: Substitute rational function
//
//fun <C, A : Ring<C>> LabeledPolynomial<C>.asFunctionOver(ring: A): (Map<Variable, C>) -> LabeledPolynomial<C> =
//    { substitute(ring, it) }
//
//fun <C, A : Ring<C>> LabeledPolynomial<C>.asPolynomialFunctionOver(ring: A): (Map<Variable, LabeledPolynomial<C>>) -> LabeledPolynomial<C> =
//    { substitute(ring, it) }
//
//// endregion

//// region Algebraic derivative and antiderivative
//// TODO
//// endregion