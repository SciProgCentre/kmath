/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring


/**
 * Returns the same degrees' description of the monomial, but without extra zero degrees on the end.
 */
internal fun List<UInt>.cleanUp() = subList(0, indexOfLast { it != 0U } + 1)

/**
 * Constructs [NumberedPolynomial] with provided coefficients map [coefs]. The map is used as is.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
@PublishedApi
internal inline fun <C> NumberedPolynomialAsIs(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(coefs)

/**
 * Constructs [NumberedPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The collections will be transformed to map with [toMap] and then will be used as is.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
@PublishedApi
internal inline fun <C> NumberedPolynomialAsIs(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

/**
 * Constructs [NumberedPolynomial] with provided array of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The array will be transformed to map with [toMap] and then will be used as is.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
@PublishedApi
internal inline fun <C> NumberedPolynomialAsIs(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

/**
 * Constructs [NumberedPolynomial] with provided coefficients map [coefs]. The map is used as is.
 *
 * **Be sure you read description of [NumberedPolynomial.coefficients]. Otherwise, you may make a mistake that will
 * cause wrong computation result or even runtime error.**
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
@DelicatePolynomialAPI
public inline fun <C> NumberedPolynomialWithoutCheck(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(coefs)

/**
 * Constructs [NumberedPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The collections will be transformed to map with [toMap] and then will be used as is.
 *
 * **Be sure you read description of [NumberedPolynomial.coefficients]. Otherwise, you may make a mistake that will
 * cause wrong computation result or even runtime error.**
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
@DelicatePolynomialAPI
public inline fun <C> NumberedPolynomialWithoutCheck(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

/**
 * Constructs [NumberedPolynomial] with provided array of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The array will be transformed to map with [toMap] and then will be used as is.
 *
 * **Be sure you read description of [NumberedPolynomial.coefficients]. Otherwise, you may make a mistake that will
 * cause wrong computation result or even runtime error.**
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
@DelicatePolynomialAPI
public inline fun <C> NumberedPolynomialWithoutCheck(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

/**
 * Constructs [NumberedPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C> NumberedPolynomial(coefs: Map<List<UInt>, C>, add: (C, C) -> C) : NumberedPolynomial<C> {
    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in coefs) {
        val key = entry.key.cleanUp()
        val value = entry.value
        fixedCoefs[key] = if (key in fixedCoefs) add(fixedCoefs[key]!!, value) else value
    }

    return NumberedPolynomial<C>(fixedCoefs)
}

/**
 * Constructs [NumberedPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C> NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, add: (C, C) -> C) : NumberedPolynomial<C> {
    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) add(fixedCoefs[key]!!, value) else value
    }

    return NumberedPolynomial<C>(fixedCoefs)
}

/**
 * Constructs [NumberedPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C> NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, add: (C, C) -> C) : NumberedPolynomial<C> {
    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) add(fixedCoefs[key]!!, value) else value
    }

    return NumberedPolynomial<C>(fixedCoefs)
}

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

/**
 * Constructs [NumberedPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> A.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs, ::add)
/**
 * Constructs [NumberedPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs) { left: C, right: C -> left + right }

/**
 * Constructs [NumberedPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs) { left: C, right: C -> left + right }

/**
 * Constructs [NumberedPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> A.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs, ::add)

/**
 * Constructs [NumberedPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs) { left: C, right: C -> left + right }
/**
 * Constructs [NumberedPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs) { left: C, right: C -> left + right }

/**
 * Constructs [NumberedPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> A.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs) { left: C, right: C -> left + right }
/**
 * Constructs [NumberedPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs) { left: C, right: C -> left + right }
/**
 * Constructs [NumberedPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName", "NOTHING_TO_INLINE")
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs) { left: C, right: C -> left + right }

/**
 * Converts [this] constant to [NumberedPolynomial].
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <C> C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to this))

/**
 * Marks DSL that allows to more simply create [NumberedPolynomial]s with good performance.
 *
 * For example, polynomial `5 x_1^2 x_3^3 - 6 x_2` can be described as
 * ```
 * Int.algebra {
 *     val numberedPolynomial : NumberedPolynomial<Int> = NumberedPolynomial {
 *         5 { 1 inPowerOf 2u; 3 inPowerOf 3u } // 5 x_1^2 x_3^3 +
 *         (-6) { 2 inPowerOf 1u }              // (-6) x_2^1
 *     }
 * }
 * ```
 */
