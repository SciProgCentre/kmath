/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke


/**
 * Returns the same degrees' description of the monomial, but without extra zero degrees on the end.
 */
internal fun List<UInt>.cleanUp() = subList(0, indexOfLast { it != 0U } + 1)

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(coefs, toCheckInput)
@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(coefs, toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.NumberedPolynomial(coefs: Map<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> {
    if (!toCheckInput) return NumberedPolynomial<C>(coefs)

    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in coefs) {
        val key = entry.key.cleanUp()
        val value = entry.value
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return NumberedPolynomial<C>(fixedCoefs)
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(pairs, toCheckInput)
@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(pairs, toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>, toCheckInput: Boolean = true) : NumberedPolynomial<C> {
    if (!toCheckInput) return NumberedPolynomial<C>(pairs.toMap())

    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return NumberedPolynomial<C>(fixedCoefs)
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(pairs = pairs, toCheckInput = toCheckInput)
@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> = ring.NumberedPolynomial(pairs = pairs, toCheckInput = toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>, toCheckInput: Boolean = true) : NumberedPolynomial<C> {
    if (!toCheckInput) return NumberedPolynomial<C>(pairs.toMap())

    val fixedCoefs = mutableMapOf<List<UInt>, C>()

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return NumberedPolynomial<C>(fixedCoefs)
}

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(coefs: Map<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs, toCheckInput = true)

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<List<UInt>, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs, toCheckInput = true)

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<List<UInt>, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs, toCheckInput = true)

public fun <C> C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomial<C>(mapOf(emptyList<UInt>() to this))

@DslMarker
internal annotation class NumberedPolynomialConstructorDSL

@NumberedPolynomialConstructorDSL
public class NumberedPolynomialTermSignatureBuilder {
    private val signature: MutableList<UInt> = ArrayList()
    public fun build(): List<UInt> = signature
    public infix fun Int.inPowerOf(deg: UInt) {
        if (this > signature.lastIndex) {
            signature.addAll(List(this - signature.lastIndex - 1) { 0u })
            signature.add(deg)
        } else {
            signature[this] = deg
        }
    }
    public infix fun Int.pow(deg: UInt): Unit = this inPowerOf deg
    public infix fun Int.of(deg: UInt): Unit = this inPowerOf deg
}

@NumberedPolynomialConstructorDSL
public class NumberedPolynomialBuilderOverRing<C> internal constructor(internal val context: Ring<C>, capacity: Int = 0) {
    private val coefficients: MutableMap<List<UInt>, C> = LinkedHashMap(capacity)
    public fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)
    public operator fun C.invoke(block: NumberedPolynomialTermSignatureBuilder.() -> Unit) {
        val signature = NumberedPolynomialTermSignatureBuilder().apply(block).build()
        coefficients[signature] = context { coefficients.getOrElse(signature) { zero } + this@invoke }
    }
    public infix fun C.with(block: NumberedPolynomialTermSignatureBuilder.() -> Unit): Unit = this.invoke(block)
}

@NumberedPolynomialConstructorDSL
public class NumberedPolynomialBuilderOverPolynomialSpace<C> internal constructor(internal val context: NumberedPolynomialSpace<C, *>, capacity: Int = 0) {
    private val coefficients: MutableMap<List<UInt>, C> = LinkedHashMap(capacity)
    public fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)
    public operator fun C.invoke(block: NumberedPolynomialTermSignatureBuilder.() -> Unit) {
        val signature = NumberedPolynomialTermSignatureBuilder().apply(block).build()
        coefficients[signature] = context { coefficients.getOrElse(signature) { constantZero } + this@invoke }
    }
}

@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(block: NumberedPolynomialBuilderOverRing<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverRing(this).apply(block).build()
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedPolynomial(capacity: Int, block: NumberedPolynomialBuilderOverRing<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverRing(this, capacity).apply(block).build()
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(block: NumberedPolynomialBuilderOverPolynomialSpace<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverPolynomialSpace(this).apply(block).build()
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(capacity: Int, block: NumberedPolynomialBuilderOverPolynomialSpace<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilderOverPolynomialSpace(this, capacity).apply(block).build()

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>, denominatorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients, toCheckInput = true),
        NumberedPolynomial(denominatorCoefficients, toCheckInput = true)
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>, denominatorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients, toCheckInput = true),
        NumberedPolynomial(denominatorCoefficients, toCheckInput = true)
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numerator: NumberedPolynomial<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(numerator, polynomialOne)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numerator: NumberedPolynomial<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(numerator, NumberedPolynomial(mapOf(emptyList<UInt>() to one), toCheckInput = false))
@Suppress("FunctionName")
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients, toCheckInput = true),
        polynomialOne
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numeratorCoefficients: Map<List<UInt>, C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients, toCheckInput = true),
        NumberedPolynomial(mapOf(emptyList<UInt>() to one), toCheckInput = false)
    )

//context(A)
//public fun <C, A: Ring<C>> C.asNumberedRationalFunction() : NumberedRationalFunction<C> = NumberedRationalFunction(asLabeledPolynomial())
//context(NumberedRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> C.asNumberedRationalFunction() : NumberedRationalFunction<C> = NumberedRationalFunction(asLabeledPolynomial())