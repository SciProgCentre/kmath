package scientifik.kmath.histogram

import scientifik.kmath.linear.RealVector
import scientifik.kmath.linear.toVector
import scientifik.kmath.structures.NDStructure
import scientifik.kmath.structures.ndStructure
import kotlin.math.floor

class MultivariateBin(override val center: RealVector, val sizes: RealVector, val counter: LongCounter = LongCounter()) : Bin<Double> {
    init {
        if (center.size != sizes.size) error("Dimension mismatch in bin creation. Expected ${center.size}, but found ${sizes.size}")
    }

    override fun contains(vector: RealVector): Boolean {
        if (vector.size != center.size) error("Dimension mismatch for input vector. Expected ${center.size}, but found ${vector.size}")
        return vector.asSequence().mapIndexed { i, value -> value in (center[i] - sizes[i] / 2)..(center[i] + sizes[i] / 2) }.all { it }
    }

    override val value: Number get() = counter.sum()
    internal operator fun inc() = this.also { counter.increment() }

    override val dimension: Int get() = center.size
}

/**
 * Uniform multivariate histogram with fixed borders. Based on NDStructure implementation with complexity of m for bin search, where m is the number of dimensions
 */
class FastHistogram(
        private val lower: RealVector,
        private val upper: RealVector,
        private val binNums: IntArray = IntArray(lower.size) { 20 }
) : Histogram<Double, MultivariateBin> {

    init {
        // argument checks
        if (lower.size != upper.size) error("Dimension mismatch in histogram lower and upper limits.")
        if (lower.size != binNums.size) error("Dimension mismatch in bin count.")
        if ((upper - lower).any { it <= 0 }) error("Range for one of axis is not strictly positive")
    }


    override val dimension: Int get() = lower.size

    //TODO optimize binSize performance if needed
    private val binSize = (upper - lower).mapIndexed { index, value -> value / binNums[index] }.toVector()

    private val bins: NDStructure<MultivariateBin> by lazy {
        val actualSizes = IntArray(binNums.size) { binNums[it] + 2 }
        ndStructure(actualSizes) { indexArray ->
            val center = indexArray.mapIndexed { axis, index ->
                when (index) {
                    0 -> Double.NEGATIVE_INFINITY
                    actualSizes[axis] -> Double.POSITIVE_INFINITY
                    else -> lower[axis] + (index - 1) * binSize[axis]
                }
            }.toVector()
            MultivariateBin(center, binSize)
        }
    }

    /**
     * Get internal [NDStructure] bin index for given axis
     */
    private fun getIndex(axis: Int, value: Double): Int {
        return when {
            value >= upper[axis] -> binNums[axis] + 1 // overflow
            value < lower[axis] -> 0 // underflow
            else -> floor((value - lower[axis]) / binSize[axis]).toInt() + 1
        }
    }


    override fun get(point: RealVector): MultivariateBin? {
        val index = IntArray(dimension) { getIndex(it, point[it]) }
        return bins[index]
    }

    override fun put(point: RealVector) {
        this[point]?.inc() ?: error("Could not find appropriate bin (should not be possible)")
    }

    override fun iterator(): Iterator<MultivariateBin> = bins.asSequence().map { it.second }.iterator()

    companion object {

        /**
         * Use it like
         * ```
         *FastHistogram.fromRanges(
         *  (-1.0..1.0),
         *  (-1.0..1.0)
         *)
         *```
         */
        fun fromRanges(vararg ranges: ClosedFloatingPointRange<Double>): FastHistogram {
            return FastHistogram(ranges.map { it.start }.toVector(), ranges.map { it.endInclusive }.toVector())
        }

        /**
         * Use it like
         * ```
         *FastHistogram.fromRanges(
         *  (-1.0..1.0) to 50,
         *  (-1.0..1.0) to 32
         *)
         *```
         */
        fun fromRanges(vararg ranges: Pair<ClosedFloatingPointRange<Double>, Int>): FastHistogram {
            return FastHistogram(
                    ranges.map { it.first.start }.toVector(),
                    ranges.map { it.first.endInclusive }.toVector(),
                    ranges.map { it.second }.toIntArray()
            )
        }
    }

}