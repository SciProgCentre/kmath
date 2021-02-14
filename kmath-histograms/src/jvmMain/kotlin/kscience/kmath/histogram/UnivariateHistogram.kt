package kscience.kmath.histogram

import kscience.kmath.domains.UnivariateDomain
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.Space
import kscience.kmath.operations.SpaceElement
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asSequence


public val UnivariateDomain.center: Double get() = (range.endInclusive - range.start) / 2

public interface UnivariateBin : Bin<Double>, ClosedFloatingPointRange<Double> {
    public val domain: UnivariateDomain

    /**
     * The value of histogram including weighting
     */
    public override val value: Double

    /**
     * Standard deviation of the bin value. Zero if not applicable
     */
    public val standardDeviation: Double

    public override val dimension: Int get() = 1

    public override fun contains(point: Buffer<Double>): Boolean = point.size == 1 && contains(point[0])

}

@UnstableKMathAPI
public interface UnivariateHistogram : Histogram<Double, UnivariateBin>,
    SpaceElement<UnivariateHistogram, Space<UnivariateHistogram>> {
    public operator fun get(value: Double): UnivariateBin?
    public override operator fun get(point: Buffer<Double>): UnivariateBin? = get(point[0])

    public companion object {
        /**
         * Build and fill a [UnivariateHistogram]. Returns a read-only histogram.
         */
        public fun uniform(
            binSize: Double,
            start: Double = 0.0,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = TreeHistogramSpace.uniform(binSize, start).produce(builder)

        /**
         * Build and fill a histogram with custom borders. Returns a read-only histogram.
         */
        public fun custom(
            borders: DoubleArray,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = TreeHistogramSpace.custom(borders).produce(builder)

    }
}

@UnstableKMathAPI
public interface UnivariateHistogramBuilder : HistogramBuilder<Double> {
    /**
     * Thread safe put operation
     */
    public fun putValue(at: Double, value: Double = 1.0)

    override fun putValue(point: Buffer<Double>, value: Number)
}

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(items: Iterable<Double>): Unit = items.forEach(this::putValue)

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(array: DoubleArray): Unit = array.forEach(this::putValue)

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(buffer: Buffer<Double>): Unit = buffer.asSequence().forEach(this::putValue)