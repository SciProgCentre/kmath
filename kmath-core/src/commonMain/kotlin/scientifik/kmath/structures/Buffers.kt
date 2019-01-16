package scientifik.kmath.structures


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

        /**
         * Create a boxing buffer of given type
         */
        inline fun <T> boxing(size: Int, initializer: (Int) -> T): Buffer<T> = ListBuffer(List(size, initializer))

        /**
         * Create most appropriate immutable buffer for given type avoiding boxing wherever possible
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any> auto(size: Int, crossinline initializer: (Int) -> T): Buffer<T> {
            return when (T::class) {
                Double::class -> DoubleBuffer(DoubleArray(size) { initializer(it) as Double }) as Buffer<T>
                Short::class -> ShortBuffer(ShortArray(size) { initializer(it) as Short }) as Buffer<T>
                Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as Buffer<T>
                Long::class -> LongBuffer(LongArray(size) { initializer(it) as Long }) as Buffer<T>
                else -> boxing(size, initializer)
            }
        }

        val DoubleBufferFactory: BufferFactory<Double> =
            { size, initializer -> DoubleBuffer(DoubleArray(size, initializer)) }
        val ShortBufferFactory: BufferFactory<Short> =
            { size, initializer -> ShortBuffer(ShortArray(size, initializer)) }
        val IntBufferFactory: BufferFactory<Int> = { size, initializer -> IntBuffer(IntArray(size, initializer)) }
        val LongBufferFactory: BufferFactory<Long> = { size, initializer -> LongBuffer(LongArray(size, initializer)) }
    }
}

fun <T> Buffer<T>.asSequence(): Sequence<T> = iterator().asSequence()

fun <T> Buffer<T>.asIterable(): Iterable<T> = iterator().asSequence().asIterable()

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
        inline fun <T : Any> boxing(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            MutableListBuffer(MutableList(size, initializer))

        /**
         * Create most appropriate mutable buffer for given type avoiding boxing wherever possible
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any> auto(size: Int, initializer: (Int) -> T): MutableBuffer<T> {
            return when (T::class) {
                Double::class -> DoubleBuffer(DoubleArray(size) { initializer(it) as Double }) as MutableBuffer<T>
                Short::class -> ShortBuffer(ShortArray(size) { initializer(it) as Short }) as MutableBuffer<T>
                Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as MutableBuffer<T>
                Long::class -> LongBuffer(LongArray(size) { initializer(it) as Long }) as MutableBuffer<T>
                else -> boxing(size, initializer)
            }
        }
    }
}


inline class ListBuffer<T>(private val list: List<T>) : Buffer<T> {

    override val size: Int
        get() = list.size

    override fun get(index: Int): T = list[index]

    override fun iterator(): Iterator<T> = list.iterator()
}

fun <T> List<T>.asBuffer() = ListBuffer(this)

inline class MutableListBuffer<T>(private val list: MutableList<T>) : MutableBuffer<T> {

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

fun <T> Array<T>.asBuffer() = ArrayBuffer(this)

inline class DoubleBuffer(private val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override fun get(index: Int): Double = array[index]

    override fun set(index: Int, value: Double) {
        array[index] = value
    }

    override fun iterator(): Iterator<Double> = array.iterator()

    override fun copy(): MutableBuffer<Double> = DoubleBuffer(array.copyOf())

}

fun DoubleArray.asBuffer() = DoubleBuffer(this)

inline class ShortBuffer(private val array: ShortArray) : MutableBuffer<Short> {
    override val size: Int get() = array.size

    override fun get(index: Int): Short = array[index]

    override fun set(index: Int, value: Short) {
        array[index] = value
    }

    override fun iterator(): Iterator<Short> = array.iterator()

    override fun copy(): MutableBuffer<Short> = ShortBuffer(array.copyOf())

}

fun ShortArray.asBuffer() = ShortBuffer(this)

inline class IntBuffer(private val array: IntArray) : MutableBuffer<Int> {
    override val size: Int get() = array.size

    override fun get(index: Int): Int = array[index]

    override fun set(index: Int, value: Int) {
        array[index] = value
    }

    override fun iterator(): Iterator<Int> = array.iterator()

    override fun copy(): MutableBuffer<Int> = IntBuffer(array.copyOf())

}

fun IntArray.asBuffer() = IntBuffer(this)

inline class LongBuffer(private val array: LongArray) : MutableBuffer<Long> {
    override val size: Int get() = array.size

    override fun get(index: Int): Long = array[index]

    override fun set(index: Int, value: Long) {
        array[index] = value
    }

    override fun iterator(): Iterator<Long> = array.iterator()

    override fun copy(): MutableBuffer<Long> = LongBuffer(array.copyOf())

}

fun LongArray.asBuffer() = LongBuffer(this)

inline class ReadOnlyBuffer<T>(private val buffer: MutableBuffer<T>) : Buffer<T> {
    override val size: Int get() = buffer.size

    override fun get(index: Int): T = buffer.get(index)

    override fun iterator(): Iterator<T> = buffer.iterator()
}

/**
 * A buffer with content calculated on-demand. The calculated contect is not stored, so it is recalculated on each call.
 * Useful when one needs single element from the buffer.
 */
class VirtualBuffer<T>(override val size: Int, private val generator: (Int) -> T) : Buffer<T> {
    override fun get(index: Int): T = generator(index)

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