@DslMarker
@UnstableKMathAPI
internal annotation class NumberedPolynomialConstructorDSL

/**
 * Builder of [NumberedPolynomial] signature. It should be used as an implicit context for lambdas that describe term signature.
 */
@UnstableKMathAPI
@NumberedPolynomialConstructorDSL
public class NumberedPolynomialTermSignatureBuilder {
    /**
     * Signature storage. Any declaration of any variable's power updates the storage by increasing corresponding value.
     * Afterward the storage will be used as a resulting signature.
     */
    private val signature: MutableList<UInt> = ArrayList()

    /**
     * Builds the resulting signature.
     *
     * In fact, it just returns [signature] as regular signature of type `List<UInt>`.
     */
    internal fun build(): List<UInt> = signature

    /**
     * Declares power of variable #[this] of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    public infix fun Int.inPowerOf(deg: UInt) {
        val index = this - 1
        if (index > signature.lastIndex) {
            signature.addAll(List(index - signature.lastIndex - 1) { 0u })
            signature.add(deg)
        } else {
            signature[index] += deg
        }
    }
    /**
     * Declares power of variable #[this] of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun Int.pow(deg: UInt): Unit = this inPowerOf deg
    /**
     * Declares power of variable #[this] of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun Int.`in`(deg: UInt): Unit = this inPowerOf deg
    /**
     * Declares power of variable #[this] of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun Int.of(deg: UInt): Unit = this inPowerOf deg
}

/**
 * Builder of [NumberedPolynomial]. It should be used as an implicit context for lambdas that describe [NumberedPolynomial].
 */
@UnstableKMathAPI
@NumberedPolynomialConstructorDSL
public class NumberedPolynomialBuilder<C>(
    /**
     * Summation operation that will be used to sum coefficients of monomials of same signatures.
     */
    private val add: (C, C) -> C,
    /**
     * Initial capacity of coefficients map.
     */
    initialCapacity: Int = 0
) {
    /**
     * Coefficients storage. Any declaration of any monomial updates the storage.
     * Afterward the storage will be used as a resulting coefficients map.
     */
    private val coefficients: MutableMap<List<UInt>, C> = LinkedHashMap(initialCapacity)

    /**
     * Builds the resulting coefficients map.
     *
     * In fact, it just returns [coefficients] as regular coefficients map of type `Map<List<UInt>, C>`.
     */
    @PublishedApi
    internal fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)

    /**
     * Declares monomial with [this] coefficient and provided [signature].
     *
     * Declaring another monomial with the same signature will add [this] coefficient to existing one. If the sum of such
     * coefficients is zero at any moment the monomial won't be removed but will be left as it is.
     */
    public infix fun C.with(signature: List<UInt>) {
        if (signature in coefficients) coefficients[signature] = add(coefficients[signature]!!, this@with)
        else coefficients[signature] = this@with
    }
    /**
     * Declares monomial with [this] coefficient and signature constructed by [block].
     *
     * Declaring another monomial with the same signature will add [this] coefficient to existing one. If the sum of such
     * coefficients is zero at any moment the monomial won't be removed but will be left as it is.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun C.with(noinline block: NumberedPolynomialTermSignatureBuilder.() -> Unit): Unit = this.invoke(block)
    /**
     * Declares monomial with [this] coefficient and signature constructed by [block].
     *
     * Declaring another monomial with the same signature will add [this] coefficient to existing one. If the sum of such
     * coefficients is zero at any moment the monomial won't be removed but will be left as it is.
     */
    public operator fun C.invoke(block: NumberedPolynomialTermSignatureBuilder.() -> Unit): Unit =
        this with NumberedPolynomialTermSignatureBuilder().apply(block).build()
}

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

