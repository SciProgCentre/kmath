package scientifik.kmath.histogram

import scientifik.kmath.domains.Domain
import scientifik.kmath.linear.Point
import scientifik.kmath.structures.ArrayBuffer
import scientifik.kmath.structures.RealBuffer

/**
 * The bin in the histogram. The histogram is by definition always done in the real space
 */
interface Bin<T : Any> : Domain<T> {
    /**
     * The value of this bin
     */
    val value: Number
    val center: Point<T>
}

interface Histogram<T : Any, out B : Bin<T>> : Iterable<B> {

    /**
     * Find existing bin, corresponding to given coordinates
     */
    operator fun get(point: Point<out T>): B?

    /**
     * Dimension of the histogram
     */
    val dimension: Int

}

interface MutableHistogram<T : Any, out B : Bin<T>> : Histogram<T, B> {

    /**
     * Increment appropriate bin
     */
    fun putWithWeight(point: Point<out T>, weight: Double)

    fun put(point: Point<out T>) = putWithWeight(point, 1.0)
}

fun <T : Any> MutableHistogram<T, *>.put(vararg point: T) = put(ArrayBuffer(point))

fun MutableHistogram<Double, *>.put(vararg point: Number) =
    put(RealBuffer(point.map { it.toDouble() }.toDoubleArray()))

fun MutableHistogram<Double, *>.put(vararg point: Double) = put(RealBuffer(point))

fun <T : Any> MutableHistogram<T, *>.fill(sequence: Iterable<Point<T>>) = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
fun <T : Any> MutableHistogram<T, *>.fill(buider: suspend SequenceScope<Point<T>>.() -> Unit) =
    fill(sequence(buider).asIterable())