/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.DoubleDomain1D
import space.kscience.kmath.domains.center
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.Buffer
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

private fun <B : ClosedRange<Double>> TreeMap<Double, B>.getBin(value: Double): B? {
    // check ceiling entry and return it if it is what needed
    val ceil = ceilingEntry(value)?.value
    if (ceil != null && value in ceil) return ceil
    //check floor entry
    val floor = floorEntry(value)?.value
    if (floor != null && value in floor) return floor
    //neither is valid, not found
    return null
}

public data class ValueAndError(val value: Double, val error: Double)

public typealias WeightedBin1D = Bin1D<Double, ValueAndError>

public class TreeHistogram(
    private val binMap: TreeMap<Double, WeightedBin1D>,
) : Histogram1D<Double, ValueAndError> {
    override fun get(value: Double): WeightedBin1D? = binMap.getBin(value)
    override val bins: Collection<WeightedBin1D> get() = binMap.values
}

@PublishedApi
internal class TreeHistogramBuilder(val binFactory: (Double) -> DoubleDomain1D) : Histogram1DBuilder<Double, Double> {

    override val defaultValue: Double get() = 1.0

    internal class BinCounter(val domain: DoubleDomain1D, val counter: Counter<Double> = Counter.ofDouble()) :
        ClosedRange<Double> by domain.range

    private val bins: TreeMap<Double, BinCounter> = TreeMap()

    fun get(value: Double): BinCounter? = bins.getBin(value)

    fun createBin(value: Double): BinCounter {
        val binDefinition: DoubleDomain1D = binFactory(value)
        val newBin = BinCounter(binDefinition)
        synchronized(this) {
            bins[binDefinition.center] = newBin
        }
        return newBin
    }

    /**
     * Thread safe put operation
     */
    override fun putValue(at: Double, value: Double) {
        (get(at) ?: createBin(at)).apply {
            counter.add(value)
        }
    }

    override fun putValue(point: Buffer<Double>, value: Double) {
        require(point.size == 1) { "Only points with single value could be used in univariate histogram" }
        putValue(point[0], value.toDouble())
    }

    fun build(): TreeHistogram {
        val map = bins.mapValuesTo(TreeMap<Double, WeightedBin1D>()) { (_, binCounter) ->
            val count: Double = binCounter.counter.value
            WeightedBin1D(binCounter.domain, ValueAndError(count, sqrt(count)))
        }
        return TreeHistogram(map)
    }
}

/**
 * A space for univariate histograms with variable bin borders based on a tree map
 */
public class TreeHistogramSpace(
    @PublishedApi internal val binFactory: (Double) -> DoubleDomain1D,
) : Group<TreeHistogram>, ScaleOperations<TreeHistogram> {

    public inline fun fill(block: Histogram1DBuilder<Double, Double>.() -> Unit): TreeHistogram =
        TreeHistogramBuilder(binFactory).apply(block).build()

    override fun add(
        left: TreeHistogram,
        right: TreeHistogram,
    ): TreeHistogram {
//        require(a.context == this) { "Histogram $a does not belong to this context" }
//        require(b.context == this) { "Histogram $b does not belong to this context" }
        val bins = TreeMap<Double, WeightedBin1D>().apply {
            (left.bins.map { it.domain } union right.bins.map { it.domain }).forEach { def ->
                put(
                    def.center,
                    WeightedBin1D(
                        def,
                        ValueAndError(
                            (left[def.center]?.binValue?.value ?: 0.0) + (right[def.center]?.binValue?.value ?: 0.0),
                            (left[def.center]?.binValue?.error ?: 0.0) + (right[def.center]?.binValue?.error ?: 0.0)
                        )
                    )
                )
            }
        }
        return TreeHistogram(bins)
    }

    override fun scale(a: TreeHistogram, value: Double): TreeHistogram {
        val bins = TreeMap<Double, WeightedBin1D>().apply {
            a.bins.forEach { bin ->
                put(
                    bin.domain.center,
                    WeightedBin1D(
                        bin.domain,
                        ValueAndError(
                            bin.binValue.value * value,
                            abs(bin.binValue.error * value)
                        )
                    )
                )
            }
        }

        return TreeHistogram(bins)
    }

    override fun TreeHistogram.unaryMinus(): TreeHistogram = this * (-1)

    override val zero: TreeHistogram by lazy { fill { } }

    public companion object {
        /**
         * Build and fill a [TreeHistogram]. Returns a read-only histogram.
         */
        public inline fun uniform(
            binSize: Double,
            start: Double = 0.0,
            builder: Histogram1DBuilder<Double, Double>.() -> Unit,
        ): TreeHistogram = uniform(binSize, start).fill(builder)

        /**
         * Build and fill a histogram with custom borders. Returns a read-only histogram.
         */
        public inline fun custom(
            borders: DoubleArray,
            builder: Histogram1DBuilder<Double, Double>.() -> Unit,
        ): TreeHistogram = custom(borders).fill(builder)


        /**
         * Build and fill a [DoubleHistogram1D]. Returns a read-only histogram.
         */
        public fun uniform(
            binSize: Double,
            start: Double = 0.0,
        ): TreeHistogramSpace = TreeHistogramSpace { value ->
            val center = start + binSize * floor((value - start) / binSize + 0.5)
            DoubleDomain1D((center - binSize / 2)..(center + binSize / 2))
        }

        /**
         * Create a histogram with custom cell borders
         */
        public fun custom(borders: DoubleArray): TreeHistogramSpace {
            val sorted = borders.sortedArray()

            return TreeHistogramSpace { value ->
                when {
                    value < sorted.first() -> DoubleDomain1D(
                        Double.NEGATIVE_INFINITY..sorted.first()
                    )

                    value > sorted.last() -> DoubleDomain1D(
                        sorted.last()..Double.POSITIVE_INFINITY
                    )

                    else -> {
                        val index = sorted.indices.first { value > sorted[it] }
                        val left = sorted[index]
                        val right = sorted[index + 1]
                        DoubleDomain1D(left..right)
                    }
                }
            }
        }
    }
}