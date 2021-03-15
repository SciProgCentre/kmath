package space.kscience.kmath.structures

import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * Typealias for buffer transformations.
 */
public typealias BufferTransform<T, R> = (Buffer<T>) -> Buffer<R>

/**
 * Typealias for buffer transformations with suspend function.
 */
public typealias SuspendBufferTransform<T, R> = suspend (Buffer<T>) -> Buffer<R>


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

/**
 * Create a new buffer from this one with the given mapping function.
 * Provided [BufferFactory] is used to construct the new buffer.
 */
public inline fun <T : Any, reified R : Any> Buffer<T>.map(
    bufferFactory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline block: (T) -> R,
): Buffer<R> = bufferFactory(size) { block(get(it)) }

/**
 * Create a new buffer from this one with the given indexed mapping function.
 * Provided [BufferFactory] is used to construct the new buffer.
 */
public inline fun <T : Any, reified R : Any> Buffer<T>.mapIndexed(
    bufferFactory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline block: (index: Int, value: T) -> R,
): Buffer<R> = bufferFactory(size) { block(it, get(it)) }

/**
 * Zip two buffers using given [transform].
 */
@UnstableKMathAPI
public inline fun <T1 : Any, T2 : Any, reified R : Any> Buffer<T1>.zip(
    other: Buffer<T2>,
    bufferFactory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline transform: (T1, T2) -> R,
): Buffer<R> {
    require(size == other.size) { "Buffer size mismatch in zip: expected $size but found ${other.size}" }
    return bufferFactory(size) { transform(get(it), other[it]) }
}