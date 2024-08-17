/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.MutableStructureNDOfDouble
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.core.internal.toPrettyString

public class OffsetDoubleBuffer(
    override val origin: Float64Buffer,
    private val offset: Int,
    override val size: Int,
) : MutableBuffer<Float64>, BufferView<Float64> {

    init {
        require(offset >= 0) { "Offset must be non-negative" }
        require(size >= 0) { "Size must be non-negative" }
        require(offset + size <= origin.size) { "Maximum index must be inside source dimension" }
    }

    override fun set(index: Int, value: Double) {
        require(index in 0 until size) { "Index must be in [0, size)" }
        origin[index + offset] = value
    }

    override fun get(index: Int): Double = origin[index + offset]

    /**
     * Copy only a part of buffer that belongs to this [OffsetDoubleBuffer]
     */
    public fun copy(): Float64Buffer = origin.array.copyOfRange(offset, offset + size).asBuffer()

    override fun iterator(): Iterator<Float64> = iterator {
        for (i in indices) {
            yield(get(i))
        }
    }

    override fun toString(): String = Buffer.toString(this)

    public fun view(addOffset: Int, newSize: Int = size - addOffset): OffsetDoubleBuffer =
        OffsetDoubleBuffer(origin, offset + addOffset, newSize)

    @UnstableKMathAPI
    override fun originIndex(index: Int): Int = if (index in 0 until size) {
        index + offset
    } else {
        -1
    }
}

public fun OffsetDoubleBuffer.slice(range: IntRange): OffsetDoubleBuffer = view(range.first, range.last - range.first)

/**
 * Map only operable content of the offset buffer
 */
public inline fun OffsetDoubleBuffer.map(operation: (Double) -> Double): Float64Buffer =
    Float64Buffer(size) { operation(get(it)) }

public inline fun OffsetDoubleBuffer.zip(
    other: OffsetDoubleBuffer,
    operation: (l: Double, r: Double) -> Double,
): Float64Buffer {
    require(size == other.size) { "The sizes of zipped buffers must be the same" }
    return Float64Buffer(size) { operation(get(it), other[it]) }
}

/**
 * map in place
 */
public inline fun OffsetDoubleBuffer.mapInPlace(operation: (Double) -> Double) {
    indices.forEach { set(it, operation(get(it))) }
}

/**
 * Default [BufferedTensor] implementation for [Double] values.
 *
 * [DoubleTensor] always uses row-based strides
 */
public open class DoubleTensor(
    shape: ShapeND,
    final override val source: OffsetDoubleBuffer,
) : BufferedTensor<Float64>(shape), MutableStructureNDOfDouble {

    init {
        require(linearSize == source.size) { "Source buffer size must be equal tensor size" }
    }

    public constructor(shape: ShapeND, buffer: Float64Buffer) : this(shape, OffsetDoubleBuffer(buffer, 0, buffer.size))


    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): Double = source[indices.offset(index)]

    @OptIn(PerformancePitfall::class)
    override fun set(index: IntArray, value: Double) {
        source[indices.offset(index)] = value
    }

    override fun getDouble(index: IntArray): Double = source[indices.offset(index)]

    override fun setDouble(index: IntArray, value: Double) {
        set(index, value)
    }

    override fun toString(): String = toPrettyString()
}

public fun DoubleTensor.asDoubleBuffer(): OffsetDoubleBuffer = if (shape.size == 1) {
    source
} else {
    error("Only 1D tensors could be cast to 1D")
}
