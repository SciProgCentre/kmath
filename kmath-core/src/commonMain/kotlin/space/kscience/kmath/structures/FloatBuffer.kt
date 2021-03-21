package space.kscience.kmath.structures

/**
 * Specialized [MutableBuffer] implementation over [FloatArray].
 *
 * @property array the underlying array.
 * @author Iaroslav Postovalov
 */
public inline class FloatBuffer(public val array: FloatArray) : MutableBuffer<Float> {
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
public inline fun FloatBuffer(size: Int, init: (Int) -> Float): FloatBuffer = FloatBuffer(FloatArray(size) { init(it) })

/**
 * Returns a new [FloatBuffer] of given elements.
 */
public fun FloatBuffer(vararg floats: Float): FloatBuffer = FloatBuffer(floats)

/**
 * Returns a new [FloatArray] containing all of the elements of this [Buffer].
 */
public fun Buffer<Float>.toFloatArray(): FloatArray = when (this) {
    is FloatBuffer -> array.copyOf()
    else -> FloatArray(size, ::get)
}

/**
 * Returns [FloatBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun FloatArray.asBuffer(): FloatBuffer = FloatBuffer(this)
