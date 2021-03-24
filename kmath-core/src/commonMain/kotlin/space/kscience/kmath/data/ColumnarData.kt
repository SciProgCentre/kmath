package space.kscience.kmath.data

import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.Buffer

/**
 * A column-based data set with all columns of the same size (not necessary fixed in time).
 * The column could be retrieved by a [get] operation.
 */
@UnstableKMathAPI
public interface ColumnarData<out T> {
    public val size: Int

    public operator fun get(symbol: Symbol): Buffer<T>
}

/**
 * A zero-copy method to represent a [Structure2D] as a two-column x-y data.
 * There could more than two columns in the structure.
 */
@UnstableKMathAPI
public fun <T> Structure2D<T>.asColumnarData(mapping: Map<Symbol, Int>): ColumnarData<T> {
    require(shape[1] >= mapping.maxOf { it.value }) { "Column index out of bounds" }
    return object : ColumnarData<T> {
        override val size: Int get() = shape[0]
        override fun get(symbol: Symbol): Buffer<T> {
            val index = mapping[symbol] ?: error("No column mapping for symbol $symbol")
            return columns[index]
        }
    }
}

