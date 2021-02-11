package kscience.kmath.histogram

import kscience.kmath.operations.Space
import kscience.kmath.structures.Buffer
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

private fun <B : UnivariateBin> TreeMap<Double, B>.getBin(value: Double): B? {
    // check ceiling entry and return it if it is what needed
    val ceil = ceilingEntry(value)?.value
    if (ceil != null && value in ceil) return ceil
    //check floor entry
    val floor = floorEntry(value)?.value
    if (floor != null && value in floor) return floor
    //neither is valid, not found
    return null
}


private class UnivariateHistogramImpl(
    override val context: UnivariateHistogramSpace,
    val binMap: TreeMap<Double, out UnivariateBin>,
) : UnivariateHistogram {
    override fun get(value: Double): UnivariateBin? = binMap.getBin(value)
    override val dimension: Int get() = 1
    override val bins: Collection<UnivariateBin> get() = binMap.values
}

private class UnivariateBinCounter(
    override val def: UnivariateHistogramBinDefinition,
) : UnivariateBin {
    val counter: LongCounter = LongCounter()
    val valueCounter: DoubleCounter = DoubleCounter()

    /**
     * The precise number of events ignoring weighting
     */
    val count: Long get() = counter.sum()

    override val standardDeviation: Double get() = sqrt(count.toDouble()) / count * value

    /**
     * The value of histogram including weighting
     */
    override val value: Double get() = valueCounter.sum()

    public fun increment(count: Long, value: Double) {
        counter.add(count)
        valueCounter.add(value)
    }
}

private class UnivariateBinValue(
    override val def: UnivariateHistogramBinDefinition,
    override val value: Double,
    override val standardDeviation: Double,
) : UnivariateBin


public class UnivariateHistogramSpace(
    public val binFactory: (Double) -> UnivariateHistogramBinDefinition,
) : Space<UnivariateHistogram> {

    private inner class UnivariateHistogramBuilderImpl : UnivariateHistogramBuilder {

        val bins: TreeMap<Double, UnivariateBinCounter> = TreeMap()
        fun get(value: Double): UnivariateBinCounter? = bins.getBin(value)

        private fun createBin(value: Double): UnivariateBinCounter {
            val binDefinition = binFactory(value)
            val newBin = UnivariateBinCounter(binDefinition)
            synchronized(this) { bins[binDefinition.position] = newBin }
            return newBin
        }

        /**
         * Thread safe put operation
         */
        override fun put(value: Double, weight: Double) {
            (get(value) ?: createBin(value)).apply {
                increment(1, weight)
            }
        }

        override fun putWithWeight(point: Buffer<out Double>, weight: Double) {
            put(point[0], weight)
        }

        /**
         * Put several items into a single bin
         */
        override fun putMany(value: Double, count: Int, weight: Double) {
            (get(value) ?: createBin(value)).apply {
                increment(count.toLong(), weight)
            }
        }

        override fun build(): UnivariateHistogram = UnivariateHistogramImpl(this@UnivariateHistogramSpace, bins)
    }


    public fun builder(): UnivariateHistogramBuilder = UnivariateHistogramBuilderImpl()

    public fun produce(builder: UnivariateHistogramBuilder.() -> Unit): UnivariateHistogram =
        UnivariateHistogramBuilderImpl().apply(builder).build()

    override fun add(
        a: UnivariateHistogram,
        b: UnivariateHistogram,
    ): UnivariateHistogram {
        require(a.context == this) { "Histogram $a does not belong to this context" }
        require(b.context == this) { "Histogram $b does not belong to this context" }
        val bins = TreeMap<Double, UnivariateBin>().apply {
            (a.bins.map { it.def } union b.bins.map { it.def }).forEach { def ->
                val newBin = UnivariateBinValue(
                    def,
                    value = (a[def.position]?.value ?: 0.0) + (b[def.position]?.value ?: 0.0),
                    standardDeviation = (a[def.position]?.standardDeviation
                        ?: 0.0) + (b[def.position]?.standardDeviation ?: 0.0)
                )
            }
        }
        return UnivariateHistogramImpl(this, bins)
    }

    override fun multiply(a: UnivariateHistogram, k: Number): UnivariateHistogram {
        val bins = TreeMap<Double, UnivariateBin>().apply {
            a.bins.forEach { bin ->
                put(bin.position,
                    UnivariateBinValue(
                        bin.def,
                        value = bin.value * k.toDouble(),
                        standardDeviation = abs(bin.standardDeviation * k.toDouble())
                    )
                )
            }
        }

        return UnivariateHistogramImpl(this, bins)
    }

    override val zero: UnivariateHistogram = produce { }

    public companion object {
        /**
         * Build and fill a [UnivariateHistogram]. Returns a read-only histogram.
         */
        public fun uniform(
            binSize: Double,
            start: Double = 0.0
        ): UnivariateHistogramSpace = UnivariateHistogramSpace { value ->
            val center = start + binSize * Math.floor((value - start) / binSize + 0.5)
            UnivariateHistogramBinDefinition(center, binSize)
        }

        /**
         * Create a histogram with custom cell borders
         */
        public fun custom(borders: DoubleArray): UnivariateHistogramSpace {
            val sorted = borders.sortedArray()

            return UnivariateHistogramSpace { value ->
                when {
                    value < sorted.first() -> UnivariateHistogramBinDefinition(
                        Double.NEGATIVE_INFINITY,
                        Double.MAX_VALUE
                    )

                    value > sorted.last() -> UnivariateHistogramBinDefinition(
                        Double.POSITIVE_INFINITY,
                        Double.MAX_VALUE
                    )

                    else -> {
                        val index = sorted.indices.first { value > sorted[it] }
                        val left = sorted[index]
                        val right = sorted[index + 1]
                        UnivariateHistogramBinDefinition((left + right) / 2, (right - left))
                    }
                }
            }
        }
    }
}