/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.core.internal.toPrettyString

public class OffsetDoubleBuffer(
    private val source: DoubleBuffer,
    private val offset: Int,
    override val size: Int,
) : MutableBuffer<Double> {

    init {
        require(offset >= 0) { "Offset must be non-negative" }
        require(size >= 0) { "Size must be non-negative" }
        require(offset + size <= source.size) { "Maximum index must be inside source dimension" }
    }

    override fun set(index: Int, value: Double) {
        require(index in 0 until size) { "Index must be in [0, size)" }
        source[index + offset] = value
    }

    override fun get(index: Int): Double = source[index + offset]

    /**
     * Copy only a part of buffer that belongs to this [OffsetDoubleBuffer]
     */
    override fun copy(): DoubleBuffer = source.array.copyOfRange(offset, offset + size).asBuffer()

    override fun iterator(): Iterator<Double> = iterator {
        for (i in indices) {
            yield(get(i))
        }
    }

    override fun toString(): String = Buffer.toString(this)

    public fun view(addOffset: Int, newSize: Int = size - addOffset): OffsetDoubleBuffer =
        OffsetDoubleBuffer(source, offset + addOffset, newSize)
}

public fun OffsetDoubleBuffer.slice(range: IntRange): OffsetDoubleBuffer = view(range.first, range.last - range.first)

/**
 * Map only operable content of the offset buffer
 */
public inline fun OffsetDoubleBuffer.map(operation: (Double) -> Double): DoubleBuffer =
    DoubleBuffer(size) { operation(get(it)) }

public inline fun OffsetDoubleBuffer.zip(
    other: OffsetDoubleBuffer,
    operation: (l: Double, r: Double) -> Double,
): DoubleBuffer {
    require(size == other.size) { "The sizes of zipped buffers must be the same" }
    return DoubleBuffer(size) { operation(get(it), other[it]) }
}

/**
 * map in place
 */
public inline fun OffsetDoubleBuffer.mapInPlace(operation: (Double) -> Double) {
    indices.forEach { set(it, operation(get(it))) }
}

/**
 * Default [BufferedTensor] implementation for [Double] values
 */
public class DoubleTensor(
    shape: IntArray,
    override val source: OffsetDoubleBuffer,
) : BufferedTensor<Double>(shape) {

    init {
        require(linearSize == source.size) { "Source buffer size must be equal tensor size" }
    }

    public constructor(shape: IntArray, buffer: DoubleBuffer) : this(shape, OffsetDoubleBuffer(buffer, 0, buffer.size))

    override fun get(index: IntArray): Double = this.source[indices.offset(index)]

    override fun set(index: IntArray, value: Double) {
        source[indices.offset(index)] = value
    }


    override fun toString(): String = toPrettyString()
}
