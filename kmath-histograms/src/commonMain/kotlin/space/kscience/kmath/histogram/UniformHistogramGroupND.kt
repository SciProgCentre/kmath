/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.histogram

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.domains.HyperSquareDomain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*
import kotlin.math.floor

public typealias HyperSquareBin<V> = DomainBin<Double, HyperSquareDomain, V>

/**
 * Multivariate histogram space for hyper-square real-field bins.
 * @param valueBufferFactory is an optional parameter used to optimize buffer production.
 */
public class UniformHistogramGroupND<V : Any, A : Field<V>>(
    override val valueAlgebraND: FieldOpsND<V, A>,
    private val lower: Buffer<Double>,
    private val upper: Buffer<Double>,
    private val binNums: IntArray = IntArray(lower.size) { 20 },
    private val valueBufferFactory: BufferFactory<V> = valueAlgebraND.elementAlgebra.bufferFactory,
) : HistogramGroupND<Double, HyperSquareDomain, V> {

    init {
        // argument checks
        require(lower.size == upper.size) { "Dimension mismatch in histogram lower and upper limits." }
        require(lower.size == binNums.size) { "Dimension mismatch in bin count." }
        require(!lower.indices.any { upper[it] - lower[it] < 0 }) { "Range for one of axis is not strictly positive" }
    }

    public val dimension: Int get() = lower.size

    override val shape: ShapeND = ShapeND(IntArray(binNums.size) { binNums[it] + 2 })

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

    override fun produceBin(index: IntArray, value: V): HyperSquareBin<V> {
        val domain = getDomain(index)
        return DomainBin(domain, value)
    }


    @OptIn(PerformancePitfall::class)
    override fun produce(
        builder: HistogramBuilder<Double, V>.() -> Unit,
    ): HistogramND<Double, HyperSquareDomain, V> {
        val ndCounter: BufferND<ObjectCounter<V>> =
            StructureND.buffered(shape) { Counter.of(valueAlgebraND.elementAlgebra) }
        val hBuilder = object : HistogramBuilder<Double, V> {
            override val defaultValue: V get() = valueAlgebraND.elementAlgebra.one

            override fun putValue(point: Point<out Double>, value: V) = with(valueAlgebraND.elementAlgebra) {
                val index = getIndexOrNull(point)
                ndCounter[index].add(value)
            }
        }
        hBuilder.apply(builder)
        val values: BufferND<V> = BufferND(ndCounter.indices, ndCounter.buffer.mapToBuffer(valueBufferFactory) { it.value })

        return HistogramND(this, values)
    }

    override fun HistogramND<Double, HyperSquareDomain, V>.unaryMinus(): HistogramND<Double, HyperSquareDomain, V> =
        this * (-1)
}

/**
 * Use it like
 * ```
 *FastHistogram.fromRanges(
 *  (-1.0..1.0),
 *  (-1.0..1.0)
 *)
 *```
 */
public fun <V : Any, A : Field<V>> Histogram.Companion.uniformNDFromRanges(
    valueAlgebraND: FieldOpsND<V, A>,
    vararg ranges: ClosedFloatingPointRange<Double>,
    bufferFactory: BufferFactory<V> = valueAlgebraND.elementAlgebra.bufferFactory,
): UniformHistogramGroupND<V, A> = UniformHistogramGroupND(
    valueAlgebraND,
    ranges.map(ClosedFloatingPointRange<Double>::start).asBuffer(),
    ranges.map(ClosedFloatingPointRange<Double>::endInclusive).asBuffer(),
    valueBufferFactory = bufferFactory
)

public fun Histogram.Companion.uniformDoubleNDFromRanges(
    vararg ranges: ClosedFloatingPointRange<Double>,
): UniformHistogramGroupND<Double, DoubleField> =
    uniformNDFromRanges(DoubleFieldOpsND, *ranges, bufferFactory = ::DoubleBuffer)


/**
 * Use it like
 * ```
 *FastHistogram.fromRanges(
 *  (-1.0..1.0) to 50,
 *  (-1.0..1.0) to 32
 *)
 *```
 */
public fun <V : Any, A : Field<V>> Histogram.Companion.uniformNDFromRanges(
    valueAlgebraND: FieldOpsND<V, A>,
    vararg ranges: Pair<ClosedFloatingPointRange<Double>, Int>,
    bufferFactory: BufferFactory<V> = valueAlgebraND.elementAlgebra.bufferFactory,
): UniformHistogramGroupND<V, A> = UniformHistogramGroupND(
    valueAlgebraND,
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
    ranges.map(Pair<ClosedFloatingPointRange<Double>, Int>::second).toIntArray(),
    valueBufferFactory = bufferFactory
)

public fun Histogram.Companion.uniformDoubleNDFromRanges(
    vararg ranges: Pair<ClosedFloatingPointRange<Double>, Int>,
): UniformHistogramGroupND<Double, DoubleField> =
    uniformNDFromRanges(DoubleFieldOpsND, *ranges, bufferFactory = ::DoubleBuffer)