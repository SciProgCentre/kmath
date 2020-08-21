package scientifik.kmath.structures

import scientifik.memory.*

/**
 * A non-boxing buffer over [Memory] object.
 *
 * @param T the type of elements contained in the buffer.
 * @property memory the underlying memory segment.
 * @property spec the spec of [T] type.
 */
open class MemoryBuffer<T : Any>(protected val memory: Memory, protected val spec: MemorySpec<T>) : Buffer<T> {
    override val size: Int get() = memory.size / spec.objectSize

    private val reader: MemoryReader = memory.reader()

    override operator fun get(index: Int): T = reader.read(spec, spec.objectSize * index)
    override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map { get(it) }.iterator()

    companion object {
        fun <T : Any> create(spec: MemorySpec<T>, size: Int): MemoryBuffer<T> =
            MemoryBuffer(Memory.allocate(size * spec.objectSize), spec)

        inline fun <T : Any> create(
            spec: MemorySpec<T>,
            size: Int,
            crossinline initializer: (Int) -> T
        ): MemoryBuffer<T> =
            MutableMemoryBuffer(Memory.allocate(size * spec.objectSize), spec).also { buffer ->
                (0 until size).forEach {
                    buffer[it] = initializer(it)
                }
            }
    }
}

/**
 * A mutable non-boxing buffer over [Memory] object.
 *
 * @param T the type of elements contained in the buffer.
 * @property memory the underlying memory segment.
 * @property spec the spec of [T] type.
 */
class MutableMemoryBuffer<T : Any>(memory: Memory, spec: MemorySpec<T>) : MemoryBuffer<T>(memory, spec),
    MutableBuffer<T> {

    private val writer: MemoryWriter = memory.writer()

    override operator fun set(index: Int, value: T): Unit = writer.write(spec, spec.objectSize * index, value)
    override fun copy(): MutableBuffer<T> = MutableMemoryBuffer(memory.copy(), spec)

    companion object {
        fun <T : Any> create(spec: MemorySpec<T>, size: Int): MutableMemoryBuffer<T> =
            MutableMemoryBuffer(Memory.allocate(size * spec.objectSize), spec)

        inline fun <T : Any> create(
            spec: MemorySpec<T>,
            size: Int,
            crossinline initializer: (Int) -> T
        ): MutableMemoryBuffer<T> =
            MutableMemoryBuffer(Memory.allocate(size * spec.objectSize), spec).also { buffer ->
                (0 until size).forEach {
                    buffer[it] = initializer(it)
                }
            }
    }
}
