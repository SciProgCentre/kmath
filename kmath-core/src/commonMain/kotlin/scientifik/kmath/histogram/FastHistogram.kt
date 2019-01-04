package scientifik.kmath.histogram

import scientifik.kmath.linear.toVector
import scientifik.kmath.structures.*
import kotlin.math.floor

private operator fun RealPoint.minus(other: RealPoint) = ListBuffer((0 until size).map { get(it) - other[it] })

private inline fun <T> Buffer<out Double>.mapIndexed(crossinline mapper: (Int, Double) -> T): Sequence<T> =
    (0 until size).asSequence().map { mapper(it, get(it)) }

/**
 * Uniform multivariate histogram with fixed borders. Based on NDStructure implementation with complexity of m for bin search, where m is the number of dimensions.
 */
class FastHistogram(
    private val lower: RealPoint,
    private val upper: RealPoint,
    private val binNums: IntArray = IntArray(lower.size) { 20 }
) : MutableHistogram<Double, PhantomBin<Double>> {


    private val strides = DefaultStrides(IntArray(binNums.size) { binNums[it] + 2 })

    private val values: NDStructure<LongCounter> = inlineNDStructure(strides) { LongCounter() }

    //private val weight: NDStructure<DoubleCounter?> = ndStructure(strides){null}

    //TODO optimize binSize performance if needed
    private val binSize: RealPoint =
        ListBuffer((upper - lower).mapIndexed { index, value -> value / binNums[index] }.toList())

    init {
        // argument checks
        if (lower.size != upper.size) error("Dimension mismatch in histogram lower and upper limits.")
        if (lower.size != binNums.size) error("Dimension mismatch in bin count.")
        if ((upper - lower).asSequence().any { it <= 0 }) error("Range for one of axis is not strictly positive")
    }


    override val dimension: Int get() = lower.size


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

    private fun getIndex(point: Buffer<out Double>): IntArray = IntArray(dimension) { getIndex(it, point[it]) }

    private fun getValue(index: IntArray): Long {
        return values[index].sum()
    }

    fun getValue(point: Buffer<out Double>): Long {
        return getValue(getIndex(point))
    }

    private fun getTemplate(index: IntArray): BinTemplate<Double> {
        val center = index.mapIndexed { axis, i ->
            when (i) {
                0 -> Double.NEGATIVE_INFINITY
                strides.shape[axis] - 1 -> Double.POSITIVE_INFINITY
                else -> lower[axis] + (i.toDouble() - 0.5) * binSize[axis]
            }
        }.toVector()
        return BinTemplate(center, binSize)
    }

    fun getTemplate(point: Buffer<out Double>): BinTemplate<Double> {
        return getTemplate(getIndex(point))
    }

    override fun get(point: Buffer<out Double>): PhantomBin<Double>? {
        val index = getIndex(point)
        return PhantomBin(getTemplate(index), getValue(index))
    }

    override fun put(point: Buffer<out Double>, weight: Double) {
        if (weight != 1.0) TODO("Implement weighting")
        val index = getIndex(point)
        values[index].increment()
    }

    override fun iterator(): Iterator<PhantomBin<Double>> = values.elements().map { (index, value) ->
        PhantomBin(getTemplate(index), value.sum())
    }.iterator()

    /**
     * Convert this histogram into NDStructure containing bin values but not bin descriptions
     */
    fun asNDStructure(): NDStructure<Number> {
        return inlineNdStructure(this.values.shape) { values[it].sum() }
    }

    /**
     * Create a phantom lightweight immutable copy of this histogram
     */
    fun asPhantomHistogram(): PhantomHistogram<Double> {
        val binTemplates = values.elements().associate { (index, _) -> getTemplate(index) to index }
        return PhantomHistogram(binTemplates, asNDStructure())
    }

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
                ListBuffer(ranges.map { it.first.start }),
                ListBuffer(ranges.map { it.first.endInclusive }),
                ranges.map { it.second }.toIntArray()
            )
        }
    }

}