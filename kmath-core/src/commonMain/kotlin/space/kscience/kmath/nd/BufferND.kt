package space.kscience.kmath.nd

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory

/**
 * Represents [StructureND] over [Buffer].
 *
 * @param T the type of items.
 * @param strides The strides to access elements of [Buffer] by linear indices.
 * @param buffer The underlying buffer.
 */
public class BufferND<T>(
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