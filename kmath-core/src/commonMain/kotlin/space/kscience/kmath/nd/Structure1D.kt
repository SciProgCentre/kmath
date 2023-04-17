/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.asMutableBuffer
import kotlin.jvm.JvmInline

/**
 * A structure that is guaranteed to be one-dimensional
 */
public interface Structure1D<out T> : StructureND<T>, Buffer<T> {
    override val dimension: Int get() = 1

    @PerformancePitfall
    override operator fun get(index: IntArray): T {
        require(index.size == 1) { "Index dimension mismatch. Expected 1 but found ${index.size}" }
        return get(index[0])
    }

    override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map(::get).iterator()

    public companion object
}

/**
 * A mutable structure that is guaranteed to be one-dimensional
 */
public interface MutableStructure1D<T> : Structure1D<T>, MutableStructureND<T>, MutableBuffer<T> {

    @PerformancePitfall
    override operator fun set(index: IntArray, value: T) {
        require(index.size == 1) { "Index dimension mismatch. Expected 1 but found ${index.size}" }
        set(index[0], value)
    }
}

/**
 * A 1D wrapper for nd-structure
 */
@JvmInline
private value class Structure1DWrapper<out T>(val structure: StructureND<T>) : Structure1D<T> {
    override val shape: ShapeND get() = structure.shape
    override val size: Int get() = structure.shape[0]

    @PerformancePitfall
    override operator fun get(index: Int): T = structure[index]

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}

/**
 * A 1D wrapper for a mutable nd-structure
 */
private class MutableStructure1DWrapper<T>(val structure: MutableStructureND<T>) : MutableStructure1D<T> {
    override val shape: ShapeND get() = structure.shape
    override val size: Int get() = structure.shape[0]

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()

    @PerformancePitfall
    override fun get(index: Int): T = structure[index]

    @PerformancePitfall
    override fun set(index: Int, value: T) {
        structure[intArrayOf(index)] = value
    }

    @OptIn(PerformancePitfall::class)
    override fun copy(): MutableBuffer<T> = structure
        .elements()
        .map(Pair<IntArray, T>::second)
        .toMutableList()
        .asMutableBuffer()

    override fun toString(): String = Buffer.toString(this)
}


/**
 * A structure wrapper for buffer
 */
@JvmInline
private value class Buffer1DWrapper<out T>(val buffer: Buffer<T>) : Structure1D<T> {
    override val shape: ShapeND get() = ShapeND(buffer.size)
    override val size: Int get() = buffer.size

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = buffer.asSequence().mapIndexed { index, value ->
        intArrayOf(index) to value
    }

    override operator fun get(index: Int): T = buffer[index]
}

internal class MutableBuffer1DWrapper<T>(val buffer: MutableBuffer<T>) : MutableStructure1D<T> {
    override val shape: ShapeND get() = ShapeND(buffer.size)
    override val size: Int get() = buffer.size

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = buffer.asSequence().mapIndexed { index, value ->
        intArrayOf(index) to value
    }

    override operator fun get(index: Int): T = buffer[index]
    override fun set(index: Int, value: T) {
        buffer[index] = value
    }

    override fun copy(): MutableBuffer<T> = buffer.copy()

    override fun toString(): String = Buffer.toString(this)
}

/**
 * Represent a [StructureND] as [Structure1D]. Throw error in case of dimension mismatch.
 */
public fun <T> StructureND<T>.as1D(): Structure1D<T> = this as? Structure1D<T> ?: if (shape.size == 1) {
    when (this) {
        is BufferND -> Buffer1DWrapper(this.buffer)
        else -> Structure1DWrapper(this)
    }
} else error("Can't create 1d-structure from ${shape.size}d-structure")

public fun <T> MutableStructureND<T>.as1D(): MutableStructure1D<T> =
    this as? MutableStructure1D<T> ?: if (shape.size == 1) {
        MutableStructure1DWrapper(this)
    } else error("Can't create 1d-structure from ${shape.size}d-structure")

/**
 * Represent this buffer as 1D structure
 */
public fun <T> Buffer<T>.asND(): Structure1D<T> = Buffer1DWrapper(this)

/**
 * Expose inner buffer of this [Structure1D] if possible
 */
internal fun <T : Any> Structure1D<T>.asND(): Buffer<T> = when {
    this is Buffer1DWrapper<T> -> buffer
    this is Structure1DWrapper && structure is BufferND<T> -> structure.buffer
    else -> this
}

