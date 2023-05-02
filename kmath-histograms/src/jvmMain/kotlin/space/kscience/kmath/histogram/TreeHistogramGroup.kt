/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.histogram

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.domains.DoubleDomain1D
import space.kscience.kmath.domains.center
import space.kscience.kmath.misc.sorted
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.first
import space.kscience.kmath.structures.indices
import space.kscience.kmath.structures.last
import java.util.*

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

//public data class ValueAndError(val value: Double, val error: Double)
//
//public typealias WeightedBin1D = Bin1D<Double, ValueAndError>

/**
 * A histogram based on a tree map of values
 */
public class TreeHistogram<V : Any>(
    private val binMap: TreeMap<Double, Bin1D<Double, V>>,
) : Histogram1D<Double, V> {
    override fun get(value: Double): Bin1D<Double, V>? = binMap.getBin(value)
    override val bins: Collection<Bin1D<Double, V>> get() = binMap.values
}

/**
 * A space for univariate histograms with variable bin borders based on a tree map
 */
public class TreeHistogramGroup<V : Any, A>(
    public val valueAlgebra: A,
    @PublishedApi internal val binFactory: (Double) -> DoubleDomain1D,
) : Group<TreeHistogram<V>>, ScaleOperations<TreeHistogram<V>> where A : Ring<V>, A : ScaleOperations<V> {

    internal inner class DomainCounter(val domain: DoubleDomain1D, val counter: Counter<V> = Counter.of(valueAlgebra)) :
        ClosedRange<Double> by domain.range

    @PublishedApi
    internal inner class TreeHistogramBuilder : Histogram1DBuilder<Double, V> {

        override val defaultValue: V get() = valueAlgebra.one

        private val bins: TreeMap<Double, DomainCounter> = TreeMap()

        private fun createBin(value: Double): DomainCounter {
            val binDefinition: DoubleDomain1D = binFactory(value)
            val newBin = DomainCounter(binDefinition)
            synchronized(this) {
                bins[binDefinition.center] = newBin
            }
            return newBin
        }

        /**
         * Thread safe put operation
         */
        override fun putValue(at: Double, value: V) {
            (bins.getBin(at) ?: createBin(at)).counter.add(value)
        }

        fun build(): TreeHistogram<V> {
            val map = bins.mapValuesTo(TreeMap<Double, Bin1D<Double, V>>()) { (_, binCounter) ->
                Bin1D(binCounter.domain, binCounter.counter.value)
            }
            return TreeHistogram(map)
        }
    }

    public inline fun produce(block: Histogram1DBuilder<Double, V>.() -> Unit): TreeHistogram<V> =
        TreeHistogramBuilder().apply(block).build()

    override fun add(
        left: TreeHistogram<V>,
        right: TreeHistogram<V>,
    ): TreeHistogram<V> {
        val bins = TreeMap<Double, Bin1D<Double, V>>().apply {
            (left.bins.map { it.domain } union right.bins.map { it.domain }).forEach { def ->
                put(
                    def.center,
                    Bin1D(
                        def,
                        with(valueAlgebra) {
                            (left[def.center]?.binValue ?: zero) + (right[def.center]?.binValue ?: zero)
                        }
                    )
                )
            }
        }
        return TreeHistogram(bins)
    }

    override fun scale(a: TreeHistogram<V>, value: Double): TreeHistogram<V> {
        val bins = TreeMap<Double, Bin1D<Double, V>>().apply {
            a.bins.forEach { bin ->
                put(
                    bin.domain.center,
                    Bin1D(bin.domain, valueAlgebra.scale(bin.binValue, value))
                )
            }
        }

        return TreeHistogram(bins)
    }

    override fun TreeHistogram<V>.unaryMinus(): TreeHistogram<V> = this * (-1)

    override val zero: TreeHistogram<V> = produce { }
}


///**
// * Build and fill a histogram with custom borders. Returns a read-only histogram.
// */
//public inline fun Histogram.custom(
//    borders: DoubleArray,
//    builder: Histogram1DBuilder<Double, Double>.() -> Unit,
//): TreeHistogram = custom(borders).fill(builder)
//
//
///**
// * Build and fill a [DoubleHistogram1D]. Returns a read-only histogram.
// */
//public fun uniform(
//    binSize: Double,
//    start: Double = 0.0,
//): TreeHistogramSpace = TreeHistogramSpace { value ->
//    val center = start + binSize * floor((value - start) / binSize + 0.5)
//    DoubleDomain1D((center - binSize / 2)..(center + binSize / 2))
//}

/**
 * Create a histogram group with custom cell borders
 */
public fun <V : Any, A> Histogram.Companion.custom1D(
    valueAlgebra: A,
    borders: Buffer<Double>,
): TreeHistogramGroup<V, A> where A : Ring<V>, A : ScaleOperations<V> {
    val sorted = borders.sorted()

    return TreeHistogramGroup(valueAlgebra) { value ->
        when {
            value <= sorted.first() -> DoubleDomain1D(
                Double.NEGATIVE_INFINITY..sorted.first()
            )

            value > sorted.last() -> DoubleDomain1D(
                sorted.last()..Double.POSITIVE_INFINITY
            )

            else -> {
                val index = sorted.indices.first { value <= sorted[it] }
                val left = sorted[index - 1]
                val right = sorted[index]
                DoubleDomain1D(left..right)
            }
        }
    }
}