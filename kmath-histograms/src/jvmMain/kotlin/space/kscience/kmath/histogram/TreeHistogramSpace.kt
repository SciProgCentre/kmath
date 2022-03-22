/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.DoubleDomain1D
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

@UnstableKMathAPI
public class TreeHistogram(
    private val binMap: TreeMap<Double, out Bin1D<Double, Double>>,
) : Histogram1D<Double, Double> {
    override fun get(value: Double): Bin1D<Double, Double>? = binMap.getBin(value)
    override val bins: Collection<Bin1D<Double, Double>> get() = binMap.values
}

@OptIn(UnstableKMathAPI::class)
@PublishedApi
internal class TreeHistogramBuilder(val binFactory: (Double) -> DoubleDomain1D) : Histogram1DBuilder<Double, Double> {

    internal class BinCounter(val domain: DoubleDomain1D, val counter: Counter<Double> = Counter.double()) :
        ClosedRange<Double> by domain.range

    private val bins: TreeMap<Double, BinCounter> = TreeMap()

    fun get(value: Double): BinCounter? = bins.getBin(value)

    fun createBin(value: Double): BinCounter {
        val binDefinition = binFactory(value)
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
        val map = bins.mapValuesTo(TreeMap<Double, Bin1D<Double,Double>>()) { (_, binCounter) ->
            val count = binCounter.counter.value
            Bin1D(binCounter.domain, count, sqrt(count))
        }
        return TreeHistogram(map)
    }
}

/**
 * A space for univariate histograms with variable bin borders based on a tree map
 */
@UnstableKMathAPI
public class TreeHistogramSpace(
    @PublishedApi internal val binFactory: (Double) -> DoubleDomain1D,
) : Group<Histogram1D<Double,Double>>, ScaleOperations<Histogram1D<Double,Double>> {

    public inline fun fill(block: Histogram1DBuilder<Double,Double>.() -> Unit): Histogram1D<Double,Double> =
        TreeHistogramBuilder(binFactory).apply(block).build()

    override fun add(
        left: Histogram1D<Double,Double>,
        right: Histogram1D<Double,Double>,
    ): Histogram1D<Double,Double> {
//        require(a.context == this) { "Histogram $a does not belong to this context" }
//        require(b.context == this) { "Histogram $b does not belong to this context" }
        val bins = TreeMap<Double, Bin1D<Double,Double>>().apply {
            (left.bins.map { it.domain } union right.bins.map { it.domain }).forEach { def ->
                put(
                    def.center,
                    Bin1D(
                        def,
                        value = (left[def.center]?.value ?: 0.0) + (right[def.center]?.value ?: 0.0),
                        standardDeviation = (left[def.center]?.standardDeviation
                            ?: 0.0) + (right[def.center]?.standardDeviation ?: 0.0)
                    )
                )
            }
        }
        return TreeHistogram(bins)
    }

    override fun scale(a: Histogram1D<Double,Double>, value: Double): Histogram1D<Double,Double> {
        val bins = TreeMap<Double, Bin1D<Double,Double>>().apply {
            a.bins.forEach { bin ->
                put(
                    bin.domain.center,
                    Bin1D(
                        bin.domain,
                        value = bin.value * value,
                        standardDeviation = abs(bin.standardDeviation * value)
                    )
                )
            }
        }

        return TreeHistogram(bins)
    }

    override fun Histogram1D<Double,Double>.unaryMinus(): Histogram1D<Double,Double> = this * (-1)

    override val zero: Histogram1D<Double,Double> by lazy { fill { } }

    public companion object {
        /**
         * Build and fill a [DoubleHistogram1D]. Returns a read-only histogram.
         */
        public inline fun uniform(
            binSize: Double,
            start: Double = 0.0,
            builder: Histogram1DBuilder<Double,Double>.() -> Unit,
        ): Histogram1D<Double,Double> = uniform(binSize, start).fill(builder)

        /**
         * Build and fill a histogram with custom borders. Returns a read-only histogram.
         */
        public inline fun custom(
            borders: DoubleArray,
            builder: Histogram1DBuilder<Double,Double>.() -> Unit,
        ): Histogram1D<Double,Double> = custom(borders).fill(builder)


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