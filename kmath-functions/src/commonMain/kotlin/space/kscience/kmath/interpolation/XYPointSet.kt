package space.kscience.kmath.interpolation

import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.Buffer

public interface XYPointSet<X, Y> {
    public val size: Int
    public val x: Buffer<X>
    public val y: Buffer<Y>
}

public interface XYZPointSet<X, Y, Z> : XYPointSet<X, Y> {
    public val z: Buffer<Z>
}

internal fun <T : Comparable<T>> insureSorted(points: XYPointSet<T, *>) {
    for (i in 0 until points.size - 1)
        require(points.x[i + 1] > points.x[i]) { "Input data is not sorted at index $i" }
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

public class BufferXYPointSet<X, Y>(
    public override val x: Buffer<X>,
    public override val y: Buffer<Y>
) : XYPointSet<X, Y> {
    public override val size: Int
        get() = x.size

    init {
        require(x.size == y.size) { "Sizes of x and y buffers should be the same" }
    }
}

public fun <T> Structure2D<T>.asXYPointSet(): XYPointSet<T, T> {
    require(shape[1] == 2) { "Structure second dimension should be of size 2" }

    return object : XYPointSet<T, T> {
        override val size: Int get() = this@asXYPointSet.shape[0]
        override val x: Buffer<T> get() = NDStructureColumn(this@asXYPointSet, 0)
        override val y: Buffer<T> get() = NDStructureColumn(this@asXYPointSet, 1)
    }
}
