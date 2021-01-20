package kscience.kmath.structures

/**
 * Specialized [MutableBuffer] implementation over [DoubleArray].
 *
 * @property array the underlying array.
 */
@Suppress("OVERRIDE_BY_INLINE")
public inline class RealBuffer(public val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override inline operator fun get(index: Int): Double = array[index]

    override inline operator fun set(index: Int, value: Double) {
        array[index] = value
    }

    override operator fun iterator(): DoubleIterator = array.iterator()

    override fun copy(): RealBuffer = RealBuffer(array.copyOf())
}

/**
 * Creates a new [RealBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
public inline fun RealBuffer(size: Int, init: (Int) -> Double): RealBuffer = RealBuffer(DoubleArray(size) { init(it) })

/**
 * Returns a new [RealBuffer] of given elements.
 */
public fun RealBuffer(vararg doubles: Double): RealBuffer = RealBuffer(doubles)

/**
 * Simplified [RealBuffer] to array comparison
 */
public fun RealBuffer.contentEquals(vararg doubles: Double): Boolean = array.contentEquals(doubles)

/**
 * Returns a [DoubleArray] containing all of the elements of this [MutableBuffer].
 */
public val MutableBuffer<out Double>.array: DoubleArray
    get() = (if (this is RealBuffer) array else DoubleArray(size) { get(it) })

/**
 * Returns [RealBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun DoubleArray.asBuffer(): RealBuffer = RealBuffer(this)
