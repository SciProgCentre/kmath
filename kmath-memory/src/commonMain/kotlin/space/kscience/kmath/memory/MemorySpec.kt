/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

/**
 * A specification to read or write custom objects with fixed size in bytes.
 *
 * @param T the type of object this spec manages.
 */
public interface MemorySpec<T : Any> {
    /**
     * Size of [T] in bytes after serialization.
     */
    public val objectSize: Int

    /**
     * Reads the object starting from [offset].
     */
    public fun MemoryReader.read(offset: Int): T

    // TODO consider thread safety

    /**
     * Writes the object [value] starting from [offset].
     */
    public fun MemoryWriter.write(offset: Int, value: T)
}

/**
 * Reads the object with [spec] starting from [offset].
 */
public fun <T : Any> MemoryReader.read(spec: MemorySpec<T>, offset: Int): T = with(spec) { read(offset) }

/**
 * Writes the object [value] with [spec] starting from [offset].
 */
public fun <T : Any> MemoryWriter.write(spec: MemorySpec<T>, offset: Int, value: T): Unit =
    with(spec) { write(offset, value) }

/**
 * Reads array of [size] objects mapped by [spec] at certain [offset].
 */
public inline fun <reified T : Any> MemoryReader.readArray(spec: MemorySpec<T>, offset: Int, size: Int): Array<T> =
    Array(size) { i -> with(spec) { read(offset + i * objectSize) } }

/**
 * Writes [array] of objects mapped by [spec] at certain [offset].
 */
public fun <T : Any> MemoryWriter.writeArray(spec: MemorySpec<T>, offset: Int, array: Array<T>): Unit =
    with(spec) { array.indices.forEach { i -> write(offset + i * objectSize, array[i]) } }

// TODO It is possible to add elastic MemorySpec with unknown object size
