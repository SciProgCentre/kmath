package scientifik.kmath.structures

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Specialized [MutableBuffer] implementation over [IntArray].
 *
 * @property array the underlying array.
 */
inline class IntBuffer(val array: IntArray) : MutableBuffer<Int> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Int = array[index]

    override operator fun set(index: Int, value: Int) {
        array[index] = value
    }

    override operator fun iterator(): IntIterator = array.iterator()

    override fun copy(): MutableBuffer<Int> =
        IntBuffer(array.copyOf())
}

/**
 * Creates a new [IntBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
inline fun IntBuffer(size: Int, init: (Int) -> Int): IntBuffer {
    contract { callsInPlace(init) }
    return IntBuffer(IntArray(size) { init(it) })
}

/**
 * Returns a new [IntBuffer] of given elements.
 */
fun IntBuffer(vararg ints: Int): IntBuffer = IntBuffer(ints)

/**
 * Returns a [IntArray] containing all of the elements of this [MutableBuffer].
 */
val MutableBuffer<out Int>.array: IntArray
    get() = (if (this is IntBuffer) array else IntArray(size) { get(it) })

/**
 * Returns [IntBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
fun IntArray.asBuffer(): IntBuffer = IntBuffer(this)
