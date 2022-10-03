/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min


/**
 * Creates a [NumberedPolynomialSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.numberedPolynomialSpace: NumberedPolynomialSpace<C, A>
    get() = NumberedPolynomialSpace(this)

/**
 * Creates a [NumberedPolynomialSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.numberedPolynomialSpace(block: NumberedPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return NumberedPolynomialSpace(this).block()
}

/**
 * Creates a [NumberedRationalFunctionSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.numberedRationalFunctionSpace: NumberedRationalFunctionSpace<C, A>
    get() = NumberedRationalFunctionSpace(this)

/**
 * Creates a [NumberedRationalFunctionSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.numberedRationalFunctionSpace(block: NumberedRationalFunctionSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return NumberedRationalFunctionSpace(this).block()
}

/**
 * Substitutes provided Double arguments [args] into [this] Double polynomial.
 */
public fun NumberedPolynomial<Double>.substitute(args: Map<Int, Double>): NumberedPolynomial<Double> = Double.algebra {
    NumberedPolynomial<Double>(
        buildMap(coefficients.size) {
            for ((degs, c) in coefficients) {
                val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
                val newC = args.entries.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrElse(variable) { 0u }
                    if (deg == 0u) product else product * substitution.pow(deg.toInt())
                }
                putOrChange(newDegs, newC) { it -> it + newC }
            }
        }
    )
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, C>): NumberedPolynomial<C> = ring {
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            for ((degs, c) in coefficients) {
                val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
                val newC = args.entries.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrElse(variable) { 0u }
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                putOrChange(newDegs, newC) { it -> it + newC }
            }
        }
    )
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */ // TODO: To optimize boxing
@JvmName("substitutePolynomial")
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedPolynomial<C>>) : NumberedPolynomial<C> =
    ring.numberedPolynomialSpace {
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
            acc + args.entries.fold(NumberedPolynomial<C>(mapOf(newDegs to c))) { product, (variable, substitution) ->
                val deg = degs.getOrElse(variable) { 0u }
                if (deg == 0u) product else product * power(substitution, deg)
            }
        }
    }

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */ // TODO: To optimize boxing
@JvmName("substituteRationalFunction")
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
    ring.numberedRationalFunctionSpace {
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
            acc + args.entries.fold(NumberedRationalFunction(NumberedPolynomial<C>(mapOf(newDegs to c)))) { product, (variable, substitution) ->
                val deg = degs.getOrElse(variable) { 0u }
                if (deg == 0u) product else product * power(substitution, deg)
            }
        }
    }

/**
 * Substitutes provided Double arguments [args] into [this] Double rational function.
 */
public fun NumberedRationalFunction<Double>.substitute(args: Map<Int, Double>): NumberedRationalFunction<Double> =
    NumberedRationalFunction(numerator.substitute(args), denominator.substitute(args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */
public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Int, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */ // TODO: To optimize calculation
@JvmName("substitutePolynomial")
public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedPolynomial<C>>) : NumberedRationalFunction<C> =
    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */ // TODO: To optimize calculation
@JvmName("substituteRationalFunction")
public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
    ring.numberedRationalFunctionSpace {
        numerator.substitute(ring, args) / denominator.substitute(ring, args)
    }

/**
 * Substitutes provided Double arguments [args] into [this] Double polynomial.
 */
public fun NumberedPolynomial<Double>.substitute(args: Buffer<Double>): NumberedPolynomial<Double> = Double.algebra {
    val lastSubstitutionVariable = args.size - 1
    NumberedPolynomial<Double>(
        buildMap(coefficients.size) {
            for ((degs, c) in coefficients) {
                val lastDegsIndex = degs.lastIndex
                val newDegs =
                    if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
                    else degs.toMutableList().apply {
                        for (i in 0..lastSubstitutionVariable) this[i] = 0u
                    }
                val newC = (0..min(lastDegsIndex, lastSubstitutionVariable)).fold(c) { product, variable ->
                    val deg = degs[variable]
                    if (deg == 0u) product else product * args[variable].pow(deg.toInt())
                }
                putOrChange(newDegs, newC) { it -> it + newC }
            }
        }
    )
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Buffer<C>): NumberedPolynomial<C> = ring {
    val lastSubstitutionVariable = args.size - 1
    NumberedPolynomial<C>(
        buildMap<List<UInt>, C>(coefficients.size) {
            for ((degs, c) in coefficients) {
                val lastDegsIndex = degs.lastIndex
                val newDegs =
                    if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
                    else degs.toMutableList().apply {
                        for (i in 0..lastSubstitutionVariable) this[i] = 0u
                    }
                val newC = (0..min(lastDegsIndex, lastSubstitutionVariable)).fold(c) { product, variable ->
                    val deg = degs[variable]
                    if (deg == 0u) product else product * power(args[variable], deg)
                }
                putOrChange(newDegs, newC) { it -> it + newC }
            }
        }
    )
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */ // TODO: To optimize boxing
@JvmName("substitutePolynomial")
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Buffer<NumberedPolynomial<C>>) : NumberedPolynomial<C> =
    ring.numberedPolynomialSpace {
        val lastSubstitutionVariable = args.size - 1
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val lastDegsIndex = degs.lastIndex
            val newDegs =
                if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
                else degs.toMutableList().apply {
                    for (i in 0..lastSubstitutionVariable) this[i] = 0u
                }
            acc + (0..min(lastDegsIndex, lastSubstitutionVariable))
                .fold(NumberedPolynomial<C>(mapOf(newDegs to c))) { product, variable ->
                    val deg = degs[variable]
                    if (deg == 0u) product else product * power(args[variable], deg)
                }
        }
    }

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */ // TODO: To optimize boxing
@JvmName("substituteRationalFunction")
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Buffer<NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
    ring.numberedRationalFunctionSpace {
        val lastSubstitutionVariable = args.size - 1
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val lastDegsIndex = degs.lastIndex
            val newDegs =
                if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
                else degs.toMutableList().apply {
                    for (i in 0..lastSubstitutionVariable) this[i] = 0u
                }
            acc + (0..min(lastDegsIndex, lastSubstitutionVariable))
                .fold(NumberedRationalFunction(NumberedPolynomial<C>(mapOf(newDegs to c)))) { product, variable ->
                    val deg = degs[variable]
                    if (deg == 0u) product else product * power(args[variable], deg)
                }
        }
    }

