package space.kscience.kmath.structures

/**
 * Specialized [MutableBuffer] implementation over [DoubleArray].
 *
 * @property array the underlying array.
 */
@Suppress("OVERRIDE_BY_INLINE")
public inline class RealBuffer(public val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override operator fun get(index: Int): Double = array[index]

    override operator fun set(index: Int, value: Double) {
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
 * Returns a new [DoubleArray] containing all of the elements of this [Buffer].
 */
public fun Buffer<Double>.toDoubleArray(): DoubleArray = when (this) {
    is RealBuffer -> array.copyOf()
    else -> DoubleArray(size, ::get)
}

/**
 * Returns [RealBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun DoubleArray.asBuffer(): RealBuffer = RealBuffer(this)
