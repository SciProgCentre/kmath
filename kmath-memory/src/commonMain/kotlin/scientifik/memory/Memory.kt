package scientifik.memory

interface Memory {
    val size: Int

    /**
     * Get a projection of this memory (it reflects the changes in the parent memory block)
     */
    fun view(offset: Int, length: Int): Memory

    /**
     * Create a copy of this memory, which does not know anything about this memory
     */
    fun copy(): Memory

    /**
     * Create and possibly register a new reader
     */
    fun reader(): MemoryReader

    fun writer(): MemoryWriter

    companion object {

    }
}

interface MemoryReader {
    val memory: Memory

    fun readDouble(offset: Int): Double
    fun readFloat(offset: Int): Float
    fun readByte(offset: Int): Byte
    fun readShort(offset: Int): Short
    fun readInt(offset: Int): Int
    fun readLong(offset: Int): Long

    fun release()
}

/**
 * Use the memory for read then release the reader
 */
inline fun Memory.read(block: MemoryReader.() -> Unit) {
    reader().apply(block).apply { release() }
}

interface MemoryWriter {
    val memory: Memory

    fun writeDouble(offset: Int, value: Double)
    fun writeFloat(offset: Int, value: Float)
    fun writeByte(offset: Int, value: Byte)
    fun writeShort(offset: Int, value: Short)
    fun writeInt(offset: Int, value: Int)
    fun writeLong(offset: Int, value: Long)

    fun release()
}

/**
 * Use the memory for write then release the writer
 */
inline fun Memory.write(block: MemoryWriter.() -> Unit) {
    writer().apply(block).apply { release() }
}

/**
 * Allocate the most effective platform-specific memory
 */
expect fun Memory.Companion.allocate(length: Int): Memory
