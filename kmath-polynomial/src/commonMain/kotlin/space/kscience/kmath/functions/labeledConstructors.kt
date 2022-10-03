/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke


/**
 * Returns the same degrees' description of the monomial, but without zero degrees.
 */
internal fun Map<Symbol, UInt>.cleanUp() = filterValues { it > 0U }

/**
 * Constructs [LabeledPolynomial] with provided coefficients map [coefs]. The map is used as is.
 */
@PublishedApi
internal inline fun <C> LabeledPolynomialAsIs(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial<C>(coefs)

/**
 * Constructs [LabeledPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The collections will be transformed to map with [toMap] and then will be used as is.
 */
@PublishedApi
internal inline fun <C> LabeledPolynomialAsIs(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial<C>(pairs.toMap())

/**
 * Constructs [LabeledPolynomial] with provided array of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The array will be transformed to map with [toMap] and then will be used as is.
 */
@PublishedApi
internal inline fun <C> LabeledPolynomialAsIs(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial<C>(pairs.toMap())

/**
 * Constructs [LabeledPolynomial] with provided coefficients map [coefs]. The map is used as is.
 *
 * **Be sure you read description of [LabeledPolynomial.coefficients]. Otherwise, you may make a mistake that will
 * cause wrong computation result or even runtime error.**
 */
@DelicatePolynomialAPI
public inline fun <C> LabeledPolynomialWithoutCheck(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial<C>(coefs)

/**
 * Constructs [LabeledPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The collections will be transformed to map with [toMap] and then will be used as is.
 *
 * **Be sure you read description of [LabeledPolynomial.coefficients]. Otherwise, you may make a mistake that will
 * cause wrong computation result or even runtime error.**
 */
@DelicatePolynomialAPI
public inline fun <C> LabeledPolynomialWithoutCheck(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial<C>(pairs.toMap())

/**
 * Constructs [LabeledPolynomial] with provided array of [pairs] of pairs "term's signature &mdash; term's coefficient".
 * The array will be transformed to map with [toMap] and then will be used as is.
 *
 * **Be sure you read description of [LabeledPolynomial.coefficients]. Otherwise, you may make a mistake that will
 * cause wrong computation result or even runtime error.**
 */
@DelicatePolynomialAPI
public inline fun <C> LabeledPolynomialWithoutCheck(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial<C>(pairs.toMap())

/**
 * Constructs [LabeledPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public fun <C> LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>, add: (C, C) -> C) : LabeledPolynomial<C> =
    LabeledPolynomialAsIs(
        coefs.mapKeys({ key, _ -> key.cleanUp() }, add)
    )

/**
 * Constructs [LabeledPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public fun <C> LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>, add: (C, C) -> C) : LabeledPolynomial<C> =
    LabeledPolynomialAsIs(
        pairs.associateBy({ it.first.cleanUp() }, { it.second }, add)
    )

/**
 * Constructs [LabeledPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public fun <C> LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>, add: (C, C) -> C) : LabeledPolynomial<C> =
    LabeledPolynomialAsIs(
        pairs.asIterable().associateBy({ it.first.cleanUp() }, { it.second }, add)
    )

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

/**
 * Constructs [LabeledPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> A.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(coefs, ::add)
/**
 * Constructs [LabeledPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(coefs) { left: C, right: C -> left + right }

/**
 * Constructs [LabeledPolynomial] with provided coefficients map [coefs].
 *
 * [coefs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [coefs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(coefs) { left: C, right: C -> left + right }

/**
 * Constructs [LabeledPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> A.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial(pairs, ::add)

/**
 * Constructs [LabeledPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial(pairs) { left: C, right: C -> left + right }
/**
 * Constructs [LabeledPolynomial] with provided collection of [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial(pairs) { left: C, right: C -> left + right }

/**
 * Constructs [LabeledPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> A.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(*pairs) { left: C, right: C -> left + right }
/**
 * Constructs [LabeledPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(*pairs) { left: C, right: C -> left + right }
/**
 * Constructs [LabeledPolynomial] with provided array [pairs] of pairs "term's signature &mdash; term's coefficient".
 *
 * [pairs] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [pairs] keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 *
 * @see LabeledPolynomialWithoutCheck
 */
public inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(*pairs) { left: C, right: C -> left + right }

/**
 * Converts [this] constant to [LabeledPolynomial].
 */
public inline fun <C> C.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this))

///**
//// * Converts [this] variable to [LabeledPolynomial].
//// */
//context(A)
//public inline fun <C, A: Ring<C>> Symbol.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf(this to 1u) to one))
///**
// * Converts [this] variable to [LabeledPolynomial].
// */
//context(LabeledPolynomialSpace<C, A>)
//public inline fun <C, A: Ring<C>> Symbol.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf(this to 1u) to constantOne))
///**
// * Converts [this] variable to [LabeledPolynomial].
// */
//context(LabeledRationalFunctionSpace<C, A>)
//public inline fun <C, A: Ring<C>> Symbol.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf(this to 1u) to constantOne))

/**
 * Marks DSL that allows to more simply create [LabeledPolynomial]s with good performance.
 *
 * For example, polynomial \(5 a^2 c^3 - 6 b\) can be described as
 * ```
 * Int.algebra {
 *     val labeledPolynomial : LabeledPolynomial<Int> = LabeledPolynomialDSL1 {
 *         5 { a inPowerOf 2u; c inPowerOf 3u } // 5 a^2 c^3 +
 *         (-6) { b inPowerOf 1u }              // (-6) b^1
 *     }
 * }
 * ```
 * @usesMathJax
 */
@DslMarker
@UnstableKMathAPI
internal annotation class LabeledPolynomialConstructorDSL1

/**
 * Builder of [LabeledPolynomial] signature. It should be used as an implicit context for lambdas that describe term signature.
 */
@UnstableKMathAPI
@LabeledPolynomialConstructorDSL1
public class DSL1LabeledPolynomialTermSignatureBuilder {
    /**
     * Signature storage. Any declaration of any variable's power updates the storage by increasing corresponding value.
     * Afterward the storage will be used as a resulting signature.
     */
    private val signature: MutableMap<Symbol, UInt> = LinkedHashMap()

    /**
     * Builds the resulting signature.
     *
     * In fact, it just returns [signature] as regular signature of type `List<UInt>`.
     */
    @PublishedApi
    internal fun build(): Map<Symbol, UInt> = signature

    /**
     * Declares power of [this] variable of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    public infix fun Symbol.inPowerOf(deg: UInt) {
        if (deg == 0u) return
        signature.putOrChange(this, deg) { it -> it + deg }
    }
    /**
     * Declares power of [this] variable of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    public inline infix fun Symbol.pow(deg: UInt): Unit = this inPowerOf deg
    /**
     * Declares power of [this] variable of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    public inline infix fun Symbol.`in`(deg: UInt): Unit = this inPowerOf deg
    /**
     * Declares power of [this] variable of degree [deg].
     *
     * Declaring another power of the same variable will increase its degree by received degree.
     */
    public inline infix fun Symbol.of(deg: UInt): Unit = this inPowerOf deg
}

/**
 * Builder of [LabeledPolynomial]. It should be used as an implicit context for lambdas that describe [LabeledPolynomial].
 */
@UnstableKMathAPI
@LabeledPolynomialConstructorDSL1
public class DSL1LabeledPolynomialBuilder<C>(
    /**
     * Summation operation that will be used to sum coefficients of monomials of same signatures.
     */
    private val add: (C, C) -> C,
    /**
     * Initial capacity of coefficients map.
     */
    initialCapacity: Int? = null
) {
    /**
     * Coefficients storage. Any declaration of any monomial updates the storage.
     * Afterward the storage will be used as a resulting coefficients map.
     */
    private val coefficients: MutableMap<Map<Symbol, UInt>, C> = if (initialCapacity != null) LinkedHashMap(initialCapacity) else LinkedHashMap()

    /**
     * Builds the resulting coefficients map.
     *
     * In fact, it just returns [coefficients] as regular coefficients map of type `Map<Map<Symbol, UInt>, C>`.
     */
    @PublishedApi
    internal fun build(): LabeledPolynomial<C> = LabeledPolynomial<C>(coefficients)

    /**
     * Declares monomial with [this] coefficient and provided [signature].
     *
     * Declaring another monomial with the same signature will add [this] coefficient to existing one. If the sum of such
     * coefficients is zero at any moment the monomial won't be removed but will be left as it is.
     */
    public infix fun C.with(signature: Map<Symbol, UInt>) {
        coefficients.putOrChange(signature, this@with, add)
    }
    /**
     * Declares monomial with [this] coefficient and signature constructed by [block].
     *
     * Declaring another monomial with the same signature will add [this] coefficient to existing one. If the sum of such
     * coefficients is zero at any moment the monomial won't be removed but will be left as it is.
     */
    public inline infix fun C.with(noinline block: DSL1LabeledPolynomialTermSignatureBuilder.() -> Unit): Unit = this.invoke(block)
    /**
     * Declares monomial with [this] coefficient and signature constructed by [block].
     *
     * Declaring another monomial with the same signature will add [this] coefficient to existing one. If the sum of such
     * coefficients is zero at any moment the monomial won't be removed but will be left as it is.
     */
    public inline operator fun C.invoke(block: DSL1LabeledPolynomialTermSignatureBuilder.() -> Unit): Unit =
        this with DSL1LabeledPolynomialTermSignatureBuilder().apply(block).build()
}

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

///**
// * Creates [LabeledPolynomial] with lambda [block] in context of [this] ring of constants.
// *
// * For example, polynomial \(5 a^2 c^3 - 6 b\) can be described as
// * ```
// * Int.algebra {
// *     val labeledPolynomial : LabeledPolynomial<Int> = LabeledPolynomialDSL1 {
// *         5 { a inPowerOf 2u; c inPowerOf 3u } // 5 a^2 c^3 +
// *         (-6) { b inPowerOf 1u }              // (-6) b^1
// *     }
// * }
// * ```
// * @usesMathJax
// */
// FIXME: For now this fabric does not let next two fabrics work. (See KT-52803.) Possible feature solutions:
//  1. `LowPriorityInOverloadResolution` becomes public. Then it should be applied to this function.
//  2. Union types are implemented. Then all three functions should be rewritten
//     as one with single union type as a (context) receiver.
//@UnstableKMathAPI
//public inline fun <C, A: Ring<C>> A.LabeledPolynomialDSL1(initialCapacity: Int? = null, block: LabeledPolynomialBuilder<C>.() -> Unit) : LabeledPolynomial<C> = LabeledPolynomialBuilder(::add, initialCapacity).apply(block).build()
/**
 * Creates [LabeledPolynomial] with lambda [block] in context of [this] ring of [LabeledPolynomial]s.
 *
 * For example, polynomial \(5 a^2 c^3 - 6 b\) can be described as
 * ```
 * Int.algebra {
 *     val labeledPolynomial : LabeledPolynomial<Int> = LabeledPolynomialDSL1 {
 *         5 { a inPowerOf 2u; c inPowerOf 3u } // 5 a^2 c^3 +
 *         (-6) { b inPowerOf 1u }              // (-6) b^1
 *     }
 * }
 * ```
 * @usesMathJax
 */
@UnstableKMathAPI
public inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomialDSL1(initialCapacity: Int? = null, block: DSL1LabeledPolynomialBuilder<C>.() -> Unit) : LabeledPolynomial<C> = DSL1LabeledPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()
/**
 * Creates [LabeledPolynomial] with lambda [block] in context of [this] field of [LabeledRationalFunction]s.
 *
 * For example, polynomial \(5 a^2 c^3 - 6 b\) can be described as
 * ```
 * Int.algebra {
 *     val labeledPolynomial : LabeledPolynomial<Int> = LabeledPolynomialDSL1 {
 *         5 { a inPowerOf 2u; c inPowerOf 3u } // 5 a^2 c^3 +
 *         (-6) { b inPowerOf 1u }              // (-6) b^1
 *     }
 * }
 * ```
 * @usesMathJax
 */
@UnstableKMathAPI
public inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomialDSL1(initialCapacity: Int? = null, block: DSL1LabeledPolynomialBuilder<C>.() -> Unit) : LabeledPolynomial<C> = DSL1LabeledPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()

/**
 * Marks DSL that allows to more simply create [LabeledPolynomial]s with good performance.
 *
 * For example, polynomial \(5 a^2 c^3 - 6 b\) can be described as
 * ```
 * Int.algebra {
 *     val numberedPolynomial : NumberedPolynomial<Int> = NumberedPolynomial {
 *         5 { a inPowerOf 2u; c inPowerOf 3u } // 5 a^2 c^3 +
 *         (-6) { b inPowerOf 1u }              // (-6) b^1
 *     }
 * }
 * ```
 * @usesMathJax
 */
@DslMarker
@UnstableKMathAPI
internal annotation class LabeledPolynomialBuilderDSL2

/**
 * Builder of [LabeledPolynomial]. It should be used as an implicit context for lambdas that describe [LabeledPolynomial].
 */
@UnstableKMathAPI
@LabeledPolynomialBuilderDSL2
public class DSL2LabeledPolynomialBuilder<C>(
    private val ring: Ring<C>,
    /**
     * Initial capacity of coefficients map.
     */
    initialCapacity: Int? = null
) {
    /**
     * Coefficients storage. Any declaration of any monomial updates the storage.
     * Afterward the storage will be used as a resulting coefficients map.
     */
    private val coefficients: MutableMap<Map<Symbol, UInt>, C> = if (initialCapacity != null) LinkedHashMap(initialCapacity) else LinkedHashMap()

    /**
     * Builds the resulting coefficients map.
     *
     * In fact, it just returns [coefficients] as regular coefficients map of type `Map<Map<Symbol, UInt>, C>`.
     */
    @PublishedApi
    internal fun build(): LabeledPolynomial<C> = LabeledPolynomial<C>(coefficients)

    public inner class Term internal constructor(
        internal val signature: Map<Symbol, UInt> = HashMap(),
        internal val coefficient: C
    )

    private inline fun submit(signature: Map<Symbol, UInt>, onPut: Ring<C>.() -> C, onChange: Ring<C>.(C) -> C) {
        coefficients.putOrChange<_, C>(signature, { ring.onPut() }, { ring.onChange(it) })
    }

    private inline fun submit(signature: Map<Symbol, UInt>, lazyCoefficient: Ring<C>.() -> C) {
        submit(signature, lazyCoefficient) { it + lazyCoefficient() }
    }

    private fun submit(signature: Map<Symbol, UInt>, coefficient: C) {
        submit(signature) { coefficient }
    }

    // TODO: `@submit` will be resolved differently. Change it to `@C`.
    private fun C.submitSelf() = submit(emptyMap()) { this@submitSelf }

    private fun Symbol.submit() = submit(mapOf(this to 1u)) { one }

    private fun Term.submit(): Submit {
        submit(signature, coefficient)
        return Submit
    }

    public object Submit

    public operator fun C.unaryPlus(): Submit {
        submitSelf()
        return Submit
    }

    public operator fun C.unaryMinus(): Submit {
        submit(emptyMap(), { -this@unaryMinus }, { it - this@unaryMinus })
        return Submit
    }

    public operator fun C.plus(other: C): Submit {
        submit(emptyMap()) { this@plus + other }
        return Submit
    }

    public operator fun C.minus(other: C): Submit {
        submit(emptyMap()) { this@minus - other }
        return Submit
    }

    public operator fun C.times(other: C): C = ring { this@times * other }

    public operator fun C.plus(other: Symbol): Submit {
        submit(emptyMap(), this)
        submit(mapOf(other to 1u), ring.one)
        return Submit
    }

    public operator fun C.minus(other: Symbol): Submit {
        submit(emptyMap(), this)
        submit(mapOf(other to 1u), { -one }, { it - one })
        return Submit
    }

    public operator fun C.times(other: Symbol): Term = Term(mapOf(other to 1u), this)

    public operator fun C.plus(other: Term): Submit {
        submit(emptyMap(), this)
        other.submit()
        return Submit
    }

    public operator fun C.minus(other: Term): Submit {
        submit(emptyMap(), this)
        submit(other.signature, { -other.coefficient }, { it - other.coefficient })
        return Submit
    }

    public operator fun C.times(other: Term): Term = Term(other.signature, ring { this@times * other.coefficient })

    public operator fun Symbol.plus(other: C): Submit {
        this.submit()
        other.submitSelf()
        return Submit
    }

    public operator fun Symbol.minus(other: C): Submit {
        this.submit()
        submit(emptyMap(), { -other }, { it - other })
        return Submit
    }

    public operator fun Symbol.times(other: C): Term = Term(mapOf(this to 1u), other)

    public operator fun Symbol.unaryPlus(): Submit {
        this.submit()
        return Submit
    }

    public operator fun Symbol.unaryMinus(): Submit {
        submit(mapOf(this to 1u), { -one }, { it - one })
        return Submit
    }

    public operator fun Symbol.plus(other: Symbol): Submit {
        this.submit()
        other.submit()
        return Submit
    }

    public operator fun Symbol.minus(other: Symbol): Submit {
        this.submit()
        submit(mapOf(other to 1u), { -one }, { it - one })
        return Submit
    }

    public operator fun Symbol.times(other: Symbol): Term =
        if (this == other) Term(mapOf(this to 2u), ring.one)
        else Term(mapOf(this to 1u, other to 1u), ring.one)

    public operator fun Symbol.plus(other: Term): Submit {
        this.submit()
        other.submit()
        return Submit
    }

    public operator fun Symbol.minus(other: Term): Submit {
        this.submit()
        submit(other.signature, { -other.coefficient }, { it - other.coefficient })
        return Submit
    }

    public operator fun Symbol.times(other: Term): Term =
        Term(
            other.signature.withPutOrChanged(this, 1u) { it -> it + 1u },
            other.coefficient
        )

    public operator fun Term.plus(other: C): Submit {
        this.submit()
        other.submitSelf()
        return Submit
    }

    public operator fun Term.minus(other: C): Submit {
        this.submit()
        submit(emptyMap(), { -other }, { it - other })
        return Submit
    }

    public operator fun Term.times(other: C): Term =
        Term(
            signature,
            ring { coefficient * other }
        )

    public operator fun Term.plus(other: Symbol): Submit {
        this.submit()
        other.submit()
        return Submit
    }

    public operator fun Term.minus(other: Symbol): Submit {
        this.submit()
        submit(mapOf(other to 1u), { -one }, { it - one })
        return Submit
    }

    public operator fun Term.times(other: Symbol): Term =
        Term(
            signature.withPutOrChanged(other, 1u) { it -> it + 1u },
            coefficient
        )

    public operator fun Term.unaryPlus(): Submit {
        this.submit()
        return Submit
    }

    public operator fun Term.unaryMinus(): Submit {
        submit(signature, { -coefficient }, { it - coefficient })
        return Submit
    }

    public operator fun Term.plus(other: Term): Submit {
        this.submit()
        other.submit()
        return Submit
    }

    public operator fun Term.minus(other: Term): Submit {
        this.submit()
        submit(other.signature, { -other.coefficient }, { it - other.coefficient })
        return Submit
    }

    public operator fun Term.times(other: Term): Term =
        Term(
            mergeBy(signature, other.signature) { deg1, deg2 -> deg1 + deg2 },
            ring { coefficient * other.coefficient }
        )
}

//@UnstableKMathAPI
//public fun <C> Ring<C>.LabeledPolynomialDSL2(initialCapacity: Int? = null, block: DSL2LabeledPolynomialBuilder<C>.() -> Unit): LabeledPolynomial<C> = DSL2LabeledPolynomialBuilder(this, initialCapacity).apply(block).build()

@UnstableKMathAPI
public fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomialDSL2(initialCapacity: Int? = null, block: DSL2LabeledPolynomialBuilder<C>.() -> Unit): LabeledPolynomial<C> = DSL2LabeledPolynomialBuilder(ring, initialCapacity).apply(block).build()

@UnstableKMathAPI
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomialDSL2(initialCapacity: Int? = null, block: DSL2LabeledPolynomialBuilder<C>.() -> Unit): LabeledPolynomial<C> = DSL2LabeledPolynomialBuilder(ring, initialCapacity).apply(block).build()

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

/**
 * Constructs [LabeledRationalFunction] with provided coefficients maps [numeratorCoefficients] and [denominatorCoefficients].
 *
 * The maps will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. the maps' keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>, denominatorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomial(denominatorCoefficients)
    )
/**
 * Constructs [LabeledRationalFunction] with provided coefficients maps [numeratorCoefficients] and [denominatorCoefficients].
 *
 * The maps will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. the maps' keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>, denominatorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomial(denominatorCoefficients)
    )

/**
 * Constructs [LabeledRationalFunction] with provided [numerator] and unit denominator.
 */
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, LabeledPolynomial(mapOf(emptyMap<Symbol, UInt>() to one)))
/**
 * Constructs [LabeledRationalFunction] with provided [numerator] and unit denominator.
 */
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, polynomialOne)

/**
 * Constructs [LabeledRationalFunction] with provided coefficients map [numeratorCoefficients] for numerator and unit
 * denominator.
 *
 * [numeratorCoefficients] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [numeratorCoefficients]'s keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        polynomialOne
    )
/**
 * Constructs [LabeledRationalFunction] with provided coefficients map [numeratorCoefficients] for numerator and unit
 * denominator.
 *
 * [numeratorCoefficients] will be "cleaned up":
 * 1. Zeros at the ends of terms' signatures (e.g. [numeratorCoefficients]'s keys) will be removed. (See [cleanUp].)
 * 1. Terms that happen to have the same signature will be summed up.
 * 1. New map will be formed of resulting terms.
 */
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to one))
    )

