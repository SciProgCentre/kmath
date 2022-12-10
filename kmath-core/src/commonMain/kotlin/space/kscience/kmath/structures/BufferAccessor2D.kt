/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.nd.*

/**
 * A context that allows to operate on a [MutableBuffer] as on 2d array
 */
internal class BufferAccessor2D<T>(
    val rowNum: Int,
    val colNum: Int,
    val factory: MutableBufferFactory<T>,
) {
    operator fun Buffer<T>.get(i: Int, j: Int): T = get(i * colNum + j)

    operator fun MutableBuffer<T>.set(i: Int, j: Int, value: T) {
        set(i * colNum + j, value)
    }

    inline fun create(crossinline init: (i: Int, j: Int) -> T): MutableBuffer<T> =
        factory(rowNum * colNum) { offset -> init(offset / colNum, offset % colNum) }

    fun create(mat: Structure2D<T>): MutableBuffer<T> = create { i, j -> mat[i, j] }

    //TODO optimize wrapper
    fun MutableBuffer<T>.collect(): Structure2D<T> = StructureND.buffered(
        ColumnStrides(ShapeND(rowNum, colNum)),
        factory
    ) { (i, j) ->
        get(i, j)
    }.as2D()

    inner class Row(val buffer: MutableBuffer<T>, val rowIndex: Int) : MutableBuffer<T> {
        override val size: Int get() = colNum

        override operator fun get(index: Int): T = buffer[rowIndex, index]

        override operator fun set(index: Int, value: T) {
            buffer[rowIndex, index] = value
        }

        override fun copy(): MutableBuffer<T> = factory(colNum) { get(it) }
        override operator fun iterator(): Iterator<T> = (0 until colNum).map(::get).iterator()

        override fun toString(): String = Buffer.toString(this)

    }

    /**
     * Get row
     */
    fun MutableBuffer<T>.row(i: Int): Row = Row(this, i)
}
