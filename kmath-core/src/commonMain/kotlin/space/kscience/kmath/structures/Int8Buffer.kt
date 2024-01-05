/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [ByteArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class Int8Buffer(public val array: ByteArray) : MutableBuffer<Byte> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Byte = array[index]

    override operator fun set(index: Int, value: Byte) {
        array[index] = value
    }

    override operator fun iterator(): ByteIterator = array.iterator()
    override fun copy(): MutableBuffer<Byte> = Int8Buffer(array.copyOf())
}

/**
 * Creates a new [Int8Buffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun Int8Buffer(size: Int, init: (Int) -> Byte): Int8Buffer = Int8Buffer(ByteArray(size) { init(it) })

/**
 * Returns a new [Int8Buffer] of given elements.
 */
public fun Int8Buffer(vararg bytes: Byte): Int8Buffer = Int8Buffer(bytes)

/**
 * Returns a new [ByteArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Byte>.toByteArray(): ByteArray = when (this) {
    is Int8Buffer -> array.copyOf()
    else -> ByteArray(size, ::get)
}

/**
 * Returns [Int8Buffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun ByteArray.asBuffer(): Int8Buffer = Int8Buffer(this)
