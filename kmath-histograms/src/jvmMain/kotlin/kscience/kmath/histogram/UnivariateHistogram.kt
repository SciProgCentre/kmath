package kscience.kmath.histogram

import kscience.kmath.linear.Point
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.SpaceElement
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asBuffer
import kscience.kmath.structures.asSequence
import java.util.*
import kotlin.math.floor

//TODO move to common

public class UnivariateBin(
    public val position: Double,
    public val size: Double,
) : Bin<Double> {
    //internal mutation operations
    internal val counter: LongCounter = LongCounter()
    internal val weightCounter: DoubleCounter = DoubleCounter()

    /**
     * The precise number of events ignoring weighting
     */
    public val count: Long get() = counter.sum()

    /**
     * The value of histogram including weighting
     */
    public override val value: Double get() = weightCounter.sum()

    public override val center: Point<Double> get() = doubleArrayOf(position).asBuffer()
    public override val dimension: Int get() = 1

    public operator fun contains(value: Double): Boolean = value in (position - size / 2)..(position + size / 2)
    public override fun contains(point: Buffer<Double>): Boolean = contains(point[0])
}

/**
 * Univariate histogram with log(n) bin search speed
 */
@OptIn(UnstableKMathAPI::class)
public abstract class UnivariateHistogram protected constructor(
    protected val bins: TreeMap<Double, UnivariateBin> = TreeMap(),
) : Histogram<Double, UnivariateBin>, SpaceElement<UnivariateHistogram, UnivariateHistogramSpace> {

    public operator fun get(value: Double): UnivariateBin? {
        // check ceiling entry and return it if it is what needed
        val ceil = bins.ceilingEntry(value)?.value
        if (ceil != null && value in ceil) return ceil
        //check floor entry
        val floor = bins.floorEntry(value)?.value
        if (floor != null && value in floor) return floor
        //neither is valid, not found
        return null
    }

    public override operator fun get(point: Buffer<out Double>): UnivariateBin? = get(point[0])

    public override val dimension: Int get() = 1

    public override operator fun iterator(): Iterator<UnivariateBin> = bins.values.iterator()

    public companion object {
        /**
         * Build a histogram with a uniform binning with a start at [start] and a bin size of [binSize]
         */
        public fun uniformBuilder(binSize: Double, start: Double = 0.0): UnivariateHistogramBuilder =
            UnivariateHistogramSpace { value ->
                val center = start + binSize * floor((value - start) / binSize + 0.5)
                UnivariateBin(center, binSize)
            }.builder()

        /**
         * Build and fill a [UnivariateHistogram]. Returns a read-only histogram.
         */
        public fun uniform(
            binSize: Double,
            start: Double = 0.0,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = uniformBuilder(binSize, start).apply(builder)

        /**
         * Create a histogram with custom cell borders
         */
        public fun customBuilder(borders: DoubleArray): UnivariateHistogramBuilder {
            val sorted = borders.sortedArray()

            return UnivariateHistogramSpace { value ->
                when {
                    value < sorted.first() -> UnivariateBin(
                        Double.NEGATIVE_INFINITY,
                        Double.MAX_VALUE
                    )

                    value > sorted.last() -> UnivariateBin(
                        Double.POSITIVE_INFINITY,
                        Double.MAX_VALUE
                    )

                    else -> {
                        val index = sorted.indices.first { value > sorted[it] }
                        val left = sorted[index]
                        val right = sorted[index + 1]
                        UnivariateBin((left + right) / 2, (right - left))
                    }
                }
            }.builder()
        }

        /**
         * Build and fill a histogram with custom borders. Returns a read-only histogram.
         */
        public fun custom(
            borders: DoubleArray,
            builder: UnivariateHistogramBuilder.() -> Unit,
        ): UnivariateHistogram = customBuilder(borders).apply(builder)
    }
}

public class UnivariateHistogramBuilder internal constructor(
    override val context: UnivariateHistogramSpace,
) : UnivariateHistogram(), MutableHistogram<Double, UnivariateBin> {

    private fun createBin(value: Double): UnivariateBin = context.binFactory(value).also {
        synchronized(this) { bins[it.position] = it }
    }

    /**
     * Thread safe put operation
     */
    public fun put(value: Double, weight: Double = 1.0) {
        (get(value) ?: createBin(value)).apply {
            counter.increment()
            weightCounter.add(weight)
        }
    }

    override fun putWithWeight(point: Buffer<out Double>, weight: Double) {
        put(point[0], weight)
    }

    /**
     * Put several items into a single bin
     */
    public fun putMany(value: Double, count: Int, weight: Double = count.toDouble()) {
        (get(value) ?: createBin(value)).apply {
            counter.add(count.toLong())
            weightCounter.add(weight)
        }
    }
}

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(items: Iterable<Double>): Unit = items.forEach(::put)

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(array: DoubleArray): Unit = array.forEach(::put)

@UnstableKMathAPI
public fun UnivariateHistogramBuilder.fill(buffer: Buffer<Double>): Unit = buffer.asSequence().forEach(::put)