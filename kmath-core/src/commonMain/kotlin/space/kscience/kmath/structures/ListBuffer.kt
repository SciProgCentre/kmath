/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf

/**
 * [Buffer] implementation over [List].
 *
 * @param T the type of elements contained in the buffer.
 * @property list The underlying list.
 */
public class ListBuffer<T>(override val type: SafeType<T>, public val list: List<T>) : Buffer<T> {

    override val size: Int get() = list.size

    override operator fun get(index: Int): T = list[index]
    override operator fun iterator(): Iterator<T> = list.iterator()

    override fun toString(): String = Buffer.toString(this)
}


/**
 * Returns an [ListBuffer] that wraps the original list.
 */
public fun <T> List<T>.asBuffer(type: SafeType<T>): ListBuffer<T> = ListBuffer(type, this)

/**
 * Returns an [ListBuffer] that wraps the original list.
 */
public inline fun <reified T> List<T>.asBuffer(): ListBuffer<T> = asBuffer(safeTypeOf())

/**
 * [MutableBuffer] implementation over [MutableList].
 *
 * @param T the type of elements contained in the buffer.
 * @property list The underlying list.
 */
public class MutableListBuffer<T>(override val type: SafeType<T>, public val list: MutableList<T>) : MutableBuffer<T> {

    override val size: Int get() = list.size

    override operator fun get(index: Int): T = list[index]

    override operator fun set(index: Int, value: T) {
        list[index] = value
    }

    override operator fun iterator(): Iterator<T> = list.iterator()
    override fun copy(): MutableBuffer<T> = MutableListBuffer(type, ArrayList(list))

    override fun toString(): String = Buffer.toString(this)
}


/**
 * Returns an [MutableListBuffer] that wraps the original list.
 */
public fun <T> MutableList<T>.asMutableBuffer(type: SafeType<T>): MutableListBuffer<T> = MutableListBuffer(
    type,
    this
)

/**
 * Returns an [MutableListBuffer] that wraps the original list.
 */
public inline fun <reified T> MutableList<T>.asMutableBuffer(): MutableListBuffer<T> = MutableListBuffer(
    safeTypeOf(),
    this
)
