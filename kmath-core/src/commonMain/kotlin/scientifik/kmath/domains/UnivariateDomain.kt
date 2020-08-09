package scientifik.kmath.domains

import scientifik.kmath.linear.Point
import scientifik.kmath.structures.asBuffer

inline class UnivariateDomain(val range: ClosedFloatingPointRange<Double>) : RealDomain {
    operator fun contains(d: Double): Boolean = range.contains(d)

    override operator fun contains(point: Point<Double>): Boolean {
        require(point.size == 0)
        return contains(point[0])
    }

    override fun nearestInDomain(point: Point<Double>): Point<Double> {
        require(point.size == 1)
        val value = point[0]
        return when {
            value in range -> point
            value >= range.endInclusive -> doubleArrayOf(range.endInclusive).asBuffer()
            else -> doubleArrayOf(range.start).asBuffer()
        }
    }

    override fun getLowerBound(num: Int, point: Point<Double>): Double? {
        require(num == 0)
        return range.start
    }

    override fun getUpperBound(num: Int, point: Point<Double>): Double? {
        require(num == 0)
        return range.endInclusive
    }

    override fun getLowerBound(num: Int): Double? {
        require(num == 0)
        return range.start
    }

    override fun getUpperBound(num: Int): Double? {
        require(num == 0)
        return range.endInclusive
    }

    override fun volume(): Double = range.endInclusive - range.start

    override val dimension: Int get() = 1
}