/**
 * Substitutes provided Double arguments [args] into [this] Double rational function.
 */
public fun NumberedRationalFunction<Double>.substitute(args: Buffer<Double>): NumberedRationalFunction<Double> =
    NumberedRationalFunction(numerator.substitute(args), denominator.substitute(args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */
public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Buffer<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */ // TODO: To optimize calculation
@JvmName("substitutePolynomial")
public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Buffer<NumberedPolynomial<C>>) : NumberedRationalFunction<C> =
    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */ // TODO: To optimize calculation
@JvmName("substituteRationalFunction")
public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Buffer<NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
    ring.numberedRationalFunctionSpace {
        numerator.substitute(ring, args) / denominator.substitute(ring, args)
    }

internal const val fullSubstitutionExceptionMessage: String = "Fully substituting buffer should cover all variables of the polynomial."

/**
 * Substitutes provided Double arguments [args] into [this] Double polynomial.
 */
public fun NumberedPolynomial<Double>.substituteFully(args: Buffer<Double>): Double = Double.algebra {
    val lastSubstitutionVariable = args.size - 1
    require(coefficients.keys.all { it.lastIndex <= lastSubstitutionVariable }) { fullSubstitutionExceptionMessage }
    coefficients.entries.fold(.0) { acc, (degs, c) ->
        acc + degs.foldIndexed(c) { variable, product, deg ->
            if (deg == 0u) product else product * args[variable].pow(deg.toInt())
        }
    }
}

/**
 * Substitutes provided arguments [args] into [this] polynomial.
 */
public fun <C> NumberedPolynomial<C>.substituteFully(ring: Ring<C>, args: Buffer<C>): C = ring {
    val lastSubstitutionVariable = args.size - 1
    require(coefficients.keys.all { it.lastIndex <= lastSubstitutionVariable }) { fullSubstitutionExceptionMessage }
    coefficients.entries.fold(zero) { acc, (degs, c) ->
        acc + degs.foldIndexed(c) { variable, product, deg ->
            if (deg == 0u) product else product * power(args[variable], deg)
        }
    }
}

/**
 * Substitutes provided Double arguments [args] into [this] Double rational function.
 */
public fun NumberedRationalFunction<Double>.substituteFully(args: Buffer<Double>): Double =
    numerator.substituteFully(args) / denominator.substituteFully(args)

/**
 * Substitutes provided arguments [args] into [this] rational function.
 */
public fun <C> NumberedRationalFunction<C>.substituteFully(ring: Field<C>, args: Buffer<C>): C = ring {
    numerator.substituteFully(ring, args) / denominator.substituteFully(ring, args)
}

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedPolynomial<C>.asFunctionOver(ring: A): (Buffer<C>) -> C = { substituteFully(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedPolynomial<C>.asFunctionOfConstantOver(ring: A): (Buffer<C>) -> C = { substituteFully(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedPolynomial<C>.asFunctionOfPolynomialOver(ring: A): (Buffer<NumberedPolynomial<C>>) -> NumberedPolynomial<C> = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedPolynomial<C>.asFunctionOfRationalFunctionOver(ring: A): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Field<C>> NumberedRationalFunction<C>.asFunctionOver(ring: A): (Buffer<C>) -> C = { substituteFully(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Field<C>> NumberedRationalFunction<C>.asFunctionOfConstantOver(ring: A): (Buffer<C>) -> C = { substituteFully(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedRationalFunction<C>.asFunctionOfPolynomialOver(ring: A): (Buffer<NumberedPolynomial<C>>) -> NumberedRationalFunction<C> = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedRationalFunction<C>.asFunctionOfRationalFunctionOver(ring: A): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = { substitute(ring, it) }

/**
 * Returns algebraic derivative of received polynomial with respect to provided variable.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> NumberedPolynomial<C>.derivativeWithRespectTo(
    ring: A,
    variable: Int,
): NumberedPolynomial<C> = ring {
    NumberedPolynomial<C>(
        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= 1u }) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.lastIndex < variable) return@forEach
                    put(
                        degs.mapIndexed { index, deg ->
                            when {
                                index != variable -> deg
                                deg > 0u -> deg - 1u
                                else -> return@forEach
                            }
                        }.cleanUp(),
                        multiplyByDoubling(c, degs[variable])
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial with respect to provided variable of specified order.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> NumberedPolynomial<C>.nthDerivativeWithRespectTo(
    ring: A,
    variable: Int,
    order: UInt
): NumberedPolynomial<C> = ring {
    if (order == 0u) return this@nthDerivativeWithRespectTo
    NumberedPolynomial<C>(
        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= order }) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.lastIndex < variable) return@forEach
                    put(
                        degs.mapIndexed { index, deg ->
                            when {
                                index != variable -> deg
                                deg >= order -> deg - order
                                else -> return@forEach
                            }
                        }.cleanUp(),
                        degs[variable].let { deg ->
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
public fun <C, A : Ring<C>> NumberedPolynomial<C>.nthDerivativeWithRespectTo(
    ring: A,
    variablesAndOrders: Map<Int, UInt>,
): NumberedPolynomial<C> = ring {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    val maxRespectedVariable = filteredVariablesAndOrders.keys.maxOrNull()!!
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.lastIndex < maxRespectedVariable) return@forEach
                    put(
                        degs.mapIndexed { index, deg ->
                            if (index !in filteredVariablesAndOrders) return@mapIndexed deg
                            val order = filteredVariablesAndOrders[index]!!
                            if (deg >= order) deg - order else return@forEach
                        }.cleanUp(),
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            degs[index].let { deg ->
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
public fun <C, A : Field<C>> NumberedPolynomial<C>.antiderivativeWithRespectTo(
    ring: A,
    variable: Int,
): NumberedPolynomial<C> = ring {
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(variable + 1, degs.size)) { degs.getOrElse(it) { 0u } + if (it != variable) 0u else 1u },
                        c / multiplyByDoubling(one, degs.getOrElse(variable) { 0u } + 1u)
                    )
                }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial with respect to provided variable of specified order.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> NumberedPolynomial<C>.nthAntiderivativeWithRespectTo(
    ring: A,
    variable: Int,
    order: UInt
): NumberedPolynomial<C> = ring {
    if (order == 0u) return this@nthAntiderivativeWithRespectTo
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(variable + 1, degs.size)) { degs.getOrElse(it) { 0u } + if (it != variable) 0u else order },
                        degs.getOrElse(variable) { 0u }.let { deg ->
                            (deg + 1u .. deg + order)
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
public fun <C, A : Field<C>> NumberedPolynomial<C>.nthAntiderivativeWithRespectTo(
    ring: A,
    variablesAndOrders: Map<Int, UInt>,
): NumberedPolynomial<C> = ring {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
    val maxRespectedVariable = filteredVariablesAndOrders.keys.maxOrNull()!!
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(maxRespectedVariable + 1, degs.size)) { degs.getOrElse(it) { 0u } + filteredVariablesAndOrders.getOrElse(it) { 0u } },
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (variable, order) ->
                            degs.getOrElse(variable) { 0u }.let { deg ->
                                (deg + 1u .. deg + order)
                                    .fold(acc1) { acc, ord -> acc / multiplyByDoubling(one, ord) }
                            }
                        }
                    )
                }
        }
    )
}