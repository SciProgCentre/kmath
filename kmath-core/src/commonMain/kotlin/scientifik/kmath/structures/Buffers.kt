package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.complex
import kotlin.reflect.KClass


typealias BufferFactory<T> = (Int, (Int) -> T) -> Buffer<T>
typealias MutableBufferFactory<T> = (Int, (Int) -> T) -> MutableBuffer<T>


/**
 * A generic random access structure for both primitives and objects
 */
interface Buffer<T> {

    /**
     * The size of the buffer
     */
    val size: Int

    /**
     * Get element at given index
     */
    operator fun get(index: Int): T

    /**
     * Iterate over all elements
     */
    operator fun iterator(): Iterator<T>

    /**
     * Check content eqiality with another buffer
     */
    fun contentEquals(other: Buffer<*>): Boolean =
        asSequence().mapIndexed { index, value -> value == other[index] }.all { it }

    companion object {

        inline fun real(size: Int, initializer: (Int) -> Double): RealBuffer {
            val array = DoubleArray(size) { initializer(it) }
            return RealBuffer(array)
        }

        /**
         * Create a boxing buffer of given type
         */
        inline fun <T> boxing(size: Int, initializer: (Int) -> T): Buffer<T> = ListBuffer(List(size, initializer))

        @Suppress("UNCHECKED_CAST")
        inline fun <T : Any> auto(type: KClass<T>, size: Int, crossinline initializer: (Int) -> T): Buffer<T> {
            //TODO add resolution based on Annotation or companion resolution
            return when (type) {
                Double::class -> RealBuffer(DoubleArray(size) { initializer(it) as Double }) as Buffer<T>
                Short::class -> ShortBuffer(ShortArray(size) { initializer(it) as Short }) as Buffer<T>
                Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as Buffer<T>
                Long::class -> LongBuffer(LongArray(size) { initializer(it) as Long }) as Buffer<T>
                Complex::class -> complex(size) { initializer(it) as Complex } as Buffer<T>
                else -> boxing(size, initializer)
            }
        }

        /**
         * Create most appropriate immutable buffer for given type avoiding boxing wherever possible
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any> auto(size: Int, crossinline initializer: (Int) -> T): Buffer<T> =
            auto(T::class, size, initializer)
    }
}

fun <T> Buffer<T>.asSequence(): Sequence<T> = Sequence(::iterator)

fun <T> Buffer<T>.asIterable(): Iterable<T> = Iterable(::iterator)

val Buffer<*>.indices: IntRange get() = IntRange(0, size - 1)

interface MutableBuffer<T> : Buffer<T> {
    operator fun set(index: Int, value: T)

    /**
     * A shallow copy of the buffer
     */
    fun copy(): MutableBuffer<T>

    companion object {
        /**
         * Create a boxing mutable buffer of given type
         */
        inline fun <T> boxing(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            MutableListBuffer(MutableList(size, initializer))

        @Suppress("UNCHECKED_CAST")
        inline fun <T : Any> auto(type: KClass<out T>, size: Int, initializer: (Int) -> T): MutableBuffer<T> {
            return when (type) {
                Double::class -> RealBuffer(DoubleArray(size) { initializer(it) as Double }) as MutableBuffer<T>
                Short::class -> ShortBuffer(ShortArray(size) { initializer(it) as Short }) as MutableBuffer<T>
                Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as MutableBuffer<T>
                Long::class -> LongBuffer(LongArray(size) { initializer(it) as Long }) as MutableBuffer<T>
                else -> boxing(size, initializer)
            }
        }

        /**
         * Create most appropriate mutable buffer for given type avoiding boxing wherever possible
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any> auto(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            auto(T::class, size, initializer)

        val real: MutableBufferFactory<Double> = { size: Int, initializer: (Int) -> Double ->
            RealBuffer(DoubleArray(size) { initializer(it) })
        }
    }
}

inline class ListBuffer<T>(val list: List<T>) : Buffer<T> {

    override val size: Int
        get() = list.size

    override fun get(index: Int): T = list[index]

    override fun iterator(): Iterator<T> = list.iterator()
}

fun <T> List<T>.asBuffer() = ListBuffer<T>(this)

@Suppress("FunctionName")
inline fun <T> ListBuffer(size: Int, init: (Int) -> T) = List(size, init).asBuffer()

inline class MutableListBuffer<T>(val list: MutableList<T>) : MutableBuffer<T> {

    override val size: Int
        get() = list.size

    override fun get(index: Int): T = list[index]

    override fun set(index: Int, value: T) {
        list[index] = value
    }

    override fun iterator(): Iterator<T> = list.iterator()
    override fun copy(): MutableBuffer<T> = MutableListBuffer(ArrayList(list))
}

class ArrayBuffer<T>(private val array: Array<T>) : MutableBuffer<T> {
    //Can't inline because array is invariant
    override val size: Int
        get() = array.size

    override fun get(index: Int): T = array[index]

    override fun set(index: Int, value: T) {
        array[index] = value
    }

    override fun iterator(): Iterator<T> = array.iterator()

    override fun copy(): MutableBuffer<T> = ArrayBuffer(array.copyOf())
}

fun <T> Array<T>.asBuffer(): ArrayBuffer<T> = ArrayBuffer(this)

inline class ReadOnlyBuffer<T>(val buffer: MutableBuffer<T>) : Buffer<T> {
    override val size: Int get() = buffer.size

    override fun get(index: Int): T = buffer.get(index)

    override fun iterator() = buffer.iterator()
}

/**
 * A buffer with content calculated on-demand. The calculated contect is not stored, so it is recalculated on each call.
 * Useful when one needs single element from the buffer.
 */
class VirtualBuffer<T>(override val size: Int, private val generator: (Int) -> T) : Buffer<T> {
    override fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Expected index from 0 to ${size - 1}, but found $index")
        return generator(index)
    }

    override fun iterator(): Iterator<T> = (0 until size).asSequence().map(generator).iterator()

    override fun contentEquals(other: Buffer<*>): Boolean {
        return if (other is VirtualBuffer) {
            this.size == other.size && this.generator == other.generator
        } else {
            super.contentEquals(other)
        }
    }
}

/**
 * Convert this buffer to read-only buffer
 */
fun <T> Buffer<T>.asReadOnly(): Buffer<T> = if (this is MutableBuffer) {
    ReadOnlyBuffer(this)
} else {
    this
}

/**
 * Typealias for buffer transformations
 */
typealias BufferTransform<T, R> = (Buffer<T>) -> Buffer<R>

typealias SuspendBufferTransform<T, R> = suspend (Buffer<T>) -> Buffer<R>