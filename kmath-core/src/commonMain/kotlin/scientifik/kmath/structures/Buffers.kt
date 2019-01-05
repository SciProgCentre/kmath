package scientifik.kmath.structures


/**
 * A generic random access structure for both primitives and objects
 */
interface Buffer<T> {

    val size: Int

    operator fun get(index: Int): T

    operator fun iterator(): Iterator<T>

    fun contentEquals(other: Buffer<*>): Boolean =
        asSequence().mapIndexed { index, value -> value == other[index] }.all { it }
}

fun <T> Buffer<T>.asSequence(): Sequence<T> = iterator().asSequence()

fun <T> Buffer<T>.asIterable(): Iterable<T> = iterator().asSequence().asIterable()

interface MutableBuffer<T> : Buffer<T> {
    operator fun set(index: Int, value: T)

    /**
     * A shallow copy of the buffer
     */
    fun copy(): MutableBuffer<T>
}


inline class ListBuffer<T>(private val list: List<T>) : Buffer<T> {

    override val size: Int
        get() = list.size

    override fun get(index: Int): T = list[index]

    override fun iterator(): Iterator<T> = list.iterator()
}

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
    //Can't inline because array invariant
    override val size: Int
        get() = array.size

    override fun get(index: Int): T = array[index]

    override fun set(index: Int, value: T) {
        array[index] = value
    }

    override fun iterator(): Iterator<T> = array.iterator()

    override fun copy(): MutableBuffer<T> = ArrayBuffer(array.copyOf())
}

inline class DoubleBuffer(private val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override fun get(index: Int): Double = array[index]

    override fun set(index: Int, value: Double) {
        array[index] = value
    }

    override fun iterator(): Iterator<Double> = array.iterator()

    override fun copy(): MutableBuffer<Double> = DoubleBuffer(array.copyOf())
}

inline class IntBuffer(private val array: IntArray) : MutableBuffer<Int> {
    override val size: Int get() = array.size

    override fun get(index: Int): Int = array[index]

    override fun set(index: Int, value: Int) {
        array[index] = value
    }

    override fun iterator(): Iterator<Int> = array.iterator()

    override fun copy(): MutableBuffer<Int> = IntBuffer(array.copyOf())
}

inline class LongBuffer(private val array: LongArray) : MutableBuffer<Long> {
    override val size: Int get() = array.size

    override fun get(index: Int): Long = array[index]

    override fun set(index: Int, value: Long) {
        array[index] = value
    }

    override fun iterator(): Iterator<Long> = array.iterator()

    override fun copy(): MutableBuffer<Long> = LongBuffer(array.copyOf())
}

inline class ReadOnlyBuffer<T>(private val buffer: MutableBuffer<T>) : Buffer<T> {
    override val size: Int get() = buffer.size

    override fun get(index: Int): T = buffer.get(index)

    override fun iterator(): Iterator<T> = buffer.iterator()
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
 * Create a boxing buffer of given type
 */
inline fun <T> boxingBuffer(size: Int, initializer: (Int) -> T): Buffer<T> = ListBuffer(List(size, initializer))

/**
 * Create most appropriate immutable buffer for given type avoiding boxing wherever possible
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> autoBuffer(size: Int, initializer: (Int) -> T): Buffer<T> {
    return when (T::class) {
        Double::class -> DoubleBuffer(DoubleArray(size) { initializer(it) as Double }) as Buffer<T>
        Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as Buffer<T>
        Long::class -> LongBuffer(LongArray(size) { initializer(it) as Long }) as Buffer<T>
        else -> boxingBuffer(size, initializer)
    }
}

/**
 * Create a boxing mutable buffer of given type
 */
inline fun <T : Any> boxingMutableBuffer(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
    MutableListBuffer(MutableList(size, initializer))

/**
 * Create most appropriate mutable buffer for given type avoiding boxing wherever possible
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> autoMutableBuffer(size: Int, initializer: (Int) -> T): MutableBuffer<T> {
    return when (T::class) {
        Double::class -> DoubleBuffer(DoubleArray(size) { initializer(it) as Double }) as MutableBuffer<T>
        Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as MutableBuffer<T>
        Long::class -> LongBuffer(LongArray(size) { initializer(it) as Long }) as MutableBuffer<T>
        else -> boxingMutableBuffer(size, initializer)
    }
}

typealias BufferFactory<T> = (Int, (Int) -> T) -> Buffer<T>
typealias MutableBufferFactory<T> = (Int, (Int) -> T) -> MutableBuffer<T>

val DoubleBufferFactory: BufferFactory<Double> = { size, initializer -> DoubleBuffer(DoubleArray(size, initializer)) }
val IntBufferFactory: BufferFactory<Int> = { size, initializer -> IntBuffer(IntArray(size, initializer)) }
val LongBufferFactory: BufferFactory<Long> = { size, initializer -> LongBuffer(LongArray(size, initializer)) }

