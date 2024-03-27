/*
 * Copyright 2018-2024 KMath contributors.
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
public value class Int32Buffer(public val array: IntArray) : PrimitiveBuffer<Int> {


    override val size: Int get() = array.size

    override operator fun get(index: Int): Int = array[index]

    override operator fun set(index: Int, value: Int) {
        array[index] = value
    }

    override operator fun iterator(): IntIterator = array.iterator()

}

public typealias IntBuffer = Int32Buffer

/**
 * Creates a new [Int32Buffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun Int32Buffer(size: Int, init: (Int) -> Int): Int32Buffer = Int32Buffer(IntArray(size) { init(it) })

/**
 * Returns a new [Int32Buffer] of given elements.
 */
public fun Int32Buffer(vararg ints: Int): Int32Buffer = Int32Buffer(ints)

/**
 * Returns a new [IntArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Int>.toIntArray(): IntArray = when (this) {
    is Int32Buffer -> array.copyOf()
    else -> IntArray(size, ::get)
}

/**
 * Returns [Int32Buffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun IntArray.asBuffer(): Int32Buffer = Int32Buffer(this)
