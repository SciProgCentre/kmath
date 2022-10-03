/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName


/**
 * Creates a [LabeledPolynomialSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.labeledPolynomialSpace: LabeledPolynomialSpace<C, A>
    get() = LabeledPolynomialSpace(this)

/**
 * Creates a [LabeledPolynomialSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.labeledPolynomialSpace(block: LabeledPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return LabeledPolynomialSpace(this).block()
}
/**
 * Creates a [LabeledRationalFunctionSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.labeledRationalFunctionSpace: LabeledRationalFunctionSpace<C, A>
    get() = LabeledRationalFunctionSpace(this)

/**
 * Creates a [LabeledRationalFunctionSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.labeledRationalFunctionSpace(block: LabeledRationalFunctionSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return LabeledRationalFunctionSpace(this).block()
}

/**
 * Substitutes provided Double arguments [args] into [this] Double polynomial.
 */
public fun LabeledPolynomial<Double>.substitute(args: Map<Symbol, Double>): LabeledPolynomial<Double> = Double.algebra {
    if (coefficients.isEmpty()) return this@substitute
    LabeledPolynomial<Double>(
        buildMap {
            coefficients.forEach { (degs, c) ->
                val newDegs = degs.filterKeys { it !in args }
                val newC = args.entries.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrElse(variable) { 0u }
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                putOrChange(newDegs, newC, ::add)
            }
        }
    )
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */
public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, C>): LabeledPolynomial<C> = ring {
    if (coefficients.isEmpty()) return this@substitute
    LabeledPolynomial<C>(
        buildMap {
            coefficients.forEach { (degs, c) ->
                val newDegs = degs.filterKeys { it !in args }
                val newC = args.entries.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrElse(variable) { 0u }
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                putOrChange(newDegs, newC, ::add)
            }
        }
    )
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */ // TODO: To optimize boxing
@JvmName("substitutePolynomial")
public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> =
    ring.labeledPolynomialSpace {
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val newDegs = degs.filterKeys { it !in args }
            acc + args.entries.fold(LabeledPolynomial<C>(mapOf(newDegs to c))) { product, (variable, substitution) ->
                val deg = degs.getOrElse(variable) { 0u }
                if (deg == 0u) product else product * power(substitution, deg)
            }
        }
    }

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */ // TODO: To optimize boxing
@JvmName("substituteRationalFunction")
public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
    ring.labeledRationalFunctionSpace {
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val newDegs = degs.filterKeys { it !in args }
            acc + args.entries.fold(LabeledRationalFunction(LabeledPolynomial<C>(mapOf(newDegs to c)))) { product, (variable, substitution) ->
                val deg = degs.getOrElse(variable) { 0u }
                if (deg == 0u) product else product * power(substitution, deg)
            }
        }
    }

/**
 * Substitutes provided Double arguments [args] into [this] Double rational function.
 */
public fun LabeledRationalFunction<Double>.substitute(args: Map<Symbol, Double>): LabeledRationalFunction<Double> =
    LabeledRationalFunction(numerator.substitute(args), denominator.substitute(args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */
public fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Symbol, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */ // TODO: To optimize calculation
@JvmName("substitutePolynomial")
public fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledPolynomial<C>>) : LabeledRationalFunction<C> =
    LabeledRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */ // TODO: To optimize calculation
@JvmName("substituteRationalFunction")
public fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
    ring.labeledRationalFunctionSpace {
        numerator.substitute(ring, args) / denominator.substitute(ring, args)
    }

/**
 * Returns algebraic derivative of received polynomial with respect to provided variable.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.derivativeWithRespectTo(
    algebra: A,
    variable: Symbol,
): LabeledPolynomial<C> = algebra {
    LabeledPolynomial<C>(
        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= 1u }) {
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
                        multiplyByDoubling(c, degs[variable]!!)
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial with respect to provided variable of specified order.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: A,
    variable: Symbol,
    order: UInt
): LabeledPolynomial<C> = algebra {
    if (order == 0u) return this@nthDerivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= order }) {
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
                                .fold(c) { acc, ord -> multiplyByDoubling(acc, ord) }
                        }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial with respect to provided variables of specified orders.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: A,
    variablesAndOrders: Map<Symbol, UInt>,
): LabeledPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(
            coefficients.count {
                variablesAndOrders.all { (variable, order) ->
                    it.key.getOrElse(variable) { 0u } >= order
                }
            }
        ) {
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
                                    .fold(acc1) { acc2, ord -> multiplyByDoubling(acc2, ord) }
                            }
                        }
                    )
                }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial with respect to provided variable.
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
                    val newDegs = degs.withPutOrChanged(variable, 1u) { it -> it + 1u }
                    put(
                        newDegs,
                        c / multiplyByDoubling(one, newDegs[variable]!!)
                    )
                }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial with respect to provided variable of specified order.
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
                    val newDegs = degs.withPutOrChanged(variable, order) { it -> it + order }
                    put(
                        newDegs,
                        newDegs[variable]!!.let { deg ->
                            (deg downTo  deg - order + 1u)
                                .fold(c) { acc, ord -> acc / multiplyByDoubling(one, ord) }
                        }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial with respect to provided variables of specified orders.
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
                    val newDegs = mergeBy(degs, filteredVariablesAndOrders) { deg, order -> deg + order }
                    put(
                        newDegs,
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            newDegs[index]!!.let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> acc2 / multiplyByDoubling(one, ord) }
                            }
                        }
                    )
                }
        }
    )
}