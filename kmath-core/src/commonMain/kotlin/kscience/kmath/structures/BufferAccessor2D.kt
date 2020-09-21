package kscience.kmath.structures

import kotlin.reflect.KClass

/**
 * A context that allows to operate on a [MutableBuffer] as on 2d array
 */
public class BufferAccessor2D<T : Any>(public val type: KClass<T>, public val rowNum: Int, public val colNum: Int) {
    public operator fun Buffer<T>.get(i: Int, j: Int): T = get(i + colNum * j)

    public operator fun MutableBuffer<T>.set(i: Int, j: Int, value: T) {
        set(i + colNum * j, value)
    }

    public inline fun create(init: (i: Int, j: Int) -> T): MutableBuffer<T> =
        MutableBuffer.auto(type, rowNum * colNum) { offset -> init(offset / colNum, offset % colNum) }

    public fun create(mat: Structure2D<T>): MutableBuffer<T> = create { i, j -> mat[i, j] }

    //TODO optimize wrapper
    public fun MutableBuffer<T>.collect(): Structure2D<T> =
        NDStructure.auto(type, rowNum, colNum) { (i, j) -> get(i, j) }.as2D()

    public inner class Row(public val buffer: MutableBuffer<T>, public val rowIndex: Int) : MutableBuffer<T> {
        override val size: Int get() = colNum

        override operator fun get(index: Int): T = buffer[rowIndex, index]

        override operator fun set(index: Int, value: T) {
            buffer[rowIndex, index] = value
        }

        override fun copy(): MutableBuffer<T> = MutableBuffer.auto(type, colNum) { get(it) }
        override operator fun iterator(): Iterator<T> = (0 until colNum).map(::get).iterator()

    }

    /**
     * Get row
     */
    public fun MutableBuffer<T>.row(i: Int): Row = Row(this, i)
}
