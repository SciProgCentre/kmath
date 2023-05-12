/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@PublishedApi
internal class ByteBufferMemory(
    val buffer: ByteBuffer,
    val startOffset: Int = 0,
    override val size: Int = buffer.limit(),
) : Memory {
    private fun position(offset: Int): Int = startOffset + offset

    override fun view(offset: Int, length: Int): Memory {
        require(offset >= 0) { "offset shouldn't be negative: $offset" }
        require(length >= 0) { "length shouldn't be negative: $length" }
        require(offset + length <= size) { "Can't view memory outside the parent region." }
        return ByteBufferMemory(buffer, position(offset), length)
    }

    override fun copy(): Memory {
        val copy = ByteBuffer.allocate(buffer.capacity())
        buffer.rewind()
        copy.put(buffer)
        copy.flip()
        return ByteBufferMemory(copy)
    }

    private val reader: MemoryReader = object : MemoryReader {
        override val memory: Memory get() = this@ByteBufferMemory

        override fun readDouble(offset: Int) = buffer.getDouble(position(offset))

        override fun readFloat(offset: Int) = buffer.getFloat(position(offset))

        override fun readByte(offset: Int) = buffer.get(position(offset))

        override fun readShort(offset: Int) = buffer.getShort(position(offset))

        override fun readInt(offset: Int) = buffer.getInt(position(offset))

        override fun readLong(offset: Int) = buffer.getLong(position(offset))

        override fun close() {
            // does nothing on JVM
        }
    }

    override fun reader(): MemoryReader = reader

    private val writer: MemoryWriter = object : MemoryWriter {
        override val memory: Memory get() = this@ByteBufferMemory

        override fun writeDouble(offset: Int, value: Double) {
            buffer.putDouble(position(offset), value)
        }

        override fun writeFloat(offset: Int, value: Float) {
            buffer.putFloat(position(offset), value)
        }

        override fun writeByte(offset: Int, value: Byte) {
            buffer.put(position(offset), value)
        }

        override fun writeShort(offset: Int, value: Short) {
            buffer.putShort(position(offset), value)
        }

        override fun writeInt(offset: Int, value: Int) {
            buffer.putInt(position(offset), value)
        }

        override fun writeLong(offset: Int, value: Long) {
            buffer.putLong(position(offset), value)
        }

        override fun close() {
            // does nothing on JVM
        }
    }

    override fun writer(): MemoryWriter = writer
}

/**
 * Allocates memory based on a [ByteBuffer].
 */
public actual fun Memory.Companion.allocate(length: Int): Memory =
    ByteBufferMemory(checkNotNull(ByteBuffer.allocate(length)))

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently of the resulting [Memory].
 */
public actual fun Memory.Companion.wrap(array: ByteArray): Memory =
    ByteBufferMemory(checkNotNull(ByteBuffer.wrap(array)))

/**
 * Wraps this [ByteBuffer] to [Memory] object.
 *
 * @receiver the byte buffer.
 * @param startOffset the start offset.
 * @param size the size of memory to map.
 * @return the [Memory] object.
 */
public fun ByteBuffer.asMemory(startOffset: Int = 0, size: Int = limit()): Memory =
    ByteBufferMemory(this, startOffset, size)

/**
 * Uses direct memory-mapped buffer from file to read something and close it afterward.
 */
@Throws(IOException::class)
public inline fun <R> Path.readAsMemory(position: Long = 0, size: Long = Files.size(this), block: Memory.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    return FileChannel
        .open(this, StandardOpenOption.READ)
        .use { ByteBufferMemory(it.map(FileChannel.MapMode.READ_ONLY, position, size)).block() }
}
