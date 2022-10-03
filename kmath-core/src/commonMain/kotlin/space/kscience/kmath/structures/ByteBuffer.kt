/*
 * Copyright 2018-2022 KMath contributors.
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
public value class ByteBuffer(public val array: ByteArray) : MutableBuffer<Byte> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Byte = array[index]

    override operator fun set(index: Int, value: Byte) {
        array[index] = value
    }

    override operator fun iterator(): ByteIterator = array.iterator()
    override fun copy(): MutableBuffer<Byte> = ByteBuffer(array.copyOf())
}

/**
 * Creates a new [ByteBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun ByteBuffer(size: Int, init: (Int) -> Byte): ByteBuffer = ByteBuffer(ByteArray(size) { init(it) })

/**
 * Returns a new [ByteBuffer] of given elements.
 */
public fun ByteBuffer(vararg bytes: Byte): ByteBuffer = ByteBuffer(bytes)

/**
 * Returns a new [ByteArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Byte>.toByteArray(): ByteArray = when (this) {
    is ByteBuffer -> array.copyOf()
    else -> ByteArray(size, ::get)
}

/**
 * Returns [ByteBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun ByteArray.asBuffer(): ByteBuffer = ByteBuffer(this)
