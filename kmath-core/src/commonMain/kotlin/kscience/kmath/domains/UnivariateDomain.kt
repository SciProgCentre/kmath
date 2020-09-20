package kscience.kmath.domains

import kscience.kmath.linear.Point
import kscience.kmath.structures.asBuffer

public inline class UnivariateDomain(public val range: ClosedFloatingPointRange<Double>) : RealDomain {
    public override val dimension: Int
        get() = 1

    public operator fun contains(d: Double): Boolean = range.contains(d)

    public override operator fun contains(point: Point<Double>): Boolean {
        require(point.size == 0)
        return contains(point[0])
    }

    public override fun nearestInDomain(point: Point<Double>): Point<Double> {
        require(point.size == 1)
        val value = point[0]

        return when {
            value in range -> point
            value >= range.endInclusive -> doubleArrayOf(range.endInclusive).asBuffer()
            else -> doubleArrayOf(range.start).asBuffer()
        }
    }

    public override fun getLowerBound(num: Int, point: Point<Double>): Double? {
        require(num == 0)
        return range.start
    }

    public override fun getUpperBound(num: Int, point: Point<Double>): Double? {
        require(num == 0)
        return range.endInclusive
    }

    public override fun getLowerBound(num: Int): Double? {
        require(num == 0)
        return range.start
    }

    public override fun getUpperBound(num: Int): Double? {
        require(num == 0)
        return range.endInclusive
    }

    public override fun volume(): Double = range.endInclusive - range.start
}
