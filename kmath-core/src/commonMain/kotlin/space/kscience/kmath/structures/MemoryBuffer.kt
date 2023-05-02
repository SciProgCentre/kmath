/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.memory.*

/**
 * A non-boxing buffer over [Memory] object.
 *
 * @param T the type of elements contained in the buffer.
 * @property memory the underlying memory segment.
 * @property spec the spec of [T] type.
 */
public open class MemoryBuffer<T : Any>(protected val memory: Memory, protected val spec: MemorySpec<T>) : Buffer<T> {
    override val size: Int get() = memory.size / spec.objectSize

    override operator fun get(index: Int): T = memory.read { read(spec, spec.objectSize * index) }
    override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map { get(it) }.iterator()

    override fun toString(): String = Buffer.toString(this)

    public companion object {
        public fun <T : Any> create(spec: MemorySpec<T>, size: Int): MemoryBuffer<T> =
            MemoryBuffer(Memory.allocate(size * spec.objectSize), spec)

        public inline fun <T : Any> create(
            spec: MemorySpec<T>,
            size: Int,
            initializer: (Int) -> T,
        ): MemoryBuffer<T> = MutableMemoryBuffer(Memory.allocate(size * spec.objectSize), spec).also { buffer ->
            (0 until size).forEach { buffer[it] = initializer(it) }
        }
    }
}

/**
 * A mutable non-boxing buffer over [Memory] object.
 *
 * @param T the type of elements contained in the buffer.
 * @property memory the underlying memory segment.
 * @property spec the spec of [T] type.
 */
public class MutableMemoryBuffer<T : Any>(memory: Memory, spec: MemorySpec<T>) : MemoryBuffer<T>(memory, spec),
    MutableBuffer<T> {

    private val writer: MemoryWriter = memory.writer()

    override operator fun set(index: Int, value: T): Unit = writer.write(spec, spec.objectSize * index, value)
    override fun copy(): MutableBuffer<T> = MutableMemoryBuffer(memory.copy(), spec)

    public companion object {
        public fun <T : Any> create(spec: MemorySpec<T>, size: Int): MutableMemoryBuffer<T> =
            MutableMemoryBuffer(Memory.allocate(size * spec.objectSize), spec)

        public inline fun <T : Any> create(
            spec: MemorySpec<T>,
            size: Int,
            initializer: (Int) -> T,
        ): MutableMemoryBuffer<T> = MutableMemoryBuffer(Memory.allocate(size * spec.objectSize), spec).also { buffer ->
            (0 until size).forEach { buffer[it] = initializer(it) }
        }
    }
}
