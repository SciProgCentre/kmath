package kscience.kmath.structures

import kotlin.reflect.KClass

/**
 * Function that produces [Buffer] from its size and function that supplies values.
 *
 * @param T the type of buffer.
 */
public typealias BufferFactory<T> = (Int, (Int) -> T) -> Buffer<T>

/**
 * Function that produces [MutableBuffer] from its size and function that supplies values.
 *
 * @param T the type of buffer.
 */
public typealias MutableBufferFactory<T> = (Int, (Int) -> T) -> MutableBuffer<T>

/**
 * A generic immutable random-access structure for both primitives and objects.
 *
 * @param T the type of elements contained in the buffer.
 */
public interface Buffer<T> {
    /**
     * The size of this buffer.
     */
    public val size: Int

    /**
     * Gets element at given index.
     */
    public operator fun get(index: Int): T

    /**
     * Iterates over all elements.
     */
    public operator fun iterator(): Iterator<T>

    /**
     * Checks content equality with another buffer.
     */
    public fun contentEquals(other: Buffer<*>): Boolean =
        asSequence().mapIndexed { index, value -> value == other[index] }.all { it }

    public companion object {
        /**
         * Creates a [RealBuffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun real(size: Int, initializer: (Int) -> Double): RealBuffer =
            RealBuffer(size) { initializer(it) }

        /**
         * Creates a [ListBuffer] of given type [T] with given [size]. Each element is calculated by calling the
         * specified [initializer] function.
         */
        public inline fun <T> boxing(size: Int, initializer: (Int) -> T): Buffer<T> =
            ListBuffer(List(size, initializer))

        // TODO add resolution based on Annotation or companion resolution

        /**
         * Creates a [Buffer] of given [type]. If the type is primitive, specialized buffers are used ([IntBuffer],
         * [RealBuffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <T : Any> auto(type: KClass<T>, size: Int, initializer: (Int) -> T): Buffer<T> =
            when (type) {
                Double::class -> RealBuffer(size) { initializer(it) as Double } as Buffer<T>
                Short::class -> ShortBuffer(size) { initializer(it) as Short } as Buffer<T>
                Int::class -> IntBuffer(size) { initializer(it) as Int } as Buffer<T>
                Long::class -> LongBuffer(size) { initializer(it) as Long } as Buffer<T>
                Float::class -> FloatBuffer(size) { initializer(it) as Float } as Buffer<T>
                else -> boxing(size, initializer)
            }

        /**
         * Creates a [Buffer] of given type [T]. If the type is primitive, specialized buffers are used ([IntBuffer],
         * [RealBuffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(size: Int, initializer: (Int) -> T): Buffer<T> =
            auto(T::class, size, initializer)
    }
}

/**
 * Creates a sequence that returns all elements from this [Buffer].
 */
public fun <T> Buffer<T>.asSequence(): Sequence<T> = Sequence(::iterator)

/**
 * Creates an iterable that returns all elements from this [Buffer].
 */
public fun <T> Buffer<T>.asIterable(): Iterable<T> = Iterable(::iterator)

/**
 * Converts this [Buffer] to a new [List]
 */
public fun <T> Buffer<T>.toList(): List<T> = asSequence().toList()

/**
 * Returns an [IntRange] of the valid indices for this [Buffer].
 */
public val Buffer<*>.indices: IntRange get() = 0 until size

/**
 * A generic mutable random-access structure for both primitives and objects.
 *
 * @param T the type of elements contained in the buffer.
 */
public interface MutableBuffer<T> : Buffer<T> {
    /**
     * Sets the array element at the specified [index] to the specified [value].
     */
    public operator fun set(index: Int, value: T)

    /**
     * Returns a shallow copy of the buffer.
     */
    public fun copy(): MutableBuffer<T>

    public companion object {
        /**
         * Create a boxing mutable buffer of given type
         */
        public inline fun <T> boxing(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            MutableListBuffer(MutableList(size, initializer))

        /**
         * Creates a [MutableBuffer] of given [type]. If the type is primitive, specialized buffers are used
         * ([IntBuffer], [RealBuffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <T : Any> auto(type: KClass<out T>, size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            when (type) {
                Double::class -> RealBuffer(size) { initializer(it) as Double } as MutableBuffer<T>
                Short::class -> ShortBuffer(size) { initializer(it) as Short } as MutableBuffer<T>
                Int::class -> IntBuffer(size) { initializer(it) as Int } as MutableBuffer<T>
                Float::class -> FloatBuffer(size) { initializer(it) as Float } as MutableBuffer<T>
                Long::class -> LongBuffer(size) { initializer(it) as Long } as MutableBuffer<T>
                else -> boxing(size, initializer)
            }

        /**
         * Creates a [MutableBuffer] of given type [T]. If the type is primitive, specialized buffers are used
         * ([IntBuffer], [RealBuffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            auto(T::class, size, initializer)

        /**
         * Creates a [RealBuffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun real(size: Int, initializer: (Int) -> Double): RealBuffer =
            RealBuffer(size) { initializer(it) }
    }
}

/**
 * [Buffer] implementation over [List].
 *
 * @param T the type of elements contained in the buffer.
 * @property list The underlying list.
 */
public inline class ListBuffer<T>(public val list: List<T>) : Buffer<T> {
    override val size: Int
        get() = list.size

    override operator fun get(index: Int): T = list[index]
    override operator fun iterator(): Iterator<T> = list.iterator()
}

/**
 * Returns an [ListBuffer] that wraps the original list.
 */
public fun <T> List<T>.asBuffer(): ListBuffer<T> = ListBuffer(this)

/**
 * Creates a new [ListBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an array element given its index.
 */
public inline fun <T> ListBuffer(size: Int, init: (Int) -> T): ListBuffer<T> = List(size, init).asBuffer()

/**
 * [MutableBuffer] implementation over [MutableList].
 *
 * @param T the type of elements contained in the buffer.
 * @property list The underlying list.
 */
public inline class MutableListBuffer<T>(public val list: MutableList<T>) : MutableBuffer<T> {
    override val size: Int
        get() = list.size

    override operator fun get(index: Int): T = list[index]

    override operator fun set(index: Int, value: T) {
        list[index] = value
    }

    override operator fun iterator(): Iterator<T> = list.iterator()
    override fun copy(): MutableBuffer<T> = MutableListBuffer(ArrayList(list))
}

/**
 * [MutableBuffer] implementation over [Array].
 *
 * @param T the type of elements contained in the buffer.
 * @property array The underlying array.
 */
public class ArrayBuffer<T>(private val array: Array<T>) : MutableBuffer<T> {
    // Can't inline because array is invariant
    override val size: Int
        get() = array.size

    override operator fun get(index: Int): T = array[index]

    override operator fun set(index: Int, value: T) {
        array[index] = value
    }

    override operator fun iterator(): Iterator<T> = array.iterator()
    override fun copy(): MutableBuffer<T> = ArrayBuffer(array.copyOf())
}

/**
 * Returns an [ArrayBuffer] that wraps the original array.
 */
public fun <T> Array<T>.asBuffer(): ArrayBuffer<T> = ArrayBuffer(this)

/**
 * Immutable wrapper for [MutableBuffer].
 *
 * @param T the type of elements contained in the buffer.
 * @property buffer The underlying buffer.
 */
public inline class ReadOnlyBuffer<T>(public val buffer: MutableBuffer<T>) : Buffer<T> {
    override val size: Int get() = buffer.size

    override operator fun get(index: Int): T = buffer[index]

    override operator fun iterator(): Iterator<T> = buffer.iterator()
}

/**
 * A buffer with content calculated on-demand. The calculated content is not stored, so it is recalculated on each call.
 * Useful when one needs single element from the buffer.
 *
 * @param T the type of elements provided by the buffer.
 */
public class VirtualBuffer<T>(override val size: Int, private val generator: (Int) -> T) : Buffer<T> {
    override operator fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Expected index from 0 to ${size - 1}, but found $index")
        return generator(index)
    }

    override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map(generator).iterator()

    override fun contentEquals(other: Buffer<*>): Boolean {
        return if (other is VirtualBuffer) {
            this.size == other.size && this.generator == other.generator
        } else {
            super.contentEquals(other)
        }
    }
}

/**
 * Convert this buffer to read-only buffer.
 */
public fun <T> Buffer<T>.asReadOnly(): Buffer<T> = if (this is MutableBuffer) ReadOnlyBuffer(this) else this

/**
 * Typealias for buffer transformations.
 */
public typealias BufferTransform<T, R> = (Buffer<T>) -> Buffer<R>

/**
 * Typealias for buffer transformations with suspend function.
 */
public typealias SuspendBufferTransform<T, R> = suspend (Buffer<T>) -> Buffer<R>
