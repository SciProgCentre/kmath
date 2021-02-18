package space.kscience.kmath.expressions

import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.BufferFactory

/**
 * An environment to easy transform indexed variables to symbols and back.
 * TODO requires multi-receivers to be beautiful
 */
public interface SymbolIndexer {
    public val symbols: List<Symbol>
    public fun indexOf(symbol: Symbol): Int = symbols.indexOf(symbol)

    public operator fun <T> List<T>.get(symbol: Symbol): T {
        require(size == symbols.size) { "The input list size for indexer should be ${symbols.size} but $size found" }
        return get(this@SymbolIndexer.indexOf(symbol))
    }

    public operator fun <T> Array<T>.get(symbol: Symbol): T {
        require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
        return get(this@SymbolIndexer.indexOf(symbol))
    }

    public operator fun DoubleArray.get(symbol: Symbol): Double {
        require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
        return get(this@SymbolIndexer.indexOf(symbol))
    }

    public operator fun <T> Point<T>.get(symbol: Symbol): T {
        require(size == symbols.size) { "The input buffer size for indexer should be ${symbols.size} but $size found" }
        return get(this@SymbolIndexer.indexOf(symbol))
    }

    public fun DoubleArray.toMap(): Map<Symbol, Double> {
        require(size == symbols.size) { "The input array size for indexer should be ${symbols.size} but $size found" }
        return symbols.indices.associate { symbols[it] to get(it) }
    }

    public operator fun <T> Structure2D<T>.get(rowSymbol: Symbol, columnSymbol: Symbol): T =
        get(indexOf(rowSymbol), indexOf(columnSymbol))


    public fun <T> Map<Symbol, T>.toList(): List<T> = symbols.map { getValue(it) }

    public fun <T> Map<Symbol, T>.toPoint(bufferFactory: BufferFactory<T>): Point<T> =
        bufferFactory(symbols.size) { getValue(symbols[it]) }

    public fun Map<Symbol, Double>.toDoubleArray(): DoubleArray = DoubleArray(symbols.size) { getValue(symbols[it]) }
}

public inline class SimpleSymbolIndexer(override val symbols: List<Symbol>) : SymbolIndexer

/**
 * Execute the block with symbol indexer based on given symbol order
 */
public inline fun <R> withSymbols(vararg symbols: Symbol, block: SymbolIndexer.() -> R): R =
    with(SimpleSymbolIndexer(symbols.toList()), block)

public inline fun <R> withSymbols(symbols: Collection<Symbol>, block: SymbolIndexer.() -> R): R =
    with(SimpleSymbolIndexer(symbols.toList()), block)