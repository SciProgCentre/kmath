/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [FloatArray].
 *
 * @property array the underlying array.
 * @author Iaroslav Postovalov
 */
@JvmInline
public value class Float32Buffer(public val array: FloatArray) : PrimitiveBuffer<Float> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Float = array[index]

    override operator fun set(index: Int, value: Float) {
        array[index] = value
    }

    override operator fun iterator(): FloatIterator = array.iterator()

    override fun copy(): MutableBuffer<Float> =
        Float32Buffer(array.copyOf())
}

public typealias FloatBuffer = Float32Buffer

/**
 * Creates a new [Float32Buffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun Float32Buffer(size: Int, init: (Int) -> Float): Float32Buffer = Float32Buffer(FloatArray(size) { init(it) })

/**
 * Returns a new [Float32Buffer] of given elements.
 */
public fun Float32Buffer(vararg floats: Float): Float32Buffer = Float32Buffer(floats)

/**
 * Returns a new [FloatArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Float>.toFloatArray(): FloatArray = when (this) {
    is Float32Buffer -> array.copyOf()
    else -> FloatArray(size, ::get)
}

/**
 * Returns [Float32Buffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun FloatArray.asBuffer(): Float32Buffer = Float32Buffer(this)
