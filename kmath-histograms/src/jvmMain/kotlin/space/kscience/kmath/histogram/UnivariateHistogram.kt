/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.UnivariateDomain
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asSequence


@UnstableKMathAPI
public val UnivariateDomain.center: Double
    get() = (range.endInclusive + range.start) / 2

/**
 * A univariate bin based on a range
 *
 * @property value The value of histogram including weighting
 * @property standardDeviation Standard deviation of the bin value. Zero or negative if not applicable
 */
@UnstableKMathAPI
public class UnivariateBin(
    public val domain: UnivariateDomain,
    override val value: Double,
    public val standardDeviation: Double,
) : Bin<Double>, ClosedFloatingPointRange<Double> by domain.range {

    override val dimension: Int get() = 1

    override fun contains(point: Buffer<Double>): Boolean = point.size == 1 && contains(point[0])
}

@OptIn(UnstableKMathAPI::class)
public interface UnivariateHistogram : Histogram<Double, UnivariateBin>{
    public operator fun get(value: Double): UnivariateBin?
    override operator fun get(point: Buffer<Double>): UnivariateBin? = get(point[0])

    public companion object {
        /**
         * Build and fill a [UnivariateHistogram]. Returns a read-only histogram.
         */
        public fun uniform(
            binSize: Double,
            start: Double = 0.0,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = TreeHistogramSpace.uniform(binSize, start).fill(builder)

        /**
         * Build and fill a histogram with custom borders. Returns a read-only histogram.
         */
        public fun custom(
            borders: DoubleArray,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = TreeHistogramSpace.custom(borders).fill(builder)

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