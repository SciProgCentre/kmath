package scientifik.kmath.structures


/**
 * A generic linear buffer for both primitives and objects
 */
interface Buffer<T> : Iterable<T> {

    val size: Int

    operator fun get(index: Int): T

    /**
     * A shallow copy of the buffer
     */
    fun copy(): Buffer<T>
}

interface MutableBuffer<T> : Buffer<T> {
    operator fun set(index: Int, value: T)

    /**
     * A shallow copy of the buffer
     */
    override fun copy(): MutableBuffer<T>
}

inline class ListBuffer<T>(private val list: MutableList<T>) : MutableBuffer<T> {

    override val size: Int
        get() = list.size

    override fun get(index: Int): T = list[index]

    override fun set(index: Int, value: T) {
        list[index] = value
    }

    override fun iterator(): Iterator<T>  = list.iterator()

    override fun copy(): MutableBuffer<T> = ListBuffer(ArrayList(list))
}

class ArrayBuffer<T>(private val array: Array<T>) : MutableBuffer<T> {
    override val size: Int
        get() = array.size

    override fun get(index: Int): T = array[index]

    override fun set(index: Int, value: T) {
        array[index] = value
    }

    override fun iterator(): Iterator<T>  = array.iterator()

    override fun copy(): MutableBuffer<T> = ArrayBuffer(array.copyOf())
}

class DoubleBuffer(private val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int
        get() = array.size

    override fun get(index: Int): Double = array[index]

    override fun set(index: Int, value: Double) {
        array[index] = value
    }

    override fun iterator(): Iterator<Double>  = array.iterator()

    override fun copy(): MutableBuffer<Double> = DoubleBuffer(array.copyOf())
}

inline fun <reified T : Any> buffer(size: Int, noinline initializer: (Int) -> T): Buffer<T> {
    return ArrayBuffer(Array(size, initializer))
}

inline fun <reified T : Any> mutableBuffer(size: Int, noinline initializer: (Int) -> T): MutableBuffer<T> {
    return ArrayBuffer(Array(size, initializer))
}