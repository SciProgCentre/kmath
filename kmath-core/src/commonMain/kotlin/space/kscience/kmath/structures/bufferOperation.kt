package space.kscience.kmath.structures


public inline fun <T : Any, reified R : Any> Buffer<T>.map(
    bufferFactory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline block: (T) -> R,
): Buffer<R> = bufferFactory(size) { block(get(it)) }