package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.*
import kotlin.jvm.JvmName
import kotlin.math.max


// TODO: Docs

// region Sort of legacy

//// region Constants
//
//// TODO: Reuse underlying ring extensions
//
//context(NumberedPolynomialSpace<C, A>)
//@Suppress("NOTHING_TO_INLINE")
//public fun <C, A: Ring<C>> numberConstant(value: Int): C = ring { number<C>(value) }
//
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> multiplyWithPower(base: C, arg: C, pow: UInt): C = ring { multiplyWithPower<C>(base, arg, pow) }
//
//// endregion

//// region Polynomials
//
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> number(value: Int): NumberedPolynomial<C> = ring { NumberedPolynomial<C>(mapOf(emptyList<UInt>() to number<C>(value))) }
//
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> multiplyWithPower(base: NumberedPolynomial<C>, arg: NumberedPolynomial<C>, pow: UInt): NumberedPolynomial<C> =
//    when {
//        arg.isZero() && pow > 0U -> base
//        arg.isOne() -> base
//        arg.isMinusOne() -> if (pow % 2U == 0U) base else -base
//        else -> multiplyWithPowerInternalLogic(base, arg, pow)
//    }
//
//// Trivial but very slow
//context(NumberedPolynomialSpace<C, A>)
//internal tailrec fun <C, A: Ring<C>> multiplyWithPowerInternalLogic(base: NumberedPolynomial<C>, arg: NumberedPolynomial<C>, exponent: UInt): NumberedPolynomial<C> =
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
 * Crates a [NumberedPolynomialSpace] over received ring.
 */
public fun <C, A : Ring<C>> A.numberedPolynomial(): NumberedPolynomialSpace<C, A> =
    NumberedPolynomialSpace(this)

/**
 * Crates a [NumberedPolynomialSpace]'s scope over received ring.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <C, A : Ring<C>, R> A.numberedPolynomial(block: NumberedPolynomialSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return NumberedPolynomialSpace(this).block()
}

// endregion

//// region String representations
//
///**
// * Represents the polynomial as a [String] where name of variable with index `i` is [withVariableName] + `"_${i+1}"`.
// * Consider that monomials are sorted in lexicographic order.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.represent(withVariableName: String = NumberedPolynomial.defaultVariableName): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> NumberedPolynomial.monomialComparator.compare(o1.key, o2.key) }
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
//                            .mapIndexed { index, deg ->
//                                when (deg) {
//                                    0U -> ""
//                                    1U -> "${withVariableName}_${index+1}"
//                                    else -> "${withVariableName}_${index+1}^$deg"
//                                }
//                            }
//                            .filter { it.isNotEmpty() }
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
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.represent(namer: (Int) -> String): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> NumberedPolynomial.monomialComparator.compare(o1.key, o2.key) }
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
//                            .mapIndexed { index, deg ->
//                                when (deg) {
//                                    0U -> ""
//                                    1U -> namer(index)
//                                    else -> "${namer(index)}^$deg"
//                                }
//                            }
//                            .filter { it.isNotEmpty() }
//                            .joinToString(separator = " ") { it }
//            }
//        }
//        .joinToString(separator = " + ") { it }
//        .ifEmpty { "0" }
//
///**
// * Represents the polynomial as a [String] where name of variable with index `i` is [withVariableName] + `"_${i+1}"`
// * and with brackets around the string if needed (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in lexicographic order.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.representWithBrackets(withVariableName: String = NumberedPolynomial.defaultVariableName): String =
//    with(represent(withVariableName)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer] and with brackets around the string if needed
// * (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in lexicographic order.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.representWithBrackets(namer: (Int) -> String): String =
//    with(represent(namer)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] where name of variable with index `i` is [withVariableName] + `"_${i+1}"`.
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.representReversed(withVariableName: String = NumberedPolynomial.defaultVariableName): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> -NumberedPolynomial.monomialComparator.compare(o1.key, o2.key) }
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
//                            .mapIndexed { index, deg ->
//                                when (deg) {
//                                    0U -> ""
//                                    1U -> "${withVariableName}_${index+1}"
//                                    else -> "${withVariableName}_${index+1}^$deg"
//                                }
//                            }
//                            .filter { it.isNotEmpty() }
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
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.representReversed(namer: (Int) -> String): String =
//    coefficients.entries
//        .sortedWith { o1, o2 -> -NumberedPolynomial.monomialComparator.compare(o1.key, o2.key) }
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
//                            .mapIndexed { index, deg ->
//                                when (deg) {
//                                    0U -> ""
//                                    1U -> namer(index)
//                                    else -> "${namer(index)}^$deg"
//                                }
//                            }
//                            .filter { it.isNotEmpty() }
//                            .joinToString(separator = " ") { it }
//            }
//        }
//        .joinToString(separator = " + ") { it }
//        .ifEmpty { "0" }
//
///**
// * Represents the polynomial as a [String] where name of variable with index `i` is [withVariableName] + `"_${i+1}"`
// * and with brackets around the string if needed (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.representReversedWithBrackets(withVariableName: String = NumberedPolynomial.defaultVariableName): String =
//    with(representReversed(withVariableName)) { if (coefficients.count() == 1) this else "($this)" }
//
///**
// * Represents the polynomial as a [String] naming variables by [namer] and with brackets around the string if needed
// * (i.e. when there are at least two addends in the representation).
// * Consider that monomials are sorted in **reversed** lexicographic order.
// */
//context(NumberedPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> NumberedPolynomial<C>.representReversedWithBrackets(namer: (Int) -> String): String =
//    with(representReversed(namer)) { if (coefficients.count() == 1) this else "($this)" }
//
//// endregion

