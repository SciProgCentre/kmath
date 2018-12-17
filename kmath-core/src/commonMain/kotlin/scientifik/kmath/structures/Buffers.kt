package scientifik.kmath.structures


/**
 * A generic random access structure for both primitives and objects
 */
interface Buffer<T> {

    val size: Int

    operator fun get(index: Int): T

    operator fun iterator(): Iterator<T>
}

fun <T> Buffer<T>.asSequence(): Sequence<T> = iterator().asSequence()

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
 * Create most appropriate immutable buffer for given type avoiding boxing wherever possible
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> buffer(size: Int, noinline initializer: (Int) -> T): Buffer<T> {
    return when (T::class) {
        Double::class -> DoubleBuffer(DoubleArray(size) { initializer(it) as Double }) as Buffer<T>
        Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as Buffer<T>
        else -> ArrayBuffer(Array(size, initializer))
    }
}

/**
 * Create most appropriate mutable buffer for given type avoiding boxing wherever possible
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> mutableBuffer(size: Int, noinline initializer: (Int) -> T): MutableBuffer<T> {
    return when (T::class) {
        Double::class -> DoubleBuffer(DoubleArray(size) { initializer(it) as Double }) as MutableBuffer<T>
        Int::class -> IntBuffer(IntArray(size) { initializer(it) as Int }) as MutableBuffer<T>
        else -> ArrayBuffer(Array(size, initializer))
    }
}