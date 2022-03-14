package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.contracts.*
import kotlin.jvm.JvmName



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
//// Trivial but slow as duck
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

// TODO: Docs
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
//operator fun <T: Field<T>> Polynomial<T>.div(other: Polynomial<T>): Polynomial<T> {
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    if (isZero()) return this
//
//    fun Map<List<Int>, T>.leadingTerm() =
//        this
//            .asSequence()
//            .map { Pair(it.key, it.value) }
//            .reduce { (accDegs, accC), (listDegs, listC) ->
//                for (i in 0..accDegs.lastIndex) {
//                    if (accDegs[i] > listDegs.getOrElse(i) { 0 }) return@reduce accDegs to accC
//                    if (accDegs[i] < listDegs.getOrElse(i) { 0 }) return@reduce listDegs to listC
//                }
//                if (accDegs.size < listDegs.size) listDegs to listC else accDegs to accC
//            }
//
//    var thisCoefficients = coefficients.toMutableMap()
//    val otherCoefficients = other.coefficients
//    val quotientCoefficients = HashMap<List<Int>, T>()
//
//    var (thisLeadingTermDegs, thisLeadingTermC) = thisCoefficients.leadingTerm()
//    val (otherLeadingTermDegs, otherLeadingTermC) = otherCoefficients.leadingTerm()
//
//    while (
//        thisLeadingTermDegs.size >= otherLeadingTermDegs.size &&
//        (0..otherLeadingTermDegs.lastIndex).all { thisLeadingTermDegs[it] >= otherLeadingTermDegs[it] }
//    ) {
//        val multiplierDegs =
//            thisLeadingTermDegs
//                .mapIndexed { index, deg -> deg - otherLeadingTermDegs.getOrElse(index) { 0 } }
//                .cleanUp()
//        val multiplierC = thisLeadingTermC / otherLeadingTermC
//
//        quotientCoefficients[multiplierDegs] = multiplierC
//
//        for ((degs, t) in otherCoefficients) {
//            val productDegs =
//                (0..max(degs.lastIndex, multiplierDegs.lastIndex))
//                    .map { degs.getOrElse(it) { 0 } + multiplierDegs.getOrElse(it) { 0 } }
//                    .cleanUp()
//            val productC = t * multiplierC
//            thisCoefficients[productDegs] =
//                if (productDegs in thisCoefficients) thisCoefficients[productDegs]!! - productC else -productC
//        }
//
//        thisCoefficients = thisCoefficients.filterValues { it.isNotZero() }.toMutableMap()
//
//        if (thisCoefficients.isEmpty())
//            return Polynomial(quotientCoefficients, toCheckInput = false)
//
//        val t = thisCoefficients.leadingTerm()
//        thisLeadingTermDegs = t.first
//        thisLeadingTermC = t.second
//    }
//
//    return Polynomial(quotientCoefficients, toCheckInput = false)
//}
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
//operator fun <T: Field<T>> Polynomial<T>.rem(other: Polynomial<T>): Polynomial<T> {
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    if (isZero()) return this
//
//    fun Map<List<Int>, T>.leadingTerm() =
//        this
//            .asSequence()
//            .map { Pair(it.key, it.value) }
//            .reduce { (accDegs, accC), (listDegs, listC) ->
//                for (i in 0..accDegs.lastIndex) {
//                    if (accDegs[i] > listDegs.getOrElse(i) { 0 }) return@reduce accDegs to accC
//                    if (accDegs[i] < listDegs.getOrElse(i) { 0 }) return@reduce listDegs to listC
//                }
//                if (accDegs.size < listDegs.size) listDegs to listC else accDegs to accC
//            }
//
//    var thisCoefficients = coefficients.toMutableMap()
//    val otherCoefficients = other.coefficients
//
//    var (thisLeadingTermDegs, thisLeadingTermC) = thisCoefficients.leadingTerm()
//    val (otherLeadingTermDegs, otherLeadingTermC) = otherCoefficients.leadingTerm()
//
//    while (
//        thisLeadingTermDegs.size >= otherLeadingTermDegs.size &&
//        (0..otherLeadingTermDegs.lastIndex).all { thisLeadingTermDegs[it] >= otherLeadingTermDegs[it] }
//    ) {
//        val multiplierDegs =
//            thisLeadingTermDegs
//                .mapIndexed { index, deg -> deg - otherLeadingTermDegs.getOrElse(index) { 0 } }
//                .cleanUp()
//        val multiplierC = thisLeadingTermC / otherLeadingTermC
//
//        for ((degs, t) in otherCoefficients) {
//            val productDegs =
//                (0..max(degs.lastIndex, multiplierDegs.lastIndex))
//                    .map { degs.getOrElse(it) { 0 } + multiplierDegs.getOrElse(it) { 0 } }
//                    .cleanUp()
//            val productC = t * multiplierC
//            thisCoefficients[productDegs] =
//                if (productDegs in thisCoefficients) thisCoefficients[productDegs]!! - productC else -productC
//        }
//
//        thisCoefficients = thisCoefficients.filterValues { it.isNotZero() }.toMutableMap()
//
//        if (thisCoefficients.isEmpty())
//            return Polynomial(thisCoefficients, toCheckInput = false)
//
//        val t = thisCoefficients.leadingTerm()
//        thisLeadingTermDegs = t.first
//        thisLeadingTermC = t.second
//    }
//
//    return Polynomial(thisCoefficients, toCheckInput = false)
//}
//
//infix fun <T: Field<T>> Polynomial<T>.divrem(other: Polynomial<T>): Polynomial.Companion.DividingResult<T> {
//    if (other.isZero()) throw ArithmeticException("/ by zero")
//    if (isZero()) return Polynomial.Companion.DividingResult(this, this)
//
//    fun Map<List<Int>, T>.leadingTerm() =
//        this
//            .asSequence()
//            .map { Pair(it.key, it.value) }
//            .reduce { (accDegs, accC), (listDegs, listC) ->
//                for (i in 0..accDegs.lastIndex) {
//                    if (accDegs[i] > listDegs.getOrElse(i) { 0 }) return@reduce accDegs to accC
//                    if (accDegs[i] < listDegs.getOrElse(i) { 0 }) return@reduce listDegs to listC
//                }
//                if (accDegs.size < listDegs.size) listDegs to listC else accDegs to accC
//            }
//
//    var thisCoefficients = coefficients.toMutableMap()
//    val otherCoefficients = other.coefficients
//    val quotientCoefficients = HashMap<List<Int>, T>()
//
//    var (thisLeadingTermDegs, thisLeadingTermC) = thisCoefficients.leadingTerm()
//    val (otherLeadingTermDegs, otherLeadingTermC) = otherCoefficients.leadingTerm()
//
//    while (
//        thisLeadingTermDegs.size >= otherLeadingTermDegs.size &&
//        (0..otherLeadingTermDegs.lastIndex).all { thisLeadingTermDegs[it] >= otherLeadingTermDegs[it] }
//    ) {
//        val multiplierDegs =
//            thisLeadingTermDegs
//                .mapIndexed { index, deg -> deg - otherLeadingTermDegs.getOrElse(index) { 0 } }
//                .cleanUp()
//        val multiplierC = thisLeadingTermC / otherLeadingTermC
//
//        quotientCoefficients[multiplierDegs] = multiplierC
//
//        for ((degs, t) in otherCoefficients) {
//            val productDegs =
//                (0..max(degs.lastIndex, multiplierDegs.lastIndex))
//                    .map { degs.getOrElse(it) { 0 } + multiplierDegs.getOrElse(it) { 0 } }
//                    .cleanUp()
//            val productC = t * multiplierC
//            thisCoefficients[productDegs] =
//                if (productDegs in thisCoefficients) thisCoefficients[productDegs]!! - productC else -productC
//        }
//
//        thisCoefficients = thisCoefficients.filterValues { it.isNotZero() }.toMutableMap()
//
//        if (thisCoefficients.isEmpty())
//            return Polynomial.Companion.DividingResult(
//                Polynomial(quotientCoefficients, toCheckInput = false),
//                Polynomial(thisCoefficients, toCheckInput = false)
//            )
//
//        val t = thisCoefficients.leadingTerm()
//        thisLeadingTermDegs = t.first
//        thisLeadingTermC = t.second
//    }
//
//    return Polynomial.Companion.DividingResult(
//        Polynomial(quotientCoefficients, toCheckInput = false),
//        Polynomial(thisCoefficients, toCheckInput = false)
//    )
//}
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
public fun <C, A> NumberedPolynomial<C>.derivativeBy(
    algebra: A,
    variable: Int,
): Polynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = algebra {
    TODO()
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> NumberedPolynomial<C>.derivativeBy(
    algebra: A,
    variables: IntArray,
): Polynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = algebra {
    TODO()
}

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKMathAPI
public fun <C, A> NumberedPolynomial<C>.derivativeBy(
    algebra: A,
    variables: Collection<Int>,
): Polynomial<C> where  A : Ring<C>, A : NumericAlgebra<C> = derivativeBy(algebra, variables.toIntArray())

/**
 * Create a polynomial witch represents indefinite integral version of this polynomial
 */
@UnstableKMathAPI
public fun <C, A> NumberedPolynomial<C>.antiderivativeBy(
    algebra: A,
    variable: Int,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    TODO()
}

/**
 * Create a polynomial witch represents indefinite integral version of this polynomial
 */
@UnstableKMathAPI
public fun <C, A> NumberedPolynomial<C>.antiderivativeBy(
    algebra: A,
    variables: IntArray,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = algebra {
    TODO()
}

/**
 * Create a polynomial witch represents indefinite integral version of this polynomial
 */
@UnstableKMathAPI
public fun <C, A> NumberedPolynomial<C>.antiderivativeBy(
    algebra: A,
    variables: Collection<Int>,
): Polynomial<C> where  A : Field<C>, A : NumericAlgebra<C> = antiderivativeBy(algebra, variables.toIntArray())

// endregion