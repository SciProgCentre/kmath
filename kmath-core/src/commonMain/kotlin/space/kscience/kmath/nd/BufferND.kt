/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * Represents [StructureND] over [Buffer].
 *
 * @param T the type of items.
 * @param indices The strides to access elements of [Buffer] by linear indices.
 * @param buffer The underlying buffer.
 */
public open class BufferND<out T>(
    override val indices: ShapeIndexer,
    public open val buffer: Buffer<T>,
) : StructureND<T> {

    override operator fun get(index: IntArray): T = buffer[indices.offset(index)]

    override val shape: IntArray get() = indices.shape

    override fun toString(): String = StructureND.toString(this)
}

/**
 * Transform structure to a new structure using provided [BufferFactory] and optimizing if argument is [BufferND]
 */
public inline fun <T, R : Any> StructureND<T>.mapToBuffer(
    factory: BufferFactory<R>,
    crossinline transform: (T) -> R,
): BufferND<R> = if (this is BufferND<T>)
    BufferND(this.indices, factory.invoke(indices.linearSize) { transform(buffer[it]) })
else {
    val strides = DefaultStrides(shape)
    BufferND(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
}

/**
 * Transform structure to a new structure using inferred [BufferFactory]
 */
public inline fun <T, reified R : Any> StructureND<T>.mapToBuffer(
    crossinline transform: (T) -> R,
): BufferND<R> = mapToBuffer(Buffer.Companion::auto, transform)

/**
 * Represents [MutableStructureND] over [MutableBuffer].
 *
 * @param T the type of items.
 * @param strides The strides to access elements of [MutableBuffer] by linear indices.
 * @param buffer The underlying buffer.
 */
public class MutableBufferND<T>(
    strides: ShapeIndexer,
    override val buffer: MutableBuffer<T>,
) : MutableStructureND<T>, BufferND<T>(strides, buffer) {
    override fun set(index: IntArray, value: T) {
        buffer[indices.offset(index)] = value
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
        MutableBufferND(this.indices, factory.invoke(indices.linearSize) { transform(buffer[it]) })
    else {
        val strides = DefaultStrides(shape)
        MutableBufferND(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
    }
}