/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.operations.asSequence
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

/**
 * Function that produces [Buffer] from its size and function that supplies values.
 *
 * @param T the type of buffer.
 */
public typealias BufferFactory<T> = (Int, (Int) -> T) -> Buffer<T>

/**
 * Function that produces [MutableBuffer] from its size and function that supplies values.
 *
 * @param T the type of buffer.
 */
public typealias MutableBufferFactory<T> = (Int, (Int) -> T) -> MutableBuffer<T>

/**
 * A generic read-only random-access structure for both primitives and objects.
 *
 * [Buffer] is in general identity-free. [Buffer.contentEquals] should be used for content equality checks.
 *
 * @param T the type of elements contained in the buffer.
 */
public interface Buffer<out T> {
    /**
     * The size of this buffer.
     */
    public val size: Int

    /**
     * Gets element at given index.
     */
    public operator fun get(index: Int): T

    /**
     * Iterates over all elements.
     */
    public operator fun iterator(): Iterator<T>

    override fun toString(): String

    public companion object {

        public fun toString(buffer: Buffer<*>): String =
            buffer.asSequence().joinToString(prefix = "[", separator = ", ", postfix = "]")

        /**
         * Check the element-by-element match of content of two buffers.
         */
        public fun <T : Any> contentEquals(first: Buffer<T>, second: Buffer<T>): Boolean {
            if (first.size != second.size) return false
            for (i in first.indices) {
                if (first[i] != second[i]) return false
            }
            return true
        }

        /**
         * Creates a [ListBuffer] of given type [T] with given [size]. Each element is calculated by calling the
         * specified [initializer] function.
         */
        public inline fun <T> boxing(size: Int, initializer: (Int) -> T): Buffer<T> =
            List(size, initializer).asBuffer()

        /**
         * Creates a [Buffer] of given [type]. If the type is primitive, specialized buffers are used ([IntBuffer],
         * [DoubleBuffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <T : Any> auto(type: KClass<T>, size: Int, initializer: (Int) -> T): Buffer<T> =
            when (type) {
                Double::class -> MutableBuffer.double(size) { initializer(it) as Double } as Buffer<T>
                Short::class -> MutableBuffer.short(size) { initializer(it) as Short } as Buffer<T>
                Int::class -> MutableBuffer.int(size) { initializer(it) as Int } as Buffer<T>
                Long::class -> MutableBuffer.long(size) { initializer(it) as Long } as Buffer<T>
                Float::class -> MutableBuffer.float(size) { initializer(it) as Float } as Buffer<T>
                else -> boxing(size, initializer)
            }

        /**
         * Creates a [Buffer] of given type [T]. If the type is primitive, specialized buffers are used ([IntBuffer],
         * [DoubleBuffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(size: Int, initializer: (Int) -> T): Buffer<T> =
            auto(T::class, size, initializer)
    }
}

/**
 * Returns an [IntRange] of the valid indices for this [Buffer].
 */
public val Buffer<*>.indices: IntRange get() = 0 until size

public fun <T> Buffer<T>.first(): T {
    require(size > 0) { "Can't get the first element of empty buffer" }
    return get(0)
}

public fun <T> Buffer<T>.last(): T {
    require(size > 0) { "Can't get the last element of empty buffer" }
    return get(size - 1)
}

/**
 * Immutable wrapper for [MutableBuffer].
 *
 * @param T the type of elements contained in the buffer.
 * @property buffer The underlying buffer.
 */
@JvmInline
public value class ReadOnlyBuffer<T>(public val buffer: MutableBuffer<T>) : Buffer<T> {
    override val size: Int get() = buffer.size

    override operator fun get(index: Int): T = buffer[index]

    override operator fun iterator(): Iterator<T> = buffer.iterator()
}

/**
 * A buffer with content calculated on-demand. The calculated content is not stored, so it is recalculated on each call.
 * Useful when one needs single element from the buffer.
 *
 * @param T the type of elements provided by the buffer.
 */
public class VirtualBuffer<out T>(override val size: Int, private val generator: (Int) -> T) : Buffer<T> {
    override operator fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Expected index from 0 to ${size - 1}, but found $index")
        return generator(index)
    }

    override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map(generator).iterator()

    override fun toString(): String = Buffer.toString(this)
}

/**
 * Convert this buffer to read-only buffer.
 */
public fun <T> Buffer<T>.asReadOnly(): Buffer<T> = if (this is MutableBuffer) ReadOnlyBuffer(this) else this