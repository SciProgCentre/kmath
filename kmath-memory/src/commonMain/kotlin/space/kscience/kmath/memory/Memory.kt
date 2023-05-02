/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Represents a display of certain memory structure.
 */
public interface Memory {
    /**
     * The length of this memory in bytes.
     */
    public val size: Int

    /**
     * Get a projection of this memory (it reflects the changes in the parent memory block).
     */
    public fun view(offset: Int, length: Int): Memory

    /**
     * Creates an independent copy of this memory.
     */
    public fun copy(): Memory

    /**
     * Gets or creates a reader of this memory.
     */
    public fun reader(): MemoryReader

    /**
     * Gets or creates a writer of this memory.
     */
    public fun writer(): MemoryWriter

    public companion object
}

/**
 * The interface to read primitive types in this memory.
 */
public interface MemoryReader: AutoCloseable {
    /**
     * The underlying memory.
     */
    public val memory: Memory

    /**
     * Reads [Double] at certain [offset].
     */
    public fun readDouble(offset: Int): Double

    /**
     * Reads [Float] at certain [offset].
     */
    public fun readFloat(offset: Int): Float

    /**
     * Reads [Byte] at certain [offset].
     */
    public fun readByte(offset: Int): Byte

    /**
     * Reads [Short] at certain [offset].
     */
    public fun readShort(offset: Int): Short

    /**
     * Reads [Int] at certain [offset].
     */
    public fun readInt(offset: Int): Int

    /**
     * Reads [Long] at certain [offset].
     */
    public fun readLong(offset: Int): Long

    /**
     * Disposes this reader if needed.
     */
    override fun close()
}

/**
 * Uses the memory for read then releases the reader.
 */
public inline fun <R> Memory.read(block: MemoryReader.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return reader().use(block)
}

/**
 * The interface to write primitive types into this memory.
 */
public interface MemoryWriter: AutoCloseable {
    /**
     * The underlying memory.
     */
    public val memory: Memory

    /**
     * Writes [Double] at certain [offset].
     */
    public fun writeDouble(offset: Int, value: Double)

    /**
     * Writes [Float] at certain [offset].
     */
    public fun writeFloat(offset: Int, value: Float)

    /**
     * Writes [Byte] at certain [offset].
     */
    public fun writeByte(offset: Int, value: Byte)

    /**
     * Writes [Short] at certain [offset].
     */
    public fun writeShort(offset: Int, value: Short)

    /**
     * Writes [Int] at certain [offset].
     */
    public fun writeInt(offset: Int, value: Int)

    /**
     * Writes [Long] at certain [offset].
     */
    public fun writeLong(offset: Int, value: Long)

    /**
     * Disposes this writer if needed.
     */
    override fun close()
}

/**
 * Uses the memory for write then releases the writer.
 */
public inline fun Memory.write(block: MemoryWriter.() -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    writer().use(block)
}

/**
 * Allocates the most effective platform-specific memory.
 */
public expect fun Memory.Companion.allocate(length: Int): Memory

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently of the resulting [Memory].
 */
public expect fun Memory.Companion.wrap(array: ByteArray): Memory
