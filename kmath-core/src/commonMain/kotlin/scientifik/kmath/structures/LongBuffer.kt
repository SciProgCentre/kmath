package scientifik.kmath.structures

/**
 * Specialized [MutableBuffer] implementation over [LongArray].
 *
 * @property array the underlying array.
 */
inline class LongBuffer(val array: LongArray) : MutableBuffer<Long> {
    override val size: Int get() = array.size

    override fun get(index: Int): Long = array[index]

    override fun set(index: Int, value: Long) {
        array[index] = value
    }

    override fun iterator(): LongIterator = array.iterator()

    override fun copy(): MutableBuffer<Long> =
        LongBuffer(array.copyOf())

}

/**
 * Creates a new [LongBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
inline fun LongBuffer(size: Int, init: (Int) -> Long): LongBuffer = LongBuffer(LongArray(size) { init(it) })

/**
 * Returns a new [LongBuffer] of given elements.
 */
fun LongBuffer(vararg longs: Long): LongBuffer = LongBuffer(longs)

/**
 * Returns a [IntArray] containing all of the elements of this [MutableBuffer].
 */
val MutableBuffer<out Long>.array: LongArray
    get() = (if (this is LongBuffer) array else LongArray(size) { get(it) })

/**
 * Returns [LongBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
fun LongArray.asBuffer(): LongBuffer = LongBuffer(this)
