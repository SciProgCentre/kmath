/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.structures.*

/**
 * Default [BufferedTensor] implementation for [Int] values
 */
public class OffsetIntBuffer(
    private val source: IntBuffer,
    private val offset: Int,
    override val size: Int,
) : MutableBuffer<Int> {

    init {
        require(offset >= 0) { "Offset must be non-negative" }
        require(size >= 0) { "Size must be non-negative" }
        require(offset + size <= source.size) { "Maximum index must be inside source dimension" }
    }

    override fun set(index: Int, value: Int) {
        require(index in 0 until size) { "Index must be in [0, size)" }
        source[index + offset] = value
    }

    override fun get(index: Int): Int = source[index + offset]

    /**
     * Copy only a part of buffer that belongs to this tensor
     */
    override fun copy(): IntBuffer = source.array.copyOfRange(offset, offset + size).asBuffer()

    override fun iterator(): Iterator<Int> = iterator {
        for (i in indices) {
            yield(get(i))
        }
    }

    override fun toString(): String = Buffer.toString(this)

    public fun view(addOffset: Int, newSize: Int = size - addOffset): OffsetIntBuffer =
        OffsetIntBuffer(source, offset + addOffset, newSize)
}

public fun OffsetIntBuffer.slice(range: IntRange): OffsetIntBuffer = view(range.first, range.last - range.first)

/**
 * Map only operable content of the offset buffer
 */
public inline fun OffsetIntBuffer.map(operation: (Int) -> Int): IntBuffer =
    IntBuffer(size) { operation(get(it)) }

public inline fun OffsetIntBuffer.zip(
    other: OffsetIntBuffer,
    operation: (l: Int, r: Int) -> Int,
): IntBuffer {
    require(size == other.size) { "The sizes of zipped buffers must be the same" }
    return IntBuffer(size) { operation(get(it), other[it]) }
}

/**
 * map in place
 */
public inline fun OffsetIntBuffer.mapInPlace(operation: (Int) -> Int) {
    indices.forEach { set(it, operation(get(it))) }
}

/**
 * Default [BufferedTensor] implementation for [Int] values
 */
public class IntTensor(
    shape: ShapeND,
    override val source: OffsetIntBuffer,
) : BufferedTensor<Int>(shape) {

    init {
        require(linearSize == source.size) { "Source buffer size must be equal tensor size" }
    }

    public constructor(shape: ShapeND, buffer: IntBuffer) : this(shape, OffsetIntBuffer(buffer, 0, buffer.size))

    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): Int = this.source[indices.offset(index)]

    @OptIn(PerformancePitfall::class)
    override fun set(index: IntArray, value: Int) {
        source[indices.offset(index)] = value
    }
}
