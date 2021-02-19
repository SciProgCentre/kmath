package space.kscience.kmath.histogram

import space.kscience.kmath.domains.UnivariateDomain
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Space
import space.kscience.kmath.structures.Buffer
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

private fun <B : ClosedFloatingPointRange<Double>> TreeMap<Double, B>.getBin(value: Double): B? {
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
    override val context: TreeHistogramSpace,
    private val binMap: TreeMap<Double, out UnivariateBin>,
) : UnivariateHistogram {
    override fun get(value: Double): UnivariateBin? = binMap.getBin(value)
    override val dimension: Int get() = 1
    override val bins: Collection<UnivariateBin> get() = binMap.values
}

/**
 * A space for univariate histograms with variable bin borders based on a tree map
 */
@UnstableKMathAPI
public class TreeHistogramSpace(
    public val binFactory: (Double) -> UnivariateDomain,
) : Space<UnivariateHistogram> {

    private class BinCounter(val domain: UnivariateDomain, val counter: Counter<Double> = Counter.real()) :
        ClosedFloatingPointRange<Double> by domain.range

    public fun produce(builder: UnivariateHistogramBuilder.() -> Unit): UnivariateHistogram {
        val bins: TreeMap<Double, BinCounter> = TreeMap()
        val hBuilder = object : UnivariateHistogramBuilder {

            fun get(value: Double): BinCounter? = bins.getBin(value)

            fun createBin(value: Double): BinCounter {
                val binDefinition = binFactory(value)
                val newBin = BinCounter(binDefinition)
                synchronized(this) { bins[binDefinition.center] = newBin }
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

            override fun putValue(point: Buffer<Double>, value: Number) {
                put(point[0], value.toDouble())
            }
        }
        hBuilder.apply(builder)
        val resBins = TreeMap<Double, UnivariateBin>()
        bins.forEach { (key, binCounter) ->
            val count = binCounter.counter.value
            resBins[key] = UnivariateBin(binCounter.domain, count, sqrt(count))
        }
        return TreeHistogram(this, resBins)
    }

    override fun add(
        a: UnivariateHistogram,
        b: UnivariateHistogram,
    ): UnivariateHistogram {
        require(a.context == this) { "Histogram $a does not belong to this context" }
        require(b.context == this) { "Histogram $b does not belong to this context" }
        val bins = TreeMap<Double, UnivariateBin>().apply {
            (a.bins.map { it.domain } union b.bins.map { it.domain }).forEach { def ->
                put(def.center,
                    UnivariateBin(
                        def,
                        value = (a[def.center]?.value ?: 0.0) + (b[def.center]?.value ?: 0.0),
                        standardDeviation = (a[def.center]?.standardDeviation
                            ?: 0.0) + (b[def.center]?.standardDeviation ?: 0.0)
                    )
                )
            }
        }
        return TreeHistogram(this, bins)
    }

    override fun multiply(a: UnivariateHistogram, k: Number): UnivariateHistogram {
        val bins = TreeMap<Double, UnivariateBin>().apply {
            a.bins.forEach { bin ->
                put(bin.domain.center,
                    UnivariateBin(
                        bin.domain,
                        value = bin.value * k.toDouble(),
                        standardDeviation = abs(bin.standardDeviation * k.toDouble())
                    )
                )
            }
        }

        return TreeHistogram(this, bins)
    }

    override val zero: UnivariateHistogram = produce { }

    public companion object {
        /**
         * Build and fill a [UnivariateHistogram]. Returns a read-only histogram.
         */
        public fun uniform(
            binSize: Double,
            start: Double = 0.0,
        ): TreeHistogramSpace = TreeHistogramSpace { value ->
            val center = start + binSize * floor((value - start) / binSize + 0.5)
            UnivariateDomain((center - binSize / 2)..(center + binSize / 2))
        }

        /**
         * Create a histogram with custom cell borders
         */
        public fun custom(borders: DoubleArray): TreeHistogramSpace {
            val sorted = borders.sortedArray()

            return TreeHistogramSpace { value ->
                when {
                    value < sorted.first() -> UnivariateDomain(
                        Double.NEGATIVE_INFINITY..sorted.first()
                    )

                    value > sorted.last() -> UnivariateDomain(
                        sorted.last()..Double.POSITIVE_INFINITY
                    )

                    else -> {
                        val index = sorted.indices.first { value > sorted[it] }
                        val left = sorted[index]
                        val right = sorted[index + 1]
                        UnivariateDomain(left..right)
                    }
                }
            }
        }
    }
}