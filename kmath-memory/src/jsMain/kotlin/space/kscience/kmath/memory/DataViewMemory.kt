/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.DataView
import org.khronos.webgl.Int8Array

private class DataViewMemory(val view: DataView) : Memory {
    override val size: Int get() = view.byteLength

    override fun view(offset: Int, length: Int): Memory {
        require(offset >= 0) { "offset shouldn't be negative: $offset" }
        require(length >= 0) { "length shouldn't be negative: $length" }
        require(offset + length <= size) { "Can't view memory outside the parent region." }

        if (offset + length > size)
            throw IndexOutOfBoundsException("offset + length > size: $offset + $length > $size")

        return DataViewMemory(DataView(view.buffer, view.byteOffset + offset, length))
    }

    override fun copy(): Memory = DataViewMemory(DataView(view.buffer.slice(0)))

    private val reader: MemoryReader = object : MemoryReader {
        override val memory: Memory get() = this@DataViewMemory

        override fun readDouble(offset: Int): Double = view.getFloat64(offset, true)

        override fun readFloat(offset: Int): Float = view.getFloat64(offset, true).toFloat()

        override fun readByte(offset: Int): Byte = view.getInt8(offset)

        override fun readShort(offset: Int): Short = view.getInt16(offset, true)

        override fun readInt(offset: Int): Int = view.getInt32(offset, true)

        override fun readLong(offset: Int): Long =
            view.getInt32(offset, true).toLong() shl 32 or view.getInt32(offset + 4, true).toLong()

        override fun release() {
            // does nothing on JS
        }
    }

    override fun reader(): MemoryReader = reader

    private val writer: MemoryWriter = object : MemoryWriter {
        override val memory: Memory get() = this@DataViewMemory

        override fun writeDouble(offset: Int, value: Double) {
            view.setFloat64(offset, value, true)
        }

        override fun writeFloat(offset: Int, value: Float) {
            view.setFloat64(offset, value.toDouble(), true)
        }

        override fun writeByte(offset: Int, value: Byte) {
            view.setInt8(offset, value)
        }

        override fun writeShort(offset: Int, value: Short) {
            view.setUint16(offset, value, true)
        }

        override fun writeInt(offset: Int, value: Int) {
            view.setInt32(offset, value, true)
        }

        override fun writeLong(offset: Int, value: Long) {
            view.setInt32(offset, (value shr 32).toInt(), littleEndian = true)
            view.setInt32(offset + 4, (value and 0xffffffffL).toInt(), littleEndian = true)
        }

        override fun release() {
            // does nothing on JS
        }
    }

    override fun writer(): MemoryWriter = writer
}

/**
 * Allocates memory based on a [DataView].
 */
public actual fun Memory.Companion.allocate(length: Int): Memory {
    val buffer = ArrayBuffer(length)
    return DataViewMemory(DataView(buffer, 0, length))
}

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently from the resulting [Memory].
 */
public actual fun Memory.Companion.wrap(array: ByteArray): Memory {
    val int8Array = array.unsafeCast<Int8Array>()
    return DataViewMemory(DataView(int8Array.buffer, int8Array.byteOffset, int8Array.length))
}
