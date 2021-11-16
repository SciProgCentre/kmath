/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [DoubleArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class DoubleBuffer(public val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Double = array[index]

    override operator fun set(index: Int, value: Double) {
        array[index] = value
    }

    override operator fun iterator(): DoubleIterator = array.iterator()

    override fun copy(): DoubleBuffer = DoubleBuffer(array.copyOf())

    override fun toString(): String = Buffer.toString(this)

    public companion object{
        public fun zero(size: Int): DoubleBuffer = DoubleArray(size).asBuffer()
    }
}

/**
 * Creates a new [DoubleBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun DoubleBuffer(size: Int, init: (Int) -> Double): DoubleBuffer = DoubleBuffer(DoubleArray(size) { init(it) })

/**
 * Returns a new [DoubleBuffer] of given elements.
 */
public fun DoubleBuffer(vararg doubles: Double): DoubleBuffer = DoubleBuffer(doubles)

/**
 * Simplified [DoubleBuffer] to array comparison
 */
public fun DoubleBuffer.contentEquals(vararg doubles: Double): Boolean = array.contentEquals(doubles)

/**
 * Returns a new [DoubleArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Double>.toDoubleArray(): DoubleArray = when (this) {
    is DoubleBuffer -> array.copyOf()
    else -> DoubleArray(size, ::get)
}

/**
 * Returns [DoubleBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun DoubleArray.asBuffer(): DoubleBuffer = DoubleBuffer(this)
