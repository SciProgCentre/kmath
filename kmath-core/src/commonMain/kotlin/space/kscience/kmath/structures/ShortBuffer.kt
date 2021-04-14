package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

/**
 * Specialized [MutableBuffer] implementation over [ShortArray].
 *
 * @property array the underlying array.
 */
@JvmInline
public value class ShortBuffer(public val array: ShortArray) : MutableBuffer<Short> {
    public override val size: Int get() = array.size

    public override operator fun get(index: Int): Short = array[index]

    public override operator fun set(index: Int, value: Short) {
        array[index] = value
    }

    public override operator fun iterator(): ShortIterator = array.iterator()
    public override fun copy(): MutableBuffer<Short> = ShortBuffer(array.copyOf())
}

/**
 * Creates a new [ShortBuffer] with the specified [size], where each element is calculated by calling the specified
 * [init] function.
 *
 * The function [init] is called for each array element sequentially starting from the first one.
 * It should return the value for an buffer element given its index.
 */
public inline fun ShortBuffer(size: Int, init: (Int) -> Short): ShortBuffer = ShortBuffer(ShortArray(size) { init(it) })

/**
 * Returns a new [ShortBuffer] of given elements.
 */
public fun ShortBuffer(vararg shorts: Short): ShortBuffer = ShortBuffer(shorts)

/**
 * Returns a new [ShortArray] containing all of the elements of this [Buffer].
 */
public fun Buffer<Short>.toShortArray(): ShortArray = when (this) {
    is ShortBuffer -> array.copyOf()
    else -> ShortArray(size, ::get)
}

/**
 * Returns [ShortBuffer] over this array.
 *
 * @receiver the array.
 * @return the new buffer.
 */
public fun ShortArray.asBuffer(): ShortBuffer = ShortBuffer(this)
