package scientifik.kmath.histogram

import scientifik.kmath.linear.RealVector
import scientifik.kmath.linear.toVector
import scientifik.kmath.operations.Space

/**
 * A simple geometric domain
 * TODO move to geometry module
 */
interface Domain {
    operator fun contains(vector: RealVector): Boolean
    val dimension: Int
}

/**
 * The bin in the histogram. The histogram is by definition always done in the real space
 */
interface Bin : Domain {
    /**
     * The value of this bin
     */
    val value: Number
    val center: RealVector
}

interface Histogram<out B : Bin> : Iterable<B> {

    /**
     * Find existing bin, corresponding to given coordinates
     */
    operator fun get(point: RealVector): B?

    /**
     * Dimension of the histogram
     */
    val dimension: Int

    /**
     * Increment appropriate bin
     */
    fun put(point: RealVector)
}

fun Histogram<*>.put(vararg point: Double) = put(point.toVector())

fun Histogram<*>.fill(sequence: Iterable<RealVector>) = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
fun Histogram<*>.fill(buider: suspend SequenceScope<RealVector>.() -> Unit) = fill(sequence(buider).asIterable())

/**
 * A space to perform arithmetic operations on histograms
 */
interface HistogramSpace<B : Bin, H : Histogram<B>> : Space<H> {
    /**
     * Rules for performing operations on bins
     */
    val binSpace: Space<Bin>
}