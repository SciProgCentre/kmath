/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.HyperSquareDomain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.*
import kotlin.math.floor

/**
 * Multivariate histogram space for hyper-square real-field bins.
 */
public class DoubleHistogramGroup(
    private val lower: Buffer<Double>,
    private val upper: Buffer<Double>,
    private val binNums: IntArray = IntArray(lower.size) { 20 },
) : IndexedHistogramGroup<Double, Double> {

    init {
        // argument checks
        require(lower.size == upper.size) { "Dimension mismatch in histogram lower and upper limits." }
        require(lower.size == binNums.size) { "Dimension mismatch in bin count." }
        require(!lower.indices.any { upper[it] - lower[it] < 0 }) { "Range for one of axis is not strictly positive" }
    }

    public val dimension: Int get() = lower.size

    override val shape: IntArray = IntArray(binNums.size) { binNums[it] + 2 }
    override val histogramValueAlgebra: DoubleFieldND = DoubleField.ndAlgebra(*shape)

    private val binSize = DoubleBuffer(dimension) { (upper[it] - lower[it]) / binNums[it] }

    /**
     * Get internal [StructureND] bin index for given axis
     */
    private fun getIndex(axis: Int, value: Double): Int = when {
        value >= upper[axis] -> binNums[axis] + 1 // overflow
        value < lower[axis] -> 0 // underflow
        else -> floor((value - lower[axis]) / binSize[axis]).toInt()
    }

    override fun getIndexOrNull(point: Buffer<Double>): IntArray = IntArray(dimension) {
        getIndex(it, point[it])
    }

    @OptIn(UnstableKMathAPI::class)
    override fun getDomain(index: IntArray): HyperSquareDomain {
        val lowerBoundary = index.mapIndexed { axis, i ->
            when (i) {
                0 -> Double.NEGATIVE_INFINITY
                shape[axis] - 1 -> upper[axis]
                else -> lower[axis] + (i.toDouble()) * binSize[axis]
            }
        }.asBuffer()

        val upperBoundary = index.mapIndexed { axis, i ->
            when (i) {
                0 -> lower[axis]
                shape[axis] - 1 -> Double.POSITIVE_INFINITY
                else -> lower[axis] + (i.toDouble() + 1) * binSize[axis]
            }
        }.asBuffer()

        return HyperSquareDomain(lowerBoundary, upperBoundary)
    }

    @OptIn(UnstableKMathAPI::class)
    override fun produceBin(index: IntArray, value: Double): DomainBin<Double, Double> {
        val domain = getDomain(index)
        return DomainBin(domain, value)
    }

    override fun produce(builder: HistogramBuilder<Double, Double>.() -> Unit): IndexedHistogram<Double, Double> {
        val ndCounter = StructureND.auto(shape) { Counter.double() }
        val hBuilder = object : HistogramBuilder<Double, Double> {
            override val defaultValue: Double get() = 1.0

            override fun putValue(point: Point<out Double>, value: Double) {
                val index = getIndexOrNull(point)
                ndCounter[index].add(value)
            }
        }
        hBuilder.apply(builder)
        val values: BufferND<Double> = ndCounter.mapToBuffer { it.value }
        return IndexedHistogram(this, values)
    }

    override fun IndexedHistogram<Double, Double>.unaryMinus(): IndexedHistogram<Double, Double> = this * (-1)

    public companion object {
        /**
         * Use it like
         * ```
         *FastHistogram.fromRanges(
         *  (-1.0..1.0),
         *  (-1.0..1.0)
         *)
         *```
         */
        public fun fromRanges(
            vararg ranges: ClosedFloatingPointRange<Double>,
        ): DoubleHistogramGroup = DoubleHistogramGroup(
            ranges.map(ClosedFloatingPointRange<Double>::start).asBuffer(),
            ranges.map(ClosedFloatingPointRange<Double>::endInclusive).asBuffer()
        )

        /**
         * Use it like
         * ```
         *FastHistogram.fromRanges(
         *  (-1.0..1.0) to 50,
         *  (-1.0..1.0) to 32
         *)
         *```
         */
        public fun fromRanges(
            vararg ranges: Pair<ClosedFloatingPointRange<Double>, Int>,
        ): DoubleHistogramGroup = DoubleHistogramGroup(
            ListBuffer(
                ranges
                    .map(Pair<ClosedFloatingPointRange<Double>, Int>::first)
                    .map(ClosedFloatingPointRange<Double>::start)
            ),

            ListBuffer(
                ranges
                    .map(Pair<ClosedFloatingPointRange<Double>, Int>::first)
                    .map(ClosedFloatingPointRange<Double>::endInclusive)
            ),

            ranges.map(Pair<ClosedFloatingPointRange<Double>, Int>::second).toIntArray()
        )
    }
}