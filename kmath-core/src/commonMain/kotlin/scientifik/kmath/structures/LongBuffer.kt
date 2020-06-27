package scientifik.kmath.structures

inline class LongBuffer(val array: LongArray) : MutableBuffer<Long> {
    override val size: Int get() = array.size

    override fun get(index: Int): Long = array[index]

    override fun set(index: Int, value: Long) {
        array[index] = value
    }

    override fun iterator() = array.iterator()

    override fun copy(): MutableBuffer<Long> =
        LongBuffer(array.copyOf())

}

fun LongArray.asBuffer() = LongBuffer(this)