//// region Polynomial substitution and functional representation
//
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, C>): NumberedPolynomial<C> = ring {
//    if (coefficients.isEmpty()) return this@substitute
//    NumberedPolynomial<C>(
//        buildMap {
//            coefficients.forEach { (degs, c) ->
//                val newDegs = degs.mapIndexed { index, deg -> if (index in args) 0U else deg }.cleanUp()
//                val newC = degs.foldIndexed(c) { index, acc, deg ->
//                    if (index in args) multiplyWithPower(acc, args[index]!!, deg)
//                    else acc
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
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, arg: Map<Int, NumberedPolynomial<C>>) : NumberedPolynomial<C> =
//    ring.numberedPolynomialSpace {
//        if (coefficients.isEmpty()) return zero
//        coefficients
//            .asSequence()
//            .map { (degs, c) ->
//                degs.foldIndexed(
//                    NumberedPolynomial(
//                        degs.mapIndexed { index, deg -> if (index in arg) 0U else deg } to c
//                    )
//                ) { index, acc, deg -> if (index in arg) multiplyWithPower(acc, arg[index]!!, deg) else acc }
//            }
//            .reduce { acc, polynomial -> acc + polynomial } // TODO: Rewrite. Might be slow.
//    }
//
//// TODO: Substitute rational function
//
//public fun <C, A : Ring<C>> NumberedPolynomial<C>.asFunctionOver(ring: A): (Map<Int, C>) -> NumberedPolynomial<C> =
//    { substitute(ring, it) }
//
//public fun <C, A : Ring<C>> NumberedPolynomial<C>.asPolynomialFunctionOver(ring: A): (Map<Int, NumberedPolynomial<C>>) -> NumberedPolynomial<C> =
//    { substitute(ring, it) }
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

// region Polynomial substitution and functional representation

// TODO: May be apply Horner's method too?
/**
 * Evaluates the value of the given double polynomial for given double argument.
 */
public fun NumberedPolynomial<Double>.substitute(args: Map<Int, Double>): NumberedPolynomial<Double> = Double.algebra {
    val acc = LinkedHashMap<List<UInt>, Double>(coefficients.size)
    for ((degs, c) in coefficients) {
        val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
        val newC = args.entries.fold(c) { product, (variable, substitutor) ->
            val deg = degs.getOrElse(variable) { 0u }
            if (deg == 0u) product else product * substitutor.pow(deg.toInt())
        }
        if (newDegs !in acc) acc[newDegs] = newC
        else acc[newDegs] = acc[newDegs]!! + newC
    }
    return NumberedPolynomial<Double>(acc)
}

/**
 * Evaluates the value of the given polynomial for given argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, C>): NumberedPolynomial<C> = ring {
    val acc = LinkedHashMap<List<UInt>, C>(coefficients.size)
    for ((degs, c) in coefficients) {
        val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
        val newC = args.entries.fold(c) { product, (variable, substitutor) ->
            val deg = degs.getOrElse(variable) { 0u }
            if (deg == 0u) product else product * power(substitutor, deg)
        }
        if (newDegs !in acc) acc[newDegs] = newC
        else acc[newDegs] = acc[newDegs]!! + newC
    }
    return NumberedPolynomial<C>(acc)
}

// TODO: (Waiting for hero) Replace with optimisation: the [result] may be unboxed, and all operations may be performed
//  as soon as possible on it
@JvmName("substitutePolynomial")
public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedPolynomial<C>>) : NumberedPolynomial<C> = TODO() /*ring.numberedPolynomial {
    val acc = LinkedHashMap<List<UInt>, NumberedPolynomial<C>>(coefficients.size)
    for ((degs, c) in coefficients) {
        val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
        val newC = args.entries.fold(c.asNumberedPolynomial()) { product, (variable, substitutor) ->
            val deg = degs.getOrElse(variable) { 0u }
            if (deg == 0u) product else product * power(substitutor, deg)
        }
        if (newDegs !in acc) acc[newDegs] = c.asNumberedPolynomial()
        else acc[newDegs] = acc[newDegs]!! + c
    }
}*/

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedPolynomial<C>.asFunction(ring: A): (Map<Int, C>) -> NumberedPolynomial<C> = { substitute(ring, it) }

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <C, A : Ring<C>> NumberedPolynomial<C>.asPolynomialFunctionOver(ring: A): (Map<Int, NumberedPolynomial<C>>) -> NumberedPolynomial<C> = { substitute(ring, it) }

