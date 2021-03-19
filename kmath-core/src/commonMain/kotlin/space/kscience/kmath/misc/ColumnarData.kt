package space.kscience.kmath.misc

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

