/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.operations.BufferTransform
import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [DoubleArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class Float64Buffer(public val array: DoubleArray) : PrimitiveBuffer<Double> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Double = array[index]

    override operator fun set(index: Int, value: Double) {
        array[index] = value
    }

    override operator fun iterator(): DoubleIterator = array.iterator()

    override fun copy(): Float64Buffer = Float64Buffer(array.copyOf())

    override fun toString(): String = Buffer.toString(this)

    public companion object {
        public fun zero(size: Int): Float64Buffer = DoubleArray(size).asBuffer()
    }
}

public typealias DoubleBuffer = Float64Buffer

/**
 * Creates a new [Float64Buffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for a buffer element given its index.
 */
public inline fun Float64Buffer(size: Int, init: (Int) -> Double): Float64Buffer =
    Float64Buffer(DoubleArray(size) { init(it) })

/**
 * Returns a new [Float64Buffer] of given elements.
 */
public fun Float64Buffer(vararg doubles: Double): Float64Buffer = Float64Buffer(doubles)

/**
 * Returns a new [DoubleArray] containing all the elements of this [Buffer].
 */
public fun Buffer<Double>.toDoubleArray(): DoubleArray = when (this) {
    is Float64Buffer -> array
    else -> DoubleArray(size, ::get)
}

/**
 * Represent this buffer as [Float64Buffer]. Does not guarantee that changes in the original buffer are reflected on this buffer.
 */
public fun Buffer<Double>.toFloat64Buffer(): Float64Buffer = when (this) {
    is Float64Buffer -> this
    else -> DoubleArray(size, ::get).asBuffer()
}

/**
 * Returns [Float64Buffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun DoubleArray.asBuffer(): Float64Buffer = Float64Buffer(this)


public fun interface Float64BufferTransform : BufferTransform<Double, Double> {
    public fun transform(arg: Float64Buffer): Float64Buffer

    override fun transform(arg: Buffer<Double>): Float64Buffer = arg.toFloat64Buffer()
}
