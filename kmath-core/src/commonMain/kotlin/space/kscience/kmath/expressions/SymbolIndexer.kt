/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.Float64Buffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

/**
 * An environment to easy transform indexed variables to symbols and back.
 */
public interface SymbolIndexer {
    public val symbols: List<Symbol>
    public fun indexOf(symbol: Symbol): Int = symbols.indexOf(symbol)
}

context(indexer: SymbolIndexer)
public operator fun <T> List<T>.get(symbol: Symbol): T  = with(indexer){
    require(size == symbols.size) { "The input list size for indexer should be ${symbols.size} but $size found" }
    return get(indexOf(symbol))
}

context(indexer: SymbolIndexer)
public operator fun <T> Array<T>.get(symbol: Symbol): T  = with(indexer){
    require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
    return get(indexOf(symbol))
}

context(indexer: SymbolIndexer)
public operator fun DoubleArray.get(symbol: Symbol): Double = with(indexer){
    require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
    return get(indexOf(symbol))
}

context(indexer: SymbolIndexer)
public operator fun <T> Point<T>.get(symbol: Symbol): T  = with(indexer){
    require(size == symbols.size) { "The input buffer size for indexer should be ${symbols.size} but $size found" }
    return get(indexOf(symbol))
}

context(indexer: SymbolIndexer)
public fun DoubleArray.toMap(): Map<Symbol, Double>  = with(indexer){
    require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
    return symbols.indices.associate { symbols[it] to get(it) }
}

context(indexer: SymbolIndexer)
public fun <T> Point<T>.toMap(): Map<Symbol, T>  = with(indexer){
    require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
    return symbols.indices.associate { symbols[it] to get(it) }
}

context(indexer: SymbolIndexer)
public operator fun <T> Structure2D<T>.get(rowSymbol: Symbol, columnSymbol: Symbol): T  = with(indexer) {
    get(indexOf(rowSymbol), indexOf(columnSymbol))
}

context(indexer: SymbolIndexer)
public fun <T> Map<Symbol, T>.toList(): List<T> = indexer.symbols.map { getValue(it) }

context(indexer: SymbolIndexer)
public fun <T> Map<Symbol, T>.toPoint(bufferFactory: BufferFactory<T>): Point<T>  = with(indexer) {
    bufferFactory(symbols.size) { getValue(symbols[it]) }
}

context(indexer: SymbolIndexer)
public fun Map<Symbol, Double>.toPoint(): Float64Buffer  = with(indexer) {
    Float64Buffer(symbols.size) { getValue(symbols[it]) }
}

context(indexer: SymbolIndexer)
public fun Map<Symbol, Double>.toDoubleArray(): DoubleArray  = with(indexer){
    DoubleArray(symbols.size) { getValue(symbols[it]) }
}

public val SymbolIndexer.size: Int get() = symbols.size

@JvmInline
public value class SimpleSymbolIndexer(override val symbols: List<Symbol>) : SymbolIndexer

/**
 * Execute the block with symbol indexer based on given symbol order
 */
public inline fun <R> withSymbols(vararg symbols: Symbol, block: SymbolIndexer.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return with(SimpleSymbolIndexer(symbols.toList()), block)
}

public inline fun <R> withSymbols(symbols: Collection<Symbol>, block: SymbolIndexer.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return with(SimpleSymbolIndexer(symbols.toList()), block)
}
