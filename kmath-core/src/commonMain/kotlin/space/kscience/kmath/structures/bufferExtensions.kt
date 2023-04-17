/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.structures.*

/**
 * Type alias for buffer transformations.
 */
public fun interface BufferTransform<T, R> {
    public fun transform(arg: Buffer<T>): Buffer<R>
}

///**
// * Type alias for buffer transformations with suspend function.
// */
//public fun interface SuspendBufferTransform<T, R>{
//    public suspend fun transform(arg: Buffer<T>): Buffer<R>
//}


/**
 * Creates a sequence that returns all elements from this [Buffer].
 */
public fun <T> Buffer<T>.asSequence(): Sequence<T> = Sequence(::iterator)

/**
 * Creates an iterable that returns all elements from this [Buffer].
 */
public fun <T> Buffer<T>.asIterable(): Iterable<T> = Iterable(::iterator)

/**
 * Returns a new [List] containing all elements of this buffer.
 */
public fun <T> Buffer<T>.toList(): List<T> = when (this) {
    is ArrayBuffer<T> -> array.toList()
    is ListBuffer<T> -> list.toList()
    is MutableListBuffer<T> -> list.toList()
    else -> asSequence().toList()
}

/**
 * Returns a new [MutableList] filled with all elements of this buffer.
 * **NOTE:** this method uses a protective copy, so it should not be used in performance-critical code.
 */
@UnstableKMathAPI
public fun <T> Buffer<T>.toMutableList(): MutableList<T> = when (this) {
    is ArrayBuffer<T> -> array.toMutableList()
    is ListBuffer<T> -> list.toMutableList()
    is MutableListBuffer<T> -> list.toMutableList()
    else -> MutableList(size, ::get)
}

/**
 * Returns a new [Array] containing all elements of this buffer.
 * **NOTE:** this method uses a protective copy, so it should not be used in performance-critical code.
 */
@UnstableKMathAPI
public inline fun <reified T> Buffer<T>.toTypedArray(): Array<T> = Array(size, ::get)
//
///**
// * Create a new buffer from this one with the given mapping function and using [Buffer.Companion.auto] buffer factory.
// */
//public inline fun <T, reified R : Any> Buffer<T>.map(block: (T) -> R): Buffer<R> =
//    Buffer.auto(size) { block(get(it)) }

/**
 * Create a new buffer from this one with the given mapping function.
 * Provided [bufferFactory] is used to construct the new buffer.
 */
public inline fun <T, R> Buffer<T>.mapToBuffer(
    bufferFactory: BufferFactory<R>,
    crossinline block: (T) -> R,
): Buffer<R> = bufferFactory(size) { block(get(it)) }

/**
 * Create a new buffer from this one with the given mapping (indexed) function.
 * Provided [bufferFactory] is used to construct the new buffer.
 */
public inline fun <T, R> Buffer<T>.mapIndexedToBuffer(
    bufferFactory: BufferFactory<R>,
    crossinline block: (index: Int, value: T) -> R,
): Buffer<R> = bufferFactory(size) { block(it, get(it)) }
//
///**
// * Create a new buffer from this one with the given indexed mapping function.
// * Provided [BufferFactory] is used to construct the new buffer.
// */
//public inline fun <T, reified R : Any> Buffer<T>.mapIndexed(
//    crossinline block: (index: Int, value: T) -> R,
//): Buffer<R> = Buffer.auto(size) { block(it, get(it)) }

/**
 * Fold given buffer according to [operation]
 */
public inline fun <T, R> Buffer<T>.fold(initial: R, operation: (acc: R, T) -> R): R {
    if (size == 0) return initial
    var accumulator = initial
    for (index in this.indices) accumulator = operation(accumulator, get(index))
    return accumulator
}

/**
 * Fold given buffer according to indexed [operation]
 */
public inline fun <T : Any, R> Buffer<T>.foldIndexed(initial: R, operation: (index: Int, acc: R, T) -> R): R {
    if (size == 0) return initial
    var accumulator = initial
    for (index in this.indices) accumulator = operation(index, accumulator, get(index))
    return accumulator
}

/**
 * Reduce a buffer from left to right according to [operation]
 */
public inline fun <T> Buffer<T>.reduce(operation: (left: T, value: T) -> T): T {
    require(size > 0) { "Buffer must have elements" }
    var current = get(0)
    for (i in 1 until size) {
        current = operation(current, get(i))
    }
    return current
}

/**
 * Zip two buffers using given [transform].
 */
@UnstableKMathAPI
public inline fun <T1, T2, R> Buffer<T1>.combineToBuffer(
    other: Buffer<T2>,
    bufferFactory: BufferFactory<R>,
    crossinline transform: (T1, T2) -> R,
): Buffer<R> {
    require(size == other.size) { "Buffer size mismatch in zip: expected $size but found ${other.size}" }
    return bufferFactory(size) { transform(get(it), other[it]) }
}