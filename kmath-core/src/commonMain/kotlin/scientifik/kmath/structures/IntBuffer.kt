package scientifik.kmath.structures

/**
 * Specialized [MutableBuffer] implementation over [IntArray].
 *
 * @property array the underlying array.
 */
inline class IntBuffer(val array: IntArray) : MutableBuffer<Int> {
    override val size: Int get() = array.size

    override fun get(index: Int): Int = array[index]

    override fun set(index: Int, value: Int) {
        array[index] = value
    }

    override fun iterator(): IntIterator = array.iterator()

    override fun copy(): MutableBuffer<Int> =
        IntBuffer(array.copyOf())

}

/**
 * Returns [IntBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
fun IntArray.asBuffer(): IntBuffer = IntBuffer(this)
