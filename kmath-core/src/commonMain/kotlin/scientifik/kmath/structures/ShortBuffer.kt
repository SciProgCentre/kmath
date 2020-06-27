package scientifik.kmath.structures

inline class ShortBuffer(val array: ShortArray) : MutableBuffer<Short> {
    override val size: Int get() = array.size

    override fun get(index: Int): Short = array[index]

    override fun set(index: Int, value: Short) {
        array[index] = value
    }

    override fun iterator() = array.iterator()

    override fun copy(): MutableBuffer<Short> =
        ShortBuffer(array.copyOf())

}


fun ShortArray.asBuffer() = ShortBuffer(this)