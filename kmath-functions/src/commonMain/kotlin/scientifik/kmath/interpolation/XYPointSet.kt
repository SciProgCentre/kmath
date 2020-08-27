package scientifik.kmath.interpolation

import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.Structure2D

interface XYPointSet<X, Y> {
    val size: Int
    val x: Buffer<X>
    val y: Buffer<Y>
}

interface XYZPointSet<X, Y, Z> : XYPointSet<X, Y> {
    val z: Buffer<Z>
}

internal fun <T : Comparable<T>> insureSorted(points: XYPointSet<T, *>) {
    for (i in 0 until points.size - 1) require(points.x[i + 1] > points.x[i]) { "Input data is not sorted at index $i" }
}

class NDStructureColumn<T>(val structure: Structure2D<T>, val column: Int) : Buffer<T> {
    init {
        require(column < structure.colNum) { "Column index is outside of structure column range" }
    }

    override val size: Int get() = structure.rowNum

    override operator fun get(index: Int): T = structure[index, column]

    override operator fun iterator(): Iterator<T> = sequence {
        repeat(size) {
            yield(get(it))
        }
    }.iterator()
}

class BufferXYPointSet<X, Y>(override val x: Buffer<X>, override val y: Buffer<Y>) : XYPointSet<X, Y> {
    init {
        require(x.size == y.size) { "Sizes of x and y buffers should be the same" }
    }

    override val size: Int
        get() = x.size
}

fun <T> Structure2D<T>.asXYPointSet(): XYPointSet<T, T> {
    require(shape[1] == 2) { "Structure second dimension should be of size 2" }
    return object : XYPointSet<T, T> {
        override val size: Int get() = this@asXYPointSet.shape[0]
        override val x: Buffer<T> get() = NDStructureColumn(this@asXYPointSet, 0)
        override val y: Buffer<T> get() = NDStructureColumn(this@asXYPointSet, 1)
    }
}