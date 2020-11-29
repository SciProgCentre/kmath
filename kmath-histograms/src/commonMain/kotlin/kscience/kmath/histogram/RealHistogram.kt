package kscience.kmath.histogram

import kscience.kmath.linear.Point
import kscience.kmath.operations.SpaceOperations
import kscience.kmath.operations.invoke
import kscience.kmath.structures.*
import kotlin.math.floor

public data class BinDef<T : Comparable<T>>(
    public val space: SpaceOperations<Point<T>>,
    public val center: Point<T>,
    public val sizes: Point<T>
) {
    public fun contains(vector: Point<out T>): Boolean {
        require(vector.size == center.size) { "Dimension mismatch for input vector. Expected ${center.size}, but found ${vector.size}" }
        val upper = space { center + sizes / 2.0 }
        val lower = space { center - sizes / 2.0 }
        return vector.asSequence().mapIndexed { i, value -> value in lower[i]..upper[i] }.all { it }
    }
}


public class MultivariateBin<T : Comparable<T>>(public val def: BinDef<T>, public override val value: Number) : Bin<T> {
    public override val dimension: Int
        get() = def.center.size

    public override val center: Point<T>
        get() = def.center

    public override operator fun contains(point: Point<T>): Boolean = def.contains(point)
}

/**
 * Uniform multivariate histogram with fixed borders. Based on NDStructure implementation with complexity of m for bin search, where m is the number of dimensions.
 */
public class RealHistogram(
    private val lower: Buffer<Double>,
    private val upper: Buffer<Double>,
    private val binNums: IntArray = IntArray(lower.size) { 20 }
) : MutableHistogram<Double, MultivariateBin<Double>> {
    private val strides = DefaultStrides(IntArray(binNums.size) { binNums[it] + 2 })
    private val values: NDStructure<LongCounter> = NDStructure.auto(strides) { LongCounter() }
    private val weights: NDStructure<DoubleCounter> = NDStructure.auto(strides) { DoubleCounter() }
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

    private fun getValue(index: IntArray): Long = values[index].sum()

    public fun getValue(point: Buffer<out Double>): Long = getValue(getIndex(point))

    private fun getDef(index: IntArray): BinDef<Double> {
        val center = index.mapIndexed { axis, i ->
            when (i) {
                0 -> Double.NEGATIVE_INFINITY
                strides.shape[axis] - 1 -> Double.POSITIVE_INFINITY
                else -> lower[axis] + (i.toDouble() - 0.5) * binSize[axis]
            }
        }.asBuffer()

        return BinDef(RealBufferFieldOperations, center, binSize)
    }

    public fun getDef(point: Buffer<out Double>): BinDef<Double> = getDef(getIndex(point))

    public override operator fun get(point: Buffer<out Double>): MultivariateBin<Double>? {
        val index = getIndex(point)
        return MultivariateBin(getDef(index), getValue(index))
    }

//    fun put(point: Point<out Double>){
//        val index = getIndex(point)
//        values[index].increment()
//    }

    public override fun putWithWeight(point: Buffer<out Double>, weight: Double) {
        val index = getIndex(point)
        values[index].increment()
        weights[index].add(weight)
    }

    public override operator fun iterator(): Iterator<MultivariateBin<Double>> =
        weights.elements().map { (index, value) -> MultivariateBin(getDef(index), value.sum()) }
            .iterator()

    /**
     * Convert this histogram into NDStructure containing bin values but not bin descriptions
     */
    public fun values(): NDStructure<Number> = NDStructure.auto(values.shape) { values[it].sum() }

    /**
     * Sum of weights
     */
    public fun weights(): NDStructure<Double> = NDStructure.auto(weights.shape) { weights[it].sum() }

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
