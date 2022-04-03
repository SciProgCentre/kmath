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
@UnstableKMathAPI
internal annotation class NumberedPolynomialConstructorDSL

@UnstableKMathAPI
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
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun Int.pow(deg: UInt): Unit = this inPowerOf deg
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun Int.`in`(deg: UInt): Unit = this inPowerOf deg
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun Int.of(deg: UInt): Unit = this inPowerOf deg
}

@UnstableKMathAPI
public class NumberedPolynomialBuilder<C>(private val zero: C, private val add: (C, C) -> C, capacity: Int = 0) {
    private val coefficients: MutableMap<List<UInt>, C> = LinkedHashMap(capacity)
    public fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)
    public operator fun C.invoke(block: NumberedPolynomialTermSignatureBuilder.() -> Unit) {
        val signature = NumberedPolynomialTermSignatureBuilder().apply(block).build()
        coefficients[signature] = add(coefficients.getOrElse(signature) { zero }, this@invoke)
    }
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun C.with(noinline block: NumberedPolynomialTermSignatureBuilder.() -> Unit): Unit = this.invoke(block)
    @Suppress("NOTHING_TO_INLINE")
    public inline infix fun (NumberedPolynomialTermSignatureBuilder.() -> Unit).with(coef: C): Unit = coef.invoke(this)
    @Suppress("NOTHING_TO_INLINE")
    public infix fun sig(block: NumberedPolynomialTermSignatureBuilder.() -> Unit): NumberedPolynomialTermSignatureBuilder.() -> Unit = block
}

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

@UnstableKMathAPI
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public inline fun <C, A: Ring<C>> A.NumberedPolynomial(block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder(zero, ::add).apply(block).build()
@UnstableKMathAPI
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public inline fun <C, A: Ring<C>> A.NumberedPolynomial(capacity: Int, block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder(zero, ::add, capacity).apply(block).build()
@UnstableKMathAPI
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder(constantZero, { left: C, right: C -> left + right}).apply(block).build()
@UnstableKMathAPI
@NumberedPolynomialConstructorDSL
@Suppress("FunctionName")
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(capacity: Int, block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder(constantZero, { left: C, right: C -> left + right}, capacity).apply(block).build()

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