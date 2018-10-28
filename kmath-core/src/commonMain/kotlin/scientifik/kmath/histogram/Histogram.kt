package scientifik.kmath.histogram

import scientifik.kmath.linear.Vector
import scientifik.kmath.linear.toVector
import scientifik.kmath.operations.Space

/**
 * A simple geometric domain
 * TODO move to geometry module
 */
interface Domain<T: Any> {
    operator fun contains(vector: Vector<T>): Boolean
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
    val center: Vector<T>
}

interface Histogram<T: Any, out B : Bin<T>> : Iterable<B> {

    /**
     * Find existing bin, corresponding to given coordinates
     */
    operator fun get(point: Vector<T>): B?

    /**
     * Dimension of the histogram
     */
    val dimension: Int

    /**
     * Increment appropriate bin
     */
    fun put(point: Vector<T>)
}

fun Histogram<Double,*>.put(vararg point: Double) = put(point.toVector())

fun <T: Any> Histogram<T,*>.fill(sequence: Iterable<Vector<T>>) = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
fun <T: Any> Histogram<T, *>.fill(buider: suspend SequenceScope<Vector<T>>.() -> Unit) = fill(sequence(buider).asIterable())

/**
 * A space to perform arithmetic operations on histograms
 */
interface HistogramSpace<T: Any, B : Bin<T>, H : Histogram<T,B>> : Space<H> {
    /**
     * Rules for performing operations on bins
     */
    val binSpace: Space<Bin<T>>
}