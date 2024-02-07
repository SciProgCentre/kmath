/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [LongArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class Int64Buffer(public val array: LongArray) : PrimitiveBuffer<Long> {

    override val type: SafeType<Long> get() = safeTypeOf()

    override val size: Int get() = array.size

    override operator fun get(index: Int): Long = array[index]

    override operator fun set(index: Int, value: Long) {
        array[index] = value
    }

    override operator fun iterator(): LongIterator = array.iterator()

    override fun copy(): MutableBuffer<Long> =
        Int64Buffer(array.copyOf())
}

public typealias LongBuffer = Int64Buffer

/**
 * Creates a new [Int64Buffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun Int64Buffer(size: Int, init: (Int) -> Long): Int64Buffer = Int64Buffer(LongArray(size) { init(it) })

/**
 * Returns a new [Int64Buffer] of given elements.
 */
public fun Int64Buffer(vararg longs: Long): Int64Buffer = Int64Buffer(longs)

/**
 * Returns a new [LongArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Long>.toLongArray(): LongArray = when (this) {
    is Int64Buffer -> array.copyOf()
    else -> LongArray(size, ::get)
}

/**
 * Returns [Int64Buffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun LongArray.asBuffer(): Int64Buffer = Int64Buffer(this)
