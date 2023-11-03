/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [ShortArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class Int16Buffer(public val array: ShortArray) : MutableBuffer<Short> {

    override val type: SafeType<Short> get() = safeTypeOf()
    override val size: Int get() = array.size

    override operator fun get(index: Int): Short = array[index]

    override operator fun set(index: Int, value: Short) {
        array[index] = value
    }

    override operator fun iterator(): ShortIterator = array.iterator()
    override fun copy(): MutableBuffer<Short> = Int16Buffer(array.copyOf())
}

public typealias ShortBuffer = Int16Buffer

/**
 * Creates a new [Int16Buffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun Int16Buffer(size: Int, init: (Int) -> Short): Int16Buffer = Int16Buffer(ShortArray(size) { init(it) })

/**
 * Returns a new [Int16Buffer] of given elements.
 */
public fun Int16Buffer(vararg shorts: Short): Int16Buffer = Int16Buffer(shorts)

/**
 * Returns a new [ShortArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Short>.toShortArray(): ShortArray = when (this) {
    is Int16Buffer -> array.copyOf()
    else -> ShortArray(size, ::get)
}

/**
 * Returns [Int16Buffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun ShortArray.asBuffer(): Int16Buffer = Int16Buffer(this)
