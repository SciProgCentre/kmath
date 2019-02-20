package scientifik.kmath.linear

import scientifik.kmath.structures.MutableBuffer
import scientifik.kmath.structures.MutableBufferFactory
import scientifik.kmath.structures.MutableNDStructure

class Mutable2DStructure<T>(val rowNum: Int, val colNum: Int, val buffer: MutableBuffer<T>) : MutableNDStructure<T> {
    override val shape: IntArray
        get() = intArrayOf(rowNum, colNum)

    operator fun get(i: Int, j: Int): T = buffer[i * colNum + j]

    override fun get(index: IntArray): T = get(index[0], index[1])

    override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        for (i in 0 until rowNum) {
            for (j in 0 until colNum) {
                yield(intArrayOf(i, j) to get(i, j))
            }
        }
    }

    operator fun set(i: Int, j: Int, value: T) {
        buffer[i * colNum + j] = value
    }

    override fun set(index: IntArray, value: T) = set(index[0], index[1], value)

    companion object {
        fun <T> create(
            rowNum: Int,
            colNum: Int,
            bufferFactory: MutableBufferFactory<T>,
            init: (i: Int, j: Int) -> T
        ): Mutable2DStructure<T> {
            val buffer = bufferFactory(rowNum * colNum) { offset -> init(offset / colNum, offset % colNum) }
            return Mutable2DStructure(rowNum, colNum, buffer)
        }
    }
}