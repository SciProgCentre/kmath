package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI

@UnstableKMathAPI
public class UnivariateDomain(public val range: ClosedFloatingPointRange<Double>) : DoubleDomain {
    public override val dimension: Int get() = 1

    public operator fun contains(d: Double): Boolean = range.contains(d)

    public override operator fun contains(point: Point<Double>): Boolean {
        require(point.size == 0)
        return contains(point[0])
    }

    public override fun getLowerBound(num: Int): Double {
        require(num == 0)
        return range.start
    }

    public override fun getUpperBound(num: Int): Double {
        require(num == 0)
        return range.endInclusive
    }

    public override fun volume(): Double = range.endInclusive - range.start
}
