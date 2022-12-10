/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [IntArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class IntBuffer(public val array: IntArray) : PrimitiveBuffer<Int> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Int = array[index]

    override operator fun set(index: Int, value: Int) {
        array[index] = value
    }

    override operator fun iterator(): IntIterator = array.iterator()

    override fun copy(): IntBuffer = IntBuffer(array.copyOf())
}

/**
 * Creates a new [IntBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun IntBuffer(size: Int, init: (Int) -> Int): IntBuffer = IntBuffer(IntArray(size) { init(it) })

/**
 * Returns a new [IntBuffer] of given elements.
 */
public fun IntBuffer(vararg ints: Int): IntBuffer = IntBuffer(ints)

/**
 * Returns a new [IntArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Int>.toIntArray(): IntArray = when (this) {
    is IntBuffer -> array.copyOf()
    else -> IntArray(size, ::get)
}

/**
 * Returns [IntBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun IntArray.asBuffer(): IntBuffer = IntBuffer(this)
