package scientifik.memory

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


private class ByteBufferMemory(
    val buffer: ByteBuffer,
    val startOffset: Int = 0,
    override val size: Int = buffer.limit()
) : Memory {


    @Suppress("NOTHING_TO_INLINE")
    private inline fun position(o: Int): Int = startOffset + o

    override fun view(offset: Int, length: Int): Memory {
        if (offset + length > size) error("Selecting a Memory view outside of memory range")
        return ByteBufferMemory(buffer, position(offset), length)
    }

    override fun copy(): Memory {
        val copy = ByteBuffer.allocate(buffer.capacity())
        buffer.rewind()
        copy.put(buffer)
        copy.flip()
        return ByteBufferMemory(copy)

    }

    private val reader = object : MemoryReader {
        override val memory: Memory get() = this@ByteBufferMemory

        override fun readDouble(offset: Int) = buffer.getDouble(position(offset))

        override fun readFloat(offset: Int) = buffer.getFloat(position(offset))

        override fun readByte(offset: Int) = buffer.get(position(offset))

        override fun readShort(offset: Int) = buffer.getShort(position(offset))

        override fun readInt(offset: Int) = buffer.getInt(position(offset))

        override fun readLong(offset: Int) = buffer.getLong(position(offset))

        override fun release() {
            //does nothing on JVM
        }
    }

    override fun reader(): MemoryReader = reader

    private val writer = object : MemoryWriter {
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

        override fun release() {
            //does nothing on JVM
        }
    }

    override fun writer(): MemoryWriter = writer
}

/**
 * Allocate the most effective platform-specific memory
 */
actual fun Memory.Companion.allocate(length: Int): Memory {
    val buffer = ByteBuffer.allocate(length)
    return ByteBufferMemory(buffer)
}

actual fun Memory.Companion.wrap(array: ByteArray): Memory {
    val buffer = ByteBuffer.wrap(array)
    return ByteBufferMemory(buffer)
}

fun ByteBuffer.asMemory(startOffset: Int = 0, size: Int = limit()): Memory =
    ByteBufferMemory(this, startOffset, size)

/**
 * Use direct memory-mapped buffer from file to read something and close it afterwards.
 */
fun <R> Path.readAsMemory(position: Long = 0, size: Long = Files.size(this), block: Memory.() -> R): R {
    return FileChannel.open(this, StandardOpenOption.READ).use {
        ByteBufferMemory(it.map(FileChannel.MapMode.READ_ONLY, position, size)).block()
    }
}