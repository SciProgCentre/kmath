/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Ring


/**
 * Returns the same degrees' description of the monomial, but without zero degrees.
 */
internal fun Map<Symbol, UInt>.cleanUp() = filterValues { it > 0U }

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>, toCheckInput: Boolean = true) : LabeledPolynomial<C> = ring.LabeledPolynomial(coefs, toCheckInput)
@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>, toCheckInput: Boolean = true) : LabeledPolynomial<C> = ring.LabeledPolynomial(coefs, toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>, toCheckInput: Boolean = true) : LabeledPolynomial<C> {
    if (!toCheckInput) return LabeledPolynomial<C>(coefs)

    val fixedCoefs = LinkedHashMap<Map<Symbol, UInt>, C>(coefs.size)

    for (entry in coefs) {
        val key = entry.key.cleanUp()
        val value = entry.value
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return LabeledPolynomial<C>(fixedCoefs)
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>, toCheckInput: Boolean = true) : LabeledPolynomial<C> = ring.LabeledPolynomial(pairs, toCheckInput)
@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>, toCheckInput: Boolean = true) : LabeledPolynomial<C> = ring.LabeledPolynomial(pairs, toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>, toCheckInput: Boolean = true) : LabeledPolynomial<C> {
    if (!toCheckInput) return LabeledPolynomial<C>(pairs.toMap())

    val fixedCoefs = LinkedHashMap<Map<Symbol, UInt>, C>(pairs.size)

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return LabeledPolynomial<C>(fixedCoefs)
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>, toCheckInput: Boolean = true) : LabeledPolynomial<C> = ring.LabeledPolynomial(pairs = pairs, toCheckInput = toCheckInput)
@Suppress("FunctionName", "NOTHING_TO_INLINE")
internal inline fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>, toCheckInput: Boolean = true) : LabeledPolynomial<C> = ring.LabeledPolynomial(pairs = pairs, toCheckInput = toCheckInput)
@Suppress("FunctionName")
internal fun <C, A: Ring<C>> A.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>, toCheckInput: Boolean = true) : LabeledPolynomial<C> {
    if (!toCheckInput) return LabeledPolynomial<C>(pairs.toMap())

    val fixedCoefs = LinkedHashMap<Map<Symbol, UInt>, C>(pairs.size)

    for (entry in pairs) {
        val key = entry.first.cleanUp()
        val value = entry.second
        fixedCoefs[key] = if (key in fixedCoefs) fixedCoefs[key]!! + value else value
    }

    return LabeledPolynomial<C>(fixedCoefs)
}

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(coefs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(coefs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(coefs: Map<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(coefs, toCheckInput = true)

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial(pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial(pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(pairs: Collection<Pair<Map<Symbol, UInt>, C>>) : LabeledPolynomial<C> = LabeledPolynomial(pairs, toCheckInput = true)

@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(*pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledPolynomialSpace<C, A>.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(*pairs, toCheckInput = true)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledPolynomial(vararg pairs: Pair<Map<Symbol, UInt>, C>) : LabeledPolynomial<C> = LabeledPolynomial(*pairs, toCheckInput = true)

//context(A)
//public fun <C, A: Ring<C>> Symbol.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf(this to 1u) to one))
//context(LabeledPolynomialSpace<C, A>)
//public fun <C, A: Ring<C>> Symbol.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf(this to 1u) to constantOne))
//context(LabeledRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> Symbol.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(mapOf(this to 1u) to constantOne))

public fun <C> C.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(mapOf(emptyMap<Symbol, UInt>() to this))

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>, denominatorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients, toCheckInput = true),
        LabeledPolynomial(denominatorCoefficients, toCheckInput = true)
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>, denominatorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients, toCheckInput = true),
        LabeledPolynomial(denominatorCoefficients, toCheckInput = true)
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, polynomialOne)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, LabeledPolynomial(mapOf(emptyMap<Symbol, UInt>() to one), toCheckInput = false))
@Suppress("FunctionName")
public fun <C, A: Ring<C>> LabeledRationalFunctionSpace<C, A>.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients, toCheckInput = true),
        polynomialOne
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.LabeledRationalFunction(numeratorCoefficients: Map<Map<Symbol, UInt>, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients, toCheckInput = true),
        LabeledPolynomial(mapOf(emptyMap<Symbol, UInt>() to one), toCheckInput = false)
    )

//context(A)
//public fun <C, A: Ring<C>> Symbol.asLabeledRationalFunction() : LabeledRationalFunction<C> = LabeledRationalFunction(asLabeledPolynomial())
//context(LabeledRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> Symbol.asLabeledRationalFunction() : LabeledRationalFunction<C> = LabeledRationalFunction(asLabeledPolynomial())

//context(A)
//public fun <C, A: Ring<C>> C.asLabeledRationalFunction() : LabeledRationalFunction<C> = LabeledRationalFunction(asLabeledPolynomial())
//context(LabeledRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> C.asLabeledRationalFunction() : LabeledRationalFunction<C> = LabeledRationalFunction(asLabeledPolynomial())