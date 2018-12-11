package scientifik.kmath.structures

import java.nio.ByteBuffer

class ObjectBuffer<T : Any>(private val buffer: ByteBuffer, private val spec: FixedSizeBufferSpec<T>) : MutableBuffer<T> {
    override val size: Int
        get() = buffer.limit() / spec.unitSize

    override fun get(index: Int): T = with(spec) { buffer.readObject(index) }

    override fun iterator(): Iterator<T> = (0 until size).asSequence().map { get(it) }.iterator()

    override fun set(index: Int, value: T) = with(spec) { buffer.writeObject(index, value) }

    override fun copy(): MutableBuffer<T> {
        val dup = buffer.duplicate()
        val copy = ByteBuffer.allocate(dup.capacity())
        dup.rewind()
        copy.put(dup)
        copy.flip()
        return ObjectBuffer(copy, spec)
    }

    companion object {
        fun <T : Any> create(spec: FixedSizeBufferSpec<T>, size: Int) =
                ObjectBuffer<T>(ByteBuffer.allocate(size * spec.unitSize), spec)
    }
}