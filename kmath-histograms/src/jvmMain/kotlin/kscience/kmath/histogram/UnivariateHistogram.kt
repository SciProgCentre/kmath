package kscience.kmath.histogram

import kscience.kmath.linear.Point
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.SpaceElement
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asBuffer
import kscience.kmath.structures.asSequence

public data class UnivariateHistogramBinDefinition(
    val position: Double,
    val size: Double,
) : Comparable<UnivariateHistogramBinDefinition> {
    override fun compareTo(other: UnivariateHistogramBinDefinition): Int = this.position.compareTo(other.position)
}

public interface UnivariateBin : Bin<Double> {
    public val def: UnivariateHistogramBinDefinition

    public val position: Double get() = def.position
    public val size: Double get() = def.size

    /**
     * The value of histogram including weighting
     */
    public override val value: Double

    /**
     * Standard deviation of the bin value. Zero if not applicable
     */
    public val standardDeviation: Double

    public val center: Point<Double> get() = doubleArrayOf(position).asBuffer()

    public override val dimension: Int get() = 1

    public override fun contains(point: Buffer<Double>): Boolean = contains(point[0])
}

public operator fun UnivariateBin.contains(value: Double): Boolean =
    value in (position - size / 2)..(position + size / 2)

@OptIn(UnstableKMathAPI::class)
public interface UnivariateHistogram : Histogram<Double, UnivariateBin>,
    SpaceElement<UnivariateHistogram, UnivariateHistogramSpace> {
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
        ): UnivariateHistogram = UnivariateHistogramSpace.uniform(binSize, start).produce(builder)

        /**
         * Build and fill a histogram with custom borders. Returns a read-only histogram.
         */
        public fun custom(
            borders: DoubleArray,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = UnivariateHistogramSpace.custom(borders).produce(builder)

    }
}

public interface UnivariateHistogramBuilder: HistogramBuilder<Double> {

    /**
     * Thread safe put operation
     */
    public fun put(value: Double, weight: Double = 1.0)

    override fun putValue(point: Buffer<Double>, value: Number)

    /**
     * Put several items into a single bin
     */
    public fun putMany(value: Double, count: Int, weight: Double = count.toDouble())

    public fun build(): UnivariateHistogram
}

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(items: Iterable<Double>): Unit = items.forEach(::put)

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(array: DoubleArray): Unit = array.forEach(::put)

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(buffer: Buffer<Double>): Unit = buffer.asSequence().forEach(::put)