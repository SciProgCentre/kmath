/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

@PublishedApi
internal class NativeMemory(
    val array: ByteArray,
    val startOffset: Int = 0,
    override val size: Int = array.size,
) : Memory {
    @Suppress("NOTHING_TO_INLINE")
    private inline fun position(o: Int): Int = startOffset + o

    override fun view(offset: Int, length: Int): Memory {
        require(offset >= 0) { "offset shouldn't be negative: $offset" }
        require(length >= 0) { "length shouldn't be negative: $length" }
        require(offset + length <= size) { "Can't view memory outside the parent region." }
        return NativeMemory(array, position(offset), length)
    }

    override fun copy(): Memory {
        val copy = array.copyOfRange(startOffset, startOffset + size)
        return NativeMemory(copy)
    }

    private val reader: MemoryReader = object : MemoryReader {
        override val memory: Memory get() = this@NativeMemory

        override fun readDouble(offset: Int) = array.getDoubleAt(position(offset))

        override fun readFloat(offset: Int) = array.getFloatAt(position(offset))

        override fun readByte(offset: Int) = array[position(offset)]

        override fun readShort(offset: Int) = array.getShortAt(position(offset))

        override fun readInt(offset: Int) = array.getIntAt(position(offset))

        override fun readLong(offset: Int) = array.getLongAt(position(offset))

        override fun close() {
            // does nothing on JVM
        }
    }

    override fun reader(): MemoryReader = reader

    private val writer: MemoryWriter = object : MemoryWriter {
        override val memory: Memory get() = this@NativeMemory

        override fun writeDouble(offset: Int, value: Double) {
            array.setDoubleAt(position(offset), value)
        }

        override fun writeFloat(offset: Int, value: Float) {
            array.setFloatAt(position(offset), value)
        }

        override fun writeByte(offset: Int, value: Byte) {
            array[position(offset)] = value
        }

        override fun writeShort(offset: Int, value: Short) {
            array.setShortAt(position(offset), value)
        }

        override fun writeInt(offset: Int, value: Int) {
            array.setIntAt(position(offset), value)
        }

        override fun writeLong(offset: Int, value: Long) {
            array.setLongAt(position(offset), value)
        }

        override fun close() {
            // does nothing on JVM
        }
    }

    override fun writer(): MemoryWriter = writer
}

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently of the resulting [Memory].
 */
public actual fun Memory.Companion.wrap(array: ByteArray): Memory = NativeMemory(array)

/**
 * Allocates the most effective platform-specific memory.
 */
public actual fun Memory.Companion.allocate(length: Int): Memory {
    val array = ByteArray(length)
    return NativeMemory(array)
}