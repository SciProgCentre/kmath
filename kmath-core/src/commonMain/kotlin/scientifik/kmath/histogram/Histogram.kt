package scientifik.kmath.histogram

import scientifik.kmath.operations.Space
import scientifik.kmath.structures.ArrayBuffer
import scientifik.kmath.structures.Buffer

typealias Point<T> = Buffer<T>

/**
 * A simple geometric domain
 * TODO move to geometry module
 */
interface Domain<T: Any> {
    operator fun contains(vector: Point<out T>): Boolean
    val dimension: Int
}

/**
 * The bin in the histogram. The histogram is by definition always done in the real space
 */
interface Bin<T: Any> : Domain<T> {
    /**
     * The value of this bin
     */
    val value: Number
    val center: Point<T>
}

interface Histogram<T: Any, out B : Bin<T>> : Iterable<B> {

    /**
     * Find existing bin, corresponding to given coordinates
     */
    operator fun get(point: Point<out T>): B?

    /**
     * Dimension of the histogram
     */
    val dimension: Int

}

interface MutableHistogram<T: Any, out B : Bin<T>>: Histogram<T,B>{

    /**
     * Increment appropriate bin
     */
    fun put(point: Point<out T>, weight: Double = 1.0)
}

fun <T: Any> MutableHistogram<T,*>.put(vararg point: T) = put(ArrayBuffer(point))

fun <T: Any> MutableHistogram<T,*>.fill(sequence: Iterable<Point<T>>) = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
fun <T: Any> MutableHistogram<T, *>.fill(buider: suspend SequenceScope<Point<T>>.() -> Unit) = fill(sequence(buider).asIterable())

/**
 * A space to perform arithmetic operations on histograms
 */
interface HistogramSpace<T: Any, B : Bin<T>, H : Histogram<T,B>> : Space<H> {
    /**
     * Rules for performing operations on bins
     */
    val binSpace: Space<Bin<T>>
}