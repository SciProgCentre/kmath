package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.*
import kotlin.random.Random
import kotlin.math.*

/**
 * Returns a reference to [IntArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Int>.array(): IntArray = when (this) {
    is IntBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to IntArray")
}

/**
 * Returns a reference to [LongArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Long>.array(): LongArray = when (this) {
    is LongBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to LongArray")
}

/**
 * Returns a reference to [FloatArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Float>.array(): FloatArray = when (this) {
    is FloatBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to FloatArray")
}

/**
 * Returns a reference to [DoubleArray] containing all of the elements of this [Buffer].
 */
internal fun Buffer<Double>.array(): DoubleArray = when (this) {
    is DoubleBuffer -> array
    else -> throw RuntimeException("Failed to cast Buffer to DoubleArray")
}

internal inline fun getRandomNormals(n: Int, seed: Long): DoubleArray {
    val u = Random(seed)
    return (0 until n).map { sqrt(-2.0 * ln(u.nextDouble())) * cos(2.0 * PI * u.nextDouble()) }.toDoubleArray()
}

internal inline fun minusIndexFrom(n: Int, i: Int) : Int = if (i >= 0) i else {
    val ii = n + i
    check(ii >= 0) {
        "Out of bound index $i for tensor of dim $n"
    }
    ii
}

internal inline fun <T> BufferedTensor<T>.minusIndex(i: Int): Int =  minusIndexFrom(this.linearStructure.dim, i)