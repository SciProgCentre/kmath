package space.kscience.kmath.real

import space.kscience.kmath.nd.NDBuffer
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.structures.RealBuffer

/**
 * Map one [NDBuffer] using function without indices.
 */
public inline fun NDBuffer<Double>.mapInline(crossinline transform: RealField.(Double) -> Double): NDBuffer<Double> {
    val array = DoubleArray(strides.linearSize) { offset -> RealField.transform(buffer[offset]) }
    return NDBuffer(strides, RealBuffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy.
 */
public operator fun Function1<Double, Double>.invoke(ndElement: NDBuffer<Double>): NDBuffer<Double> =
    ndElement.mapInline { this@invoke(it) }

/* plus and minus */

/**
 * Summation operation for [NDBuffer] and single element
 */
public operator fun NDBuffer<Double>.plus(arg: Double): NDBuffer<Double> = mapInline { it + arg }

/**
 * Subtraction operation between [NDBuffer] and single element
 */
public operator fun NDBuffer<Double>.minus(arg: Double): NDBuffer<Double> = mapInline { it - arg }