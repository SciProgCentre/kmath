package scientifik.memory

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Represents a display of certain memory structure.
 */
interface Memory {
    /**
     * The length of this memory in bytes.
     */
    val size: Int

    /**
     * Get a projection of this memory (it reflects the changes in the parent memory block).
     */
    fun view(offset: Int, length: Int): Memory

    /**
     * Creates an independent copy of this memory.
     */
    fun copy(): Memory

    /**
     * Gets or creates a reader of this memory.
     */
    fun reader(): MemoryReader

    /**
     * Gets or creates a writer of this memory.
     */
    fun writer(): MemoryWriter

    companion object
}

/**
 * The interface to read primitive types in this memory.
 */
interface MemoryReader {
    /**
     * The underlying memory.
     */
    val memory: Memory

    /**
     * Reads [Double] at certain [offset].
     */
    fun readDouble(offset: Int): Double

    /**
     * Reads [Float] at certain [offset].
     */
    fun readFloat(offset: Int): Float

    /**
     * Reads [Byte] at certain [offset].
     */
    fun readByte(offset: Int): Byte

    /**
     * Reads [Short] at certain [offset].
     */
    fun readShort(offset: Int): Short

    /**
     * Reads [Int] at certain [offset].
     */
    fun readInt(offset: Int): Int

    /**
     * Reads [Long] at certain [offset].
     */
    fun readLong(offset: Int): Long

    /**
     * Disposes this reader if needed.
     */
    fun release()
}

/**
 * Uses the memory for read then releases the reader.
 */
inline fun <R> Memory.read(block: MemoryReader.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    val reader = reader()
    val result = reader.block()
    reader.release()
    return result
}

/**
 * The interface to write primitive types into this memory.
 */
interface MemoryWriter {
    /**
     * The underlying memory.
     */
    val memory: Memory

    /**
     * Writes [Double] at certain [offset].
     */
    fun writeDouble(offset: Int, value: Double)

    /**
     * Writes [Float] at certain [offset].
     */
    fun writeFloat(offset: Int, value: Float)

    /**
     * Writes [Byte] at certain [offset].
     */
    fun writeByte(offset: Int, value: Byte)

    /**
     * Writes [Short] at certain [offset].
     */
    fun writeShort(offset: Int, value: Short)

    /**
     * Writes [Int] at certain [offset].
     */
    fun writeInt(offset: Int, value: Int)

    /**
     * Writes [Long] at certain [offset].
     */
    fun writeLong(offset: Int, value: Long)

    /**
     * Disposes this writer if needed.
     */
    fun release()
}

/**
 * Uses the memory for write then releases the writer.
 */
inline fun Memory.write(block: MemoryWriter.() -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    writer().apply(block).release()
}

/**
 * Allocates the most effective platform-specific memory.
 */
expect fun Memory.Companion.allocate(length: Int): Memory

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently from the resulting [Memory].
 */
expect fun Memory.Companion.wrap(array: ByteArray): Memory
