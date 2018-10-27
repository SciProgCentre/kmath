package scientifik.kmath.histogram

import scientifik.kmath.linear.RealVector
import scientifik.kmath.operations.Space

/**
 * The bin in the histogram. The histogram is by definition always done in the real space
 */
interface Bin {
    /**
     * The value of this bin
     */
    val value: Number
    val center: RealVector
}

/**
 * Creates a new bin with zero count corresponding to given point
 */
interface BinFactory<out B : Bin> {
    fun createBin(point: RealVector): B
}

interface Histogram<out B : Bin> : Iterable<B> {

    /**
     * Find existing bin, corresponding to given coordinates
     */
    fun findBin(point: RealVector): B?

    /**
     * Dimension of the histogram
     */
    val dimension: Int
}

interface HistogramSpace<B : Bin, H : Histogram<B>> : Space<H> {
    /**
     * Rules for performing operations on bins
     */
    val binSpace: Space<Bin>
}