package scientifik.kmath.structures

import scientifik.kmath.operations.Field

class BufferNDField<T : Any, F : Field<T>>(override val shape: IntArray, override val field: F, val bufferFactory: BufferFactory<T>) : NDField<T, F> {
    val strides = DefaultStrides(shape)

    override inline fun produce(crossinline initializer: F.(IntArray) -> T): NDElement<T, F> {
        return BufferNDElement(this, bufferFactory(strides.linearSize) { offset -> field.initializer(strides.index(offset)) })
    }
}

class BufferNDElement<T : Any, F : Field<T>>(override val context: BufferNDField<T, F>, private val buffer: Buffer<T>) : NDElement<T, F> {

    override val self: NDStructure<T> get() = this
    override val shape: IntArray get() = context.shape

    override fun get(index: IntArray): T = buffer[context.strides.offset(index)]

    override fun elements(): Sequence<Pair<IntArray, T>> = context.strides.indices().map { it to get(it) }

}