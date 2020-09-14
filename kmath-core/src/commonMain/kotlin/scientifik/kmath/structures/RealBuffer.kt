package scientifik.kmath.structures

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Specialized [MutableBuffer] implementation over [DoubleArray].
 *
 * @property array the underlying array.
 */
inline class RealBuffer(val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Double = array[index]

    override operator fun set(index: Int, value: Double) {
        array[index] = value
    }

    override operator fun iterator(): DoubleIterator = array.iterator()

    override fun copy(): MutableBuffer<Double> =
        RealBuffer(array.copyOf())
}

/**
 * Creates a new [RealBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
inline fun RealBuffer(size: Int, init: (Int) -> Double): RealBuffer {
    contract { callsInPlace(init) }
    return RealBuffer(DoubleArray(size) { init(it) })
}

/**
 * Returns a new [RealBuffer] of given elements.
 */
fun RealBuffer(vararg doubles: Double): RealBuffer = RealBuffer(doubles)

/**
 * Returns a [DoubleArray] containing all of the elements of this [MutableBuffer].
 */
val MutableBuffer<out Double>.array: DoubleArray
    get() = (if (this is RealBuffer) array else DoubleArray(size) { get(it) })

/**
 * Returns [RealBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
fun DoubleArray.asBuffer(): RealBuffer = RealBuffer(this)
