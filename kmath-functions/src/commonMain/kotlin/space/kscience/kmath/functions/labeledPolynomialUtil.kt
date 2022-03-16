/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// TODO: Docs

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

//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> power(arg: Symbol, pow: UInt): LabeledPolynomial<C> =
//    if (pow == 0U) one
//    else LabeledPolynomial<C>(mapOf(
//        mapOf(arg to pow) to constantOne
//    ))
//

//
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> number(value: Int): LabeledPolynomial<C> = ring { LabeledPolynomial<C>(mapOf(emptyMap<Symbol, UInt>() to number<C>(value))) }
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

/**
 * Creates a [LabeledPolynomialSpace] over a received ring.
 */
public fun <C, A : Ring<C>> A.labeledPolynomial(): LabeledPolynomialSpace<C, A> =
    LabeledPolynomialSpace(this)

/**
 * Creates a [LabeledPolynomialSpace]'s scope over a received ring.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <C, A : Ring<C>, R> A.labeledPolynomial(block: LabeledPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return LabeledPolynomialSpace(this).block()
}

///**
// * Represents the polynomial as a [String] with names of variables substituted with names from [names].
// * Consider that monomials are sorted in lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.represent(names: Map<Symbol, String> = emptyMap()): String =
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
//fun <C, A: Ring<C>> LabeledPolynomial<C>.represent(namer: (Symbol) -> String): String =
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
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representWithBrackets(names: Map<Symbol, String> = emptyMap()): String =
//    with(represent(names)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer] and with brackets around the string if needed
// * (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representWithBrackets(namer: (Symbol) -> String): String =
//    with(represent(namer)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] with names of variables substituted with names from [names].
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversed(names: Map<Symbol, String> = emptyMap()): String =
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
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversed(namer: (Symbol) -> String): String =
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
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversedWithBrackets(names: Map<Symbol, String> = emptyMap()): String =
//    with(representReversed(names)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer] and with brackets around the string if needed
// * (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(LabeledPolynomialSpace<C, A>)
//fun <C, A: Ring<C>> LabeledPolynomial<C>.representReversedWithBrackets(namer: (Symbol) -> String): String =
//    with(representReversed(namer)) { if (coefficients.count() == 1) this else "($this)" }

//operator fun <T: Field<T>> Polynomial<T>.div(other: T): Polynomial<T> =
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    else
//        Polynomial(
//            coefficients
//                .mapValues { it.value / other },
//            toCheckInput = false
//        )

//public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, C>): LabeledPolynomial<C> = ring {
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
//fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, arg: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> =
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
//fun <C, A : Ring<C>> LabeledPolynomial<C>.asFunctionOver(ring: A): (Map<Symbol, C>) -> LabeledPolynomial<C> =
//    { substitute(ring, it) }
//
//fun <C, A : Ring<C>> LabeledPolynomial<C>.asPolynomialFunctionOver(ring: A): (Map<Symbol, LabeledPolynomial<C>>) -> LabeledPolynomial<C> =
//    { substitute(ring, it) }

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.derivativeWithRespectTo(
    algebra: A,
    variable: Symbol,
): LabeledPolynomial<C> = algebra {
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (variable !in degs) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                when {
                                    vari != variable -> put(vari, deg)
                                    deg > 1u -> put(vari, deg - 1u)
                                }
                            }
                        },
                        multiplyBySquaring(c, degs[variable]!!)
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.derivativeWithRespectTo(
    algebra: A,
    variables: Collection<Symbol>,
): LabeledPolynomial<C> = algebra {
    val cleanedVariables = variables.toSet()
    if (cleanedVariables.isEmpty()) return this@derivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (!degs.keys.containsAll(cleanedVariables)) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                when {
                                    vari !in cleanedVariables -> put(vari, deg)
                                    deg > 1u -> put(vari, deg - 1u)
                                }
                            }
                        },
                        cleanedVariables.fold(c) { acc, variable -> multiplyBySquaring(acc, degs[variable]!!) }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: A,
    variable: Symbol,
    order: UInt
): LabeledPolynomial<C> = algebra {
    if (order == 0u) return this@nthDerivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.getOrElse(variable) { 0u } < order) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                when {
                                    vari != variable -> put(vari, deg)
                                    deg > order -> put(vari, deg - order)
                                }
                            }
                        },
                        degs[variable]!!.let { deg ->
                            (deg downTo deg - order + 1u)
                                .fold(c) { acc, ord -> multiplyBySquaring(acc, ord) }
                        }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: A,
    variablesAndOrders: Map<Symbol, UInt>,
): LabeledPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (filteredVariablesAndOrders.any { (variable, order) -> degs.getOrElse(variable) { 0u } < order }) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                if (vari !in filteredVariablesAndOrders) put(vari, deg)
                                else {
                                    val order = filteredVariablesAndOrders[vari]!!
                                    if (deg > order) put(vari, deg - order)
                                }
                            }
                        },
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            degs[index]!!.let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> multiplyBySquaring(acc2, ord) }
                            }
                        }
                    )
                }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> LabeledPolynomial<C>.antiderivativeWithRespectTo(
    algebra: A,
    variable: Symbol,
): LabeledPolynomial<C> = algebra {
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = buildMap<Symbol, UInt>(degs.size + 1) {
                        put(variable, 1u)
                        for ((vari, deg) in degs) put(vari, deg + getOrElse(vari) { 0u })
                    }
                    put(
                        newDegs,
                        c / multiplyBySquaring(one, newDegs[variable]!!)
                    )
                }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> LabeledPolynomial<C>.antiderivativeWithRespectTo(
    algebra: A,
    variables: Collection<Symbol>,
): LabeledPolynomial<C> = algebra {
    val cleanedVariables = variables.toSet()
    if (cleanedVariables.isEmpty()) return this@antiderivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = buildMap<Symbol, UInt>(degs.size + 1) {
                        for (variable in cleanedVariables) put(variable, 1u)
                        for ((vari, deg) in degs) put(vari, deg + getOrElse(vari) { 0u })
                    }
                    put(
                        newDegs,
                        cleanedVariables.fold(c) { acc, variable -> acc / multiplyBySquaring(one, newDegs[variable]!!) }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    algebra: A,
    variable: Symbol,
    order: UInt
): LabeledPolynomial<C> = algebra {
    if (order == 0u) return this@nthAntiderivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = buildMap<Symbol, UInt>(degs.size + 1) {
                        put(variable, order)
                        for ((vari, deg) in degs) put(vari, deg + getOrElse(vari) { 0u })
                    }
                    put(
                        newDegs,
                        newDegs[variable]!!.let { deg ->
                            (deg downTo  deg - order + 1u)
                                .fold(c) { acc, ord -> acc / multiplyBySquaring(one, ord) }
                        }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    algebra: A,
    variablesAndOrders: Map<Symbol, UInt>,
): LabeledPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = buildMap<Symbol, UInt>(degs.size + 1) {
                        for ((variable, order) in filteredVariablesAndOrders) put(variable, order)
                        for ((vari, deg) in degs) put(vari, deg + getOrElse(vari) { 0u })
                    }
                    put(
                        newDegs,
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            newDegs[index]!!.let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> acc2 / multiplyBySquaring(one, ord) }
                            }
                        }
                    )
                }
        }
    )
}