///**
// * Converts [this] constant to [LabeledRationalFunction].
// */
//context(A)
//public fun <C, A: Ring<C>> C.asLabeledRationalFunction() : LabeledRationalFunction<C> =
//    LabeledRationalFunction(
//        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this)),
//        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to one))
//    )
///**
// * Converts [this] constant to [LabeledRationalFunction].
// */
//context(LabeledRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> C.asLabeledRationalFunction() : LabeledRationalFunction<C> =
//    LabeledRationalFunction(
//        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to this)),
//        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to constantOne))
//    )

///**
// * Converts [this] variable to [LabeledRationalFunction].
// */
//context(A)
//public fun <C, A: Ring<C>> Symbol.asLabeledRationalFunction() : LabeledRationalFunction<C> =
//    LabeledRationalFunction(
//        LabeledPolynomialAsIs(mapOf(mapOf(this to 1u) to one)),
//        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to one))
//    )
///**
// * Converts [this] variable to [LabeledRationalFunction].
// */
//context(LabeledRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> Symbol.asLabeledRationalFunction() : LabeledRationalFunction<C> =
//    LabeledRationalFunction(
//        LabeledPolynomialAsIs(mapOf(mapOf(this to 1u) to constantOne)),
//        LabeledPolynomialAsIs(mapOf(emptyMap<Symbol, UInt>() to constantOne))
//    )