package scientifik.kmath.structures

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Specialized [MutableBuffer] implementation over [ShortArray].
 *
 * @property array the underlying array.
 */
inline class ShortBuffer(val array: ShortArray) : MutableBuffer<Short> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Short = array[index]

    override operator fun set(index: Int, value: Short) {
        array[index] = value
    }

    override operator fun iterator(): ShortIterator = array.iterator()

    override fun copy(): MutableBuffer<Short> =
        ShortBuffer(array.copyOf())
}

/**
 * Creates a new [ShortBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
inline fun ShortBuffer(size: Int, init: (Int) -> Short): ShortBuffer {
    contract { callsInPlace(init) }
    return ShortBuffer(ShortArray(size) { init(it) })
}

/**
 * Returns a new [ShortBuffer] of given elements.
 */
fun ShortBuffer(vararg shorts: Short): ShortBuffer = ShortBuffer(shorts)

/**
 * Returns a [ShortArray] containing all of the elements of this [MutableBuffer].
 */
val MutableBuffer<out Short>.array: ShortArray
    get() = (if (this is ShortBuffer) array else ShortArray(size) { get(it) })

/**
 * Returns [ShortBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
fun ShortArray.asBuffer(): ShortBuffer = ShortBuffer(this)
