package scientifik.kmath.structures

import scientifik.memory.*

/**
 * A non-boxing buffer based on [ByteBuffer] storage
 */
open class ObjectBuffer<T : Any>(protected val memory: Memory, protected val spec: MemorySpec<T>) : Buffer<T> {

    override val size: Int get() = memory.size / spec.objectSize

    private val reader = memory.reader()

    override fun get(index: Int): T = reader.read(spec, spec.objectSize * index)

    override fun iterator(): Iterator<T> = (0 until size).asSequence().map { get(it) }.iterator()


    companion object {
        fun <T : Any> create(spec: MemorySpec<T>, size: Int) =
            ObjectBuffer(Memory.allocate(size * spec.objectSize), spec)

        inline fun <T : Any> create(
            spec: MemorySpec<T>,
            size: Int,
            crossinline initializer: (Int) -> T
        ): ObjectBuffer<T> =
            MutableObjectBuffer(Memory.allocate(size * spec.objectSize), spec).also { buffer ->
                (0 until size).forEach {
                    buffer[it] = initializer(it)
                }
            }
    }
}

class MutableObjectBuffer<T : Any>(memory: Memory, spec: MemorySpec<T>) : ObjectBuffer<T>(memory, spec),
    MutableBuffer<T> {

    private val writer = memory.writer()

    override fun set(index: Int, value: T) = writer.write(spec, spec.objectSize * index, value)

    override fun copy(): MutableBuffer<T> = MutableObjectBuffer(memory.copy(), spec)

    companion object {

    }
}