package space.kscience.kmath.misc

import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.Buffer

@UnstableKMathAPI
public interface XYPointSet<T, X : T, Y : T> : ColumnarData<T> {
    public val x: Buffer<X>
    public val y: Buffer<Y>

    override fun get(symbol: Symbol): Buffer<T> = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        else -> error("A column for symbol $symbol not found")
    }
}

@UnstableKMathAPI
public interface XYZPointSet<T, X : T, Y : T, Z : T> : XYPointSet<T, X, Y> {
    public val z: Buffer<Z>

    override fun get(symbol: Symbol): Buffer<T> = when (symbol) {
        Symbol.x -> x
        Symbol.y -> y
        Symbol.z -> z
        else -> error("A column for symbol $symbol not found")
    }
}


public class NDStructureColumn<T>(public val structure: Structure2D<T>, public val column: Int) : Buffer<T> {
    public override val size: Int
        get() = structure.rowNum

    init {
        require(column < structure.colNum) { "Column index is outside of structure column range" }
    }

    public override operator fun get(index: Int): T = structure[index, column]
    public override operator fun iterator(): Iterator<T> = sequence { repeat(size) { yield(get(it)) } }.iterator()
}

@UnstableKMathAPI
public class BufferXYPointSet<T, X : T, Y : T>(
    public override val x: Buffer<X>,
    public override val y: Buffer<Y>,
) : XYPointSet<T, X, Y> {
    public override val size: Int get() = x.size

    init {
        require(x.size == y.size) { "Sizes of x and y buffers should be the same" }
    }
}

@UnstableKMathAPI
public fun <T> Structure2D<T>.asXYPointSet(): XYPointSet<T, T, T> {
    require(shape[1] == 2) { "Structure second dimension should be of size 2" }

    return object : XYPointSet<T, T, T> {
        override val size: Int get() = this@asXYPointSet.shape[0]
        override val x: Buffer<T> get() = NDStructureColumn(this@asXYPointSet, 0)
        override val y: Buffer<T> get() = NDStructureColumn(this@asXYPointSet, 1)
    }
}
