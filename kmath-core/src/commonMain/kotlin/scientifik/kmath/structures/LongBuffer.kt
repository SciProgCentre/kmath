package scientifik.kmath.structures

import kotlin.contracts.contract

/**
 * Specialized [MutableBuffer] implementation over [LongArray].
 *
 * @property array the underlying array.
 */
public inline class LongBuffer(public val array: LongArray) : MutableBuffer<Long> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Long = array[index]

    override operator fun set(index: Int, value: Long) {
        array[index] = value
    }

    override operator fun iterator(): LongIterator = array.iterator()

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
public inline fun LongBuffer(size: Int, init: (Int) -> Long): LongBuffer = LongBuffer(LongArray(size) { init(it) })

/**
 * Returns a new [LongBuffer] of given elements.
 */
public fun LongBuffer(vararg longs: Long): LongBuffer = LongBuffer(longs)

/**
 * Returns a [IntArray] containing all of the elements of this [MutableBuffer].
 */
public val MutableBuffer<out Long>.array: LongArray
    get() = (if (this is LongBuffer) array else LongArray(size) { get(it) })

/**
 * Returns [LongBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun LongArray.asBuffer(): LongBuffer = LongBuffer(this)
