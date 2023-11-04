/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.attributes.SafeType
import space.kscience.attributes.WithType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.operations.WithSize
import space.kscience.kmath.operations.asSequence
import kotlin.reflect.typeOf

/**
 * Function that produces [Buffer] from its size and function that supplies values.
 *
 * @param T the type of buffer.
 */
public interface BufferFactory<T> : WithType<T> {

    public operator fun invoke(size: Int, builder: (Int) -> T): Buffer<T>
}

/**
 * Create a [BufferFactory] for given [type], using primitive storage if possible
 */
public fun <T> BufferFactory(type: SafeType<T>): BufferFactory<T> = object : BufferFactory<T> {
    override val type: SafeType<T> = type
    override fun invoke(size: Int, builder: (Int) -> T): Buffer<T> = Buffer(type, size, builder)
}

/**
 * Create [BufferFactory] using the reified type
 */
public inline fun <reified T> BufferFactory(): BufferFactory<T> = BufferFactory(safeTypeOf())

/**
 * Function that produces [MutableBuffer] from its size and function that supplies values.
 *
 * @param T the type of buffer.
 */
public interface MutableBufferFactory<T> : BufferFactory<T> {
    override fun invoke(size: Int, builder: (Int) -> T): MutableBuffer<T>
}

/**
 * Create a [MutableBufferFactory] for given [type], using primitive storage if possible
 */
public fun <T> MutableBufferFactory(type: SafeType<T>): MutableBufferFactory<T> = object : MutableBufferFactory<T> {
    override val type: SafeType<T> = type
    override fun invoke(size: Int, builder: (Int) -> T): MutableBuffer<T> = MutableBuffer(type, size, builder)
}

/**
 * Create [BufferFactory] using the reified type
 */
public inline fun <reified T> MutableBufferFactory(): MutableBufferFactory<T> = MutableBufferFactory(safeTypeOf())

/**
 * A generic read-only random-access structure for both primitives and objects.
 *
 * [Buffer] is in general identity-free. [Buffer.contentEquals] should be used for content equality checks.
 *
 * @param T the type of elements contained in the buffer.
 */
public interface Buffer<out T> : WithSize, WithType<T> {
    /**
     * The size of this buffer.
     */
    override val size: Int

    /**
     * Gets an element at given index.
     */
    public operator fun get(index: Int): T

    /**
     * Iterates over all elements.
     */
    public operator fun iterator(): Iterator<T> = indices.asSequence().map(::get).iterator()

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

    }
}

/**
 * Creates a [Buffer] of given type [T]. If the type is primitive, specialized buffers are used ([Int32Buffer],
 * [Float64Buffer], etc.), [ListBuffer] is returned otherwise.
 *
 * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
 */
@Suppress("UNCHECKED_CAST")
public inline fun <reified T> Buffer(size: Int, initializer: (Int) -> T): Buffer<T> {
    val type = safeTypeOf<T>()
    return when (type.kType) {
        typeOf<Double>() -> MutableBuffer.double(size) { initializer(it) as Double } as Buffer<T>
        typeOf<Short>() -> MutableBuffer.short(size) { initializer(it) as Short } as Buffer<T>
        typeOf<Int>() -> MutableBuffer.int(size) { initializer(it) as Int } as Buffer<T>
        typeOf<Long>() -> MutableBuffer.long(size) { initializer(it) as Long } as Buffer<T>
        typeOf<Float>() -> MutableBuffer.float(size) { initializer(it) as Float } as Buffer<T>
        else -> List(size, initializer).asBuffer(type)
    }
}

/**
 * Creates a [Buffer] of given [type]. If the type is primitive, specialized buffers are used ([Int32Buffer],
 * [Float64Buffer], etc.), [ListBuffer] is returned otherwise.
 *
 * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
 */
@Suppress("UNCHECKED_CAST")
public fun <T> Buffer(
    type: SafeType<T>,
    size: Int,
    initializer: (Int) -> T,
): Buffer<T> = when (type.kType) {
    typeOf<Double>() -> MutableBuffer.double(size) { initializer(it) as Double } as Buffer<T>
    typeOf<Short>() -> MutableBuffer.short(size) { initializer(it) as Short } as Buffer<T>
    typeOf<Int>() -> MutableBuffer.int(size) { initializer(it) as Int } as Buffer<T>
    typeOf<Long>() -> MutableBuffer.long(size) { initializer(it) as Long } as Buffer<T>
    typeOf<Float>() -> MutableBuffer.float(size) { initializer(it) as Float } as Buffer<T>
    else -> List(size, initializer).asBuffer(type)
}

/**
 * Returns an [IntRange] of the valid indices for this [Buffer].
 */
public val <T> Buffer<T>.indices: IntRange get() = 0 until size

public operator fun <T> Buffer<T>.get(index: UInt): T = get(index.toInt())

/**
 * if index is in range of buffer, return the value. Otherwise, return null.
 */
public fun <T> Buffer<T>.getOrNull(index: Int): T? = if (index in indices) get(index) else null

public fun <T> Buffer<T>.first(): T {
    require(size > 0) { "Can't get the first element of empty buffer" }
    return get(0)
}

public fun <T> Buffer<T>.last(): T {
    require(size > 0) { "Can't get the last element of empty buffer" }
    return get(size - 1)
}

/**
 * A buffer with content calculated on-demand. The calculated content is not stored, so it is recalculated on each call.
 * Useful when one needs a single element from the buffer.
 *
 * @param T the type of elements provided by the buffer.
 */
public class VirtualBuffer<out T>(
    override val type: SafeType<T>,
    override val size: Int,
    private val generator: (Int) -> T,
) : Buffer<T> {
    override operator fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Expected index from 0 to ${size - 1}, but found $index")
        return generator(index)
    }

    override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map(generator).iterator()

    override fun toString(): String = Buffer.toString(this)
}

/**
 * Inline builder for [VirtualBuffer]
 */
public inline fun <reified T> VirtualBuffer(
    size: Int,
    noinline generator: (Int) -> T,
): VirtualBuffer<T> = VirtualBuffer(safeTypeOf(), size, generator)
