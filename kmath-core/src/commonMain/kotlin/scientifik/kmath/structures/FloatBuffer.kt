package scientifik.kmath.structures

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Specialized [MutableBuffer] implementation over [FloatArray].
 *
 * @property array the underlying array.
 */
inline class FloatBuffer(val array: FloatArray) : MutableBuffer<Float> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Float = array[index]

    override operator fun set(index: Int, value: Float) {
        array[index] = value
    }

    override operator fun iterator(): FloatIterator = array.iterator()

    override fun copy(): MutableBuffer<Float> =
        FloatBuffer(array.copyOf())
}

/**
 * Creates a new [FloatBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
inline fun FloatBuffer(size: Int, init: (Int) -> Float): FloatBuffer {
    contract { callsInPlace(init) }
    return FloatBuffer(FloatArray(size) { init(it) })
}

/**
 * Returns a new [FloatBuffer] of given elements.
 */
fun FloatBuffer(vararg floats: Float): FloatBuffer = FloatBuffer(floats)

/**
 * Returns a [FloatArray] containing all of the elements of this [MutableBuffer].
 */
val MutableBuffer<out Float>.array: FloatArray
    get() = (if (this is FloatBuffer) array else FloatArray(size) { get(it) })

/**
 * Returns [FloatBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
fun FloatArray.asBuffer(): FloatBuffer = FloatBuffer(this)
