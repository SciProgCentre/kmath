/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [LongArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class LongBuffer(public val array: LongArray) : PrimitiveBuffer<Long> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Long = array[index]

    override operator fun set(index: Int, value: Long) {
        array[index] = value
    }

    override operator fun iterator(): LongIterator = array.iterator()

    override fun copy(): MutableBuffer<Long> =
        LongBuffer(array.copyOf())
}

/**
 * Creates a new [LongBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun LongBuffer(size: Int, init: (Int) -> Long): LongBuffer = LongBuffer(LongArray(size) { init(it) })

/**
 * Returns a new [LongBuffer] of given elements.
 */
public fun LongBuffer(vararg longs: Long): LongBuffer = LongBuffer(longs)

/**
 * Returns a new [LongArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Long>.toLongArray(): LongArray = when (this) {
    is LongBuffer -> array.copyOf()
    else -> LongArray(size, ::get)
}

/**
 * Returns [LongBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun LongArray.asBuffer(): LongBuffer = LongBuffer(this)
