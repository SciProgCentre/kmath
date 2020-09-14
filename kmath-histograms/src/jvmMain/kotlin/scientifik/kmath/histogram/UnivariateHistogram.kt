package scientifik.kmath.histogram

import scientifik.kmath.real.RealVector
import scientifik.kmath.real.asVector
import scientifik.kmath.structures.Buffer
import java.util.*
import kotlin.math.floor

//TODO move to common

class UnivariateBin(val position: Double, val size: Double, val counter: LongCounter = LongCounter()) : Bin<Double> {
    //TODO add weighting
    override val value: Number get() = counter.sum()

    override val center: RealVector get() = doubleArrayOf(position).asVector()

    operator fun contains(value: Double): Boolean = value in (position - size / 2)..(position + size / 2)

    override fun contains(point: Buffer<Double>): Boolean = contains(point[0])

    internal operator fun inc() = this.also { counter.increment() }

    override val dimension: Int get() = 1
}

/**
 * Univariate histogram with log(n) bin search speed
 */
class UnivariateHistogram private constructor(private val factory: (Double) -> UnivariateBin) :
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
        synchronized(this) { bins.put(it.position, it) }
    }

    override operator fun get(point: Buffer<out Double>): UnivariateBin? = get(point[0])

    override val dimension: Int get() = 1

    override operator fun iterator(): Iterator<UnivariateBin> = bins.values.iterator()

    /**
     * Thread safe put operation
     */
    fun put(value: Double) {
        (get(value) ?: createBin(value)).inc()
    }

    override fun putWithWeight(point: Buffer<out Double>, weight: Double) {
        if (weight != 1.0) TODO("Implement weighting")
        put(point[0])
    }

    companion object {
        fun uniform(binSize: Double, start: Double = 0.0): UnivariateHistogram = UnivariateHistogram { value ->
            val center = start + binSize * floor((value - start) / binSize + 0.5)
            UnivariateBin(center, binSize)
        }

        fun custom(borders: DoubleArray): UnivariateHistogram {
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
                        val index = (0 until sorted.size).first { value > sorted[it] }
                        val left = sorted[index]
                        val right = sorted[index + 1]
                        UnivariateBin((left + right) / 2, (right - left))
                    }
                }
            }
        }
    }
}

fun UnivariateHistogram.fill(sequence: Iterable<Double>) = sequence.forEach { put(it) }
