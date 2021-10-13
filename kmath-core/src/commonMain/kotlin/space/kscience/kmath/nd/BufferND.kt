/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * Represents [StructureND] over [Buffer].
 *
 * @param T the type of items.
 * @param strides The strides to access elements of [Buffer] by linear indices.
 * @param buffer The underlying buffer.
 */
public open class BufferND<out T>(
    public val strides: Strides,
    public val buffer: Buffer<T>,
) : StructureND<T> {

    init {
        if (strides.linearSize != buffer.size) {
            error("Expected buffer side of ${strides.linearSize}, but found ${buffer.size}")
        }
    }

    override operator fun get(index: IntArray): T = buffer[strides.offset(index)]

    override val shape: IntArray get() = strides.shape

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = strides.indices().map {
        it to this[it]
    }

    override fun toString(): String = StructureND.toString(this)
}

/**
 * Transform structure to a new structure using provided [BufferFactory] and optimizing if argument is [BufferND]
 */
public inline fun <T, reified R : Any> StructureND<T>.mapToBuffer(
    factory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline transform: (T) -> R,
): BufferND<R> {
    return if (this is BufferND<T>)
        BufferND(this.strides, factory.invoke(strides.linearSize) { transform(buffer[it]) })
    else {
        val strides = DefaultStrides(shape)
        BufferND(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
    }
}

/**
 * Represents [MutableStructureND] over [MutableBuffer].
 *
 * @param T the type of items.
 * @param strides The strides to access elements of [MutableBuffer] by linear indices.
 * @param mutableBuffer The underlying buffer.
 */
public class MutableBufferND<T>(
    strides: Strides,
    public val mutableBuffer: MutableBuffer<T>,
) : MutableStructureND<T>, BufferND<T>(strides, mutableBuffer) {
    override fun set(index: IntArray, value: T) {
        mutableBuffer[strides.offset(index)] = value
    }
}

/**
 * Transform structure to a new structure using provided [MutableBufferFactory] and optimizing if argument is [MutableBufferND]
 */
public inline fun <T, reified R : Any> MutableStructureND<T>.mapToMutableBuffer(
    factory: MutableBufferFactory<R> = MutableBuffer.Companion::auto,
    crossinline transform: (T) -> R,
): MutableBufferND<R> {
    return if (this is MutableBufferND<T>)
        MutableBufferND(this.strides, factory.invoke(strides.linearSize) { transform(mutableBuffer[it]) })
    else {
        val strides = DefaultStrides(shape)
        MutableBufferND(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
    }
}