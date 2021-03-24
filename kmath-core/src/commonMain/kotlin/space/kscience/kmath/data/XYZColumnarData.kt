package space.kscience.kmath.data

import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer

/**
 * A [XYColumnarData] with guaranteed [x], [y] and [z] columns designated by corresponding symbols.
 * Inherits [XYColumnarData].
 */
@UnstableKMathAPI
public interface XYZColumnarData<T, out X : T, out Y : T, out Z : T> : XYColumnarData<T, X, Y> {
    public val z: Buffer<Z>

    override fun get(symbol: Symbol): Buffer<T> = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        Symbol.z -> z
        else -> error("A column for symbol $symbol not found")
    }
}