///**
// * Creates [NumberedPolynomial] with lambda [block] in context of [this] ring of constants.
// *
// * For example, polynomial `5 x_1^2 x_3^3 - 6 x_2` can be described as
// * ```
// * Int.algebra {
// *     val numberedPolynomial : NumberedPolynomial<Int> = NumberedPolynomial {
// *         5 { 1 inPowerOf 2u; 3 inPowerOf 3u } // 5 x_1^2 x_3^3 +
// *         (-6) { 2 inPowerOf 1u }              // (-6) x_2^1
// *     }
// * }
// * ```
// */
// FIXME: For now this fabric does not let next two fabrics work. (See KT-52803.) Possible feature solutions:
//  1. `LowPriorityInOverloadResolution` becomes public. Then it should be applied to this function.
//  2. Union types are implemented. Then all three functions should be rewritten
//     as one with single union type as a (context) receiver.
//@UnstableKMathAPI
//@Suppress("FunctionName")
//public inline fun <C, A: Ring<C>> A.NumberedPolynomial(initialCapacity: Int = 0, block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder(::add, initialCapacity).apply(block).build()
/**
 * Creates [NumberedPolynomial] with lambda [block] in context of [this] ring of [NumberedPolynomial]s.
 *
 * For example, polynomial `5 x_1^2 x_3^3 - 6 x_2` can be described as
 * ```
 * Int.algebra {
 *     val numberedPolynomial : NumberedPolynomial<Int> = NumberedPolynomial {
 *         5 { 1 inPowerOf 2u; 3 inPowerOf 3u } // 5 x_1^2 x_3^3 +
 *         (-6) { 2 inPowerOf 1u }              // (-6) x_2^1
 *     }
 * }
 * ```
 */
@UnstableKMathAPI
@Suppress("FunctionName")
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(initialCapacity: Int = 0, block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()
/**
 * Creates [NumberedPolynomial] with lambda [block] in context of [this] field of [NumberedRationalFunction]s.
 *
 * For example, polynomial `5 x_1^2 x_3^3 - 6 x_2` can be described as
 * ```
 * Int.algebra {
 *     val numberedPolynomial : NumberedPolynomial<Int> = NumberedPolynomial {
 *         5 { 1 inPowerOf 2u; 3 inPowerOf 3u } // 5 x_1^2 x_3^3 +
 *         (-6) { 2 inPowerOf 1u }              // (-6) x_2^1
 *     }
 * }
 * ```
 */
@UnstableKMathAPI
@Suppress("FunctionName")
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(initialCapacity: Int = 0, block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

/**
 * Constructs [NumberedRationalFunction] with provided coefficients maps [numeratorCoefficients] and [denominatorCoefficients].
 *
 * The maps will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. the maps' keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>, denominatorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        NumberedPolynomial(denominatorCoefficients)
    )
/**
 * Constructs [NumberedRationalFunction] with provided coefficients maps [numeratorCoefficients] and [denominatorCoefficients].
 *
 * The maps will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. the maps' keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>, denominatorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        NumberedPolynomial(denominatorCoefficients)
    )

/**
 * Constructs [NumberedRationalFunction] with provided [numerator] and unit denominator.
 */
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numerator: NumberedPolynomial<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(numerator, NumberedPolynomial(mapOf(emptyList<UInt>() to one)))
/**
 * Constructs [NumberedRationalFunction] with provided [numerator] and unit denominator.
 */
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numerator: NumberedPolynomial<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(numerator, polynomialOne)

/**
 * Constructs [NumberedRationalFunction] with provided coefficients map [numeratorCoefficients] for numerator and unit
 * denominator.
 *
 * [numeratorCoefficients] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [numeratorCoefficients]'s keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        polynomialOne
    )
/**
 * Constructs [NumberedRationalFunction] with provided coefficients map [numeratorCoefficients] for numerator and unit
 * denominator.
 *
 * [numeratorCoefficients] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [numeratorCoefficients]'s keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to one))
    )

///**
// * Converts [this] coefficient to [NumberedRationalFunction].
// */
//context(A)
//public fun <C, A: Ring<C>> C.asNumberedRationalFunction() : NumberedRationalFunction<C> =
//    NumberedRationalFunction(
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to this)),
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to one))
//    )
///**
// * Converts [this] coefficient to [NumberedRationalFunction].
// */
//context(NumberedRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> C.asNumberedRationalFunction() : NumberedRationalFunction<C> =
//    NumberedRationalFunction(
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to this)),
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to constantOne))
//    )