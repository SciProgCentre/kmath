package kscience.kmath.histogram

import kscience.kmath.real.RealVector
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asBuffer
import java.util.*
import kotlin.math.floor

//TODO move to common

public class UnivariateBin(
    public val position: Double,
    public val size: Double,
    public val counter: LongCounter = LongCounter()
) : Bin<Double> {
    //TODO add weighting
    public override val value: Number get() = counter.sum()

    public override val center: RealVector get() = doubleArrayOf(position).asBuffer()
    public override val dimension: Int get() = 1

    public operator fun contains(value: Double): Boolean = value in (position - size / 2)..(position + size / 2)
    public override fun contains(point: Buffer<Double>): Boolean = contains(point[0])
    internal operator fun inc(): UnivariateBin = this.also { counter.increment() }
}

/**
 * Univariate histogram with log(n) bin search speed
 */
public class UnivariateHistogram private constructor(private val factory: (Double) -> UnivariateBin) :
    MutableHistogram<Double, UnivariateBin> {

    private val bins: TreeMap<Double, UnivariateBin> = TreeMap()

    private operator fun get(value: Double): UnivariateBin? {
        // check ceiling entry and return it if it is what needed
        val ceil = bins.ceilingEntry(value)?.value
        if (ceil != null && value in ceil) return ceil
        //check floor entry
        val floor = bins.floorEntry(value)?.value
        if (floor != null && value in floor) return floor
        //neither is valid, not found
        return null
    }

    private fun createBin(value: Double): UnivariateBin = factory(value).also {
        synchronized(this) { bins[it.position] = it }
    }

    public override operator fun get(point: Buffer<out Double>): UnivariateBin? = get(point[0])

    public override val dimension: Int get() = 1

    public override operator fun iterator(): Iterator<UnivariateBin> = bins.values.iterator()

    /**
     * Thread safe put operation
     */
    public fun put(value: Double) {
        (get(value) ?: createBin(value)).inc()
    }

    override fun putWithWeight(point: Buffer<out Double>, weight: Double) {
        if (weight != 1.0) TODO("Implement weighting")
        put(point[0])
    }

    public companion object {
        public fun uniform(binSize: Double, start: Double = 0.0): UnivariateHistogram = UnivariateHistogram { value ->
            val center = start + binSize * floor((value - start) / binSize + 0.5)
            UnivariateBin(center, binSize)
        }

        public fun custom(borders: DoubleArray): UnivariateHistogram {
            val sorted = borders.sortedArray()

            return UnivariateHistogram { value ->
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
            }
        }
    }
}

public fun UnivariateHistogram.fill(sequence: Iterable<Double>): Unit = sequence.forEach(::put)