// endregion

// region Algebraic derivative and antiderivative

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> NumberedPolynomial<C>.derivativeWithRespectTo(
    algebra: A,
    variable: Int,
): NumberedPolynomial<C> = algebra {
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.size > variable) return@forEach
                    put(
                        degs.mapIndexed { index, deg ->
                            when {
                                index != variable -> deg
                                deg > 0u -> deg - 1u
                                else -> return@forEach
                            }
                        }.cleanUp(),
                        optimizedMultiply(c, degs[variable])
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> NumberedPolynomial<C>.derivativeWithRespectTo(
    algebra: A,
    variables: Collection<Int>,
): NumberedPolynomial<C> = algebra {
    val cleanedVariables = variables.toSet()
    if (cleanedVariables.isEmpty()) return this@derivativeWithRespectTo
    val maxRespectedVariable = cleanedVariables.maxOrNull()!!
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.size > maxRespectedVariable) return@forEach
                    put(
                        degs.mapIndexed { index, deg ->
                            when {
                                index !in cleanedVariables -> deg
                                deg > 0u -> deg - 1u
                                else -> return@forEach
                            }
                        }.cleanUp(),
                        cleanedVariables.fold(c) { acc, variable -> optimizedMultiply(acc, degs[variable]) }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Ring<C>> NumberedPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: A,
    variable: Int,
    order: UInt
): NumberedPolynomial<C> = algebra {
    if (order == 0u) return this@nthDerivativeWithRespectTo
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.size > variable) return@forEach
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
                                .fold(c) { acc, ord -> optimizedMultiply(acc, ord) }
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
public fun <C, A : Ring<C>> NumberedPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: A,
    variablesAndOrders: Map<Int, UInt>,
): NumberedPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    val maxRespectedVariable = filteredVariablesAndOrders.keys.maxOrNull()!!
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.size > maxRespectedVariable) return@forEach
                    put(
                        degs.mapIndexed { index, deg ->
                            if (index !in filteredVariablesAndOrders) return@mapIndexed deg
                            val order = filteredVariablesAndOrders[index]!!
                            if (deg >= order) deg - order else return@forEach
                        }.cleanUp(),
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            degs[index].let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> optimizedMultiply(acc2, ord) }
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
public fun <C, A : Field<C>> NumberedPolynomial<C>.antiderivativeWithRespectTo(
    algebra: A,
    variable: Int,
): NumberedPolynomial<C> = algebra {
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(variable + 1, degs.size)) { if (it != variable) degs[it] else degs[it] + 1u },
                        c / optimizedMultiply(one, degs[variable])
                    )
                }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> NumberedPolynomial<C>.antiderivativeWithRespectTo(
    algebra: A,
    variables: Collection<Int>,
): NumberedPolynomial<C> = algebra {
    val cleanedVariables = variables.toSet()
    if (cleanedVariables.isEmpty()) return this@antiderivativeWithRespectTo
    val maxRespectedVariable = cleanedVariables.maxOrNull()!!
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(maxRespectedVariable + 1, degs.size)) { if (it !in variables) degs[it] else degs[it] + 1u },
                        cleanedVariables.fold(c) { acc, variable -> acc / optimizedMultiply(one, degs[variable]) }
                    )
                }
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A : Field<C>> NumberedPolynomial<C>.nthAntiderivativeWithRespectTo(
    algebra: A,
    variable: Int,
    order: UInt
): NumberedPolynomial<C> = algebra {
    if (order == 0u) return this@nthAntiderivativeWithRespectTo
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(variable + 1, degs.size)) { if (it != variable) degs[it] else degs[it] + order },
                        degs[variable].let { deg ->
                            (deg downTo deg - order + 1u)
                                .fold(c) { acc, ord -> acc / optimizedMultiply(one, ord) }
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
public fun <C, A : Field<C>> NumberedPolynomial<C>.nthAntiderivativeWithRespectTo(
    algebra: A,
    variablesAndOrders: Map<Int, UInt>,
): NumberedPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
    val maxRespectedVariable = filteredVariablesAndOrders.keys.maxOrNull()!!
    NumberedPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    put(
                        List(max(maxRespectedVariable + 1, degs.size)) { degs[it] + filteredVariablesAndOrders.getOrElse(it) { 0u } },
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            degs[index].let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> acc2 / optimizedMultiply(one, ord) }
                            }
                        }
                    )
                }
        }
    )
}

// endregion