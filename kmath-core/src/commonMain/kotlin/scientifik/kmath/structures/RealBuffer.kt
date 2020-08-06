package scientifik.kmath.structures

inline class RealBuffer(val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override fun get(index: Int): Double = array[index]

    override fun set(index: Int, value: Double) {
        array[index] = value
    }

    override fun iterator(): DoubleIterator = array.iterator()

    override fun copy(): MutableBuffer<Double> =
        RealBuffer(array.copyOf())
}

/**
 * Creates a new array with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an array element given its index.
 */
@Suppress("FunctionName")
inline fun RealBuffer(size: Int, init: (Int) -> Double): RealBuffer = RealBuffer(DoubleArray(size) { init(it) })

/**
 * Returns a new [RealBuffer] of given elements.
 */
@Suppress("FunctionName")
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
