package kscience.kmath.histogram

import kscience.kmath.linear.Point
import kscience.kmath.nd.DefaultStrides
import kscience.kmath.nd.NDStructure
import kscience.kmath.operations.SpaceOperations
import kscience.kmath.operations.invoke
import kscience.kmath.structures.*
import kotlin.math.floor

public data class MultivariateBinDefinition<T : Comparable<T>>(
    public val space: SpaceOperations<Point<T>>,
    public val center: Point<T>,
    public val sizes: Point<T>,
) {
    public fun contains(vector: Point<out T>): Boolean {
        require(vector.size == center.size) { "Dimension mismatch for input vector. Expected ${center.size}, but found ${vector.size}" }
        val upper = space { center + sizes / 2.0 }
        val lower = space { center - sizes / 2.0 }
        return vector.asSequence().mapIndexed { i, value -> value in lower[i]..upper[i] }.all { it }
    }
}


public class MultivariateBin<T : Comparable<T>>(
    public val definition: MultivariateBinDefinition<T>,
    public val count: Long,
    public override val value: Double,
) : Bin<T> {
    public override val dimension: Int
        get() = definition.center.size

    public override val center: Point<T>
        get() = definition.center

    public override operator fun contains(point: Point<T>): Boolean = definition.contains(point)
}

/**
 * Uniform multivariate histogram with fixed borders. Based on NDStructure implementation with complexity of m for bin search, where m is the number of dimensions.
 */
public class RealHistogram(
    private val lower: Buffer<Double>,
    private val upper: Buffer<Double>,
    private val binNums: IntArray = IntArray(lower.size) { 20 },
) : MutableHistogram<Double, MultivariateBin<Double>> {
    private val strides = DefaultStrides(IntArray(binNums.size) { binNums[it] + 2 })
    private val counts: NDStructure<LongCounter> = NDStructure.auto(strides) { LongCounter() }
    private val values: NDStructure<DoubleCounter> = NDStructure.auto(strides) { DoubleCounter() }
    public override val dimension: Int get() = lower.size
    private val binSize = RealBuffer(dimension) { (upper[it] - lower[it]) / binNums[it] }

    init {
        // argument checks
        require(lower.size == upper.size) { "Dimension mismatch in histogram lower and upper limits." }
        require(lower.size == binNums.size) { "Dimension mismatch in bin count." }
        require(!(0 until dimension).any { upper[it] - lower[it] < 0 }) { "Range for one of axis is not strictly positive" }
    }

    /**
     * Get internal [NDStructure] bin index for given axis
     */
    private fun getIndex(axis: Int, value: Double): Int = when {
        value >= upper[axis] -> binNums[axis] + 1 // overflow
        value < lower[axis] -> 0 // underflow
        else -> floor((value - lower[axis]) / binSize[axis]).toInt() + 1
    }

    private fun getIndex(point: Buffer<out Double>): IntArray = IntArray(dimension) { getIndex(it, point[it]) }

    private fun getCount(index: IntArray): Long = counts[index].sum()

    public fun getCount(point: Buffer<out Double>): Long = getCount(getIndex(point))

    private fun getValue(index: IntArray): Double = values[index].sum()

    public fun getValue(point: Buffer<out Double>): Double = getValue(getIndex(point))

    private fun getBinDefinition(index: IntArray): MultivariateBinDefinition<Double> {
        val center = index.mapIndexed { axis, i ->
            when (i) {
                0 -> Double.NEGATIVE_INFINITY
                strides.shape[axis] - 1 -> Double.POSITIVE_INFINITY
                else -> lower[axis] + (i.toDouble() - 0.5) * binSize[axis]
            }
        }.asBuffer()

        return MultivariateBinDefinition(RealBufferFieldOperations, center, binSize)
    }

    public fun getBinDefinition(point: Buffer<out Double>): MultivariateBinDefinition<Double> = getBinDefinition(getIndex(point))

    public override operator fun get(point: Buffer<out Double>): MultivariateBin<Double>? {
        val index = getIndex(point)
        return MultivariateBin(getBinDefinition(index), getCount(index),getValue(index))
    }

//    fun put(point: Point<out Double>){
//        val index = getIndex(point)
//        values[index].increment()
//    }

    public override fun putWithWeight(point: Buffer<out Double>, weight: Double) {
        val index = getIndex(point)
        counts[index].increment()
        values[index].add(weight)
    }

    public override operator fun iterator(): Iterator<MultivariateBin<Double>> =
        strides.indices().map { index->
            MultivariateBin(getBinDefinition(index), counts[index].sum(), values[index].sum())
        }.iterator()

    /**
     * NDStructure containing number of events in bins without weights
     */
    public fun counts(): NDStructure<Long> = NDStructure.auto(counts.shape) { counts[it].sum() }

    /**
     * NDStructure containing values of bins including weights
     */
    public fun values(): NDStructure<Double> = NDStructure.auto(values.shape) { values[it].sum() }

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
        public fun fromRanges(vararg ranges: ClosedFloatingPointRange<Double>): RealHistogram = RealHistogram(
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
        public fun fromRanges(vararg ranges: Pair<ClosedFloatingPointRange<Double>, Int>): RealHistogram =
            RealHistogram(
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
