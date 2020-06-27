package scientifik.kmath.structures

inline class IntBuffer(val array: IntArray) : MutableBuffer<Int> {
    override val size: Int get() = array.size

    override fun get(index: Int): Int = array[index]

    override fun set(index: Int, value: Int) {
        array[index] = value
    }

    override fun iterator() = array.iterator()

    override fun copy(): MutableBuffer<Int> =
        IntBuffer(array.copyOf())

}


fun IntArray.asBuffer() = IntBuffer(this)