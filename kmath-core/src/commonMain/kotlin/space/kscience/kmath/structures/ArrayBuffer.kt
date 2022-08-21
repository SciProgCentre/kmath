/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

/**
 * [MutableBuffer] implementation over [Array].
 *
 * @param T the type of elements contained in the buffer.
 * @property array The underlying array.
 */
public class ArrayBuffer<T>(internal val array: Array<T>) : MutableBuffer<T> {
    // Can't inline because array is invariant
    override val size: Int get() = array.size

    override operator fun get(index: Int): T = array[index]

    override operator fun set(index: Int, value: T) {
        array[index] = value
    }

    override operator fun iterator(): Iterator<T> = array.iterator()
    override fun copy(): MutableBuffer<T> = ArrayBuffer(array.copyOf())

    override fun toString(): String = Buffer.toString(this)
}


/**
 * Returns an [ArrayBuffer] that wraps the original array.
 */
public fun <T> Array<T>.asBuffer(): ArrayBuffer<T> = ArrayBuffer(this)