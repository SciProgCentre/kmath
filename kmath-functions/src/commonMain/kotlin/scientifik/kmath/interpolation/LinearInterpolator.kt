package scientifik.kmath.interpolation

import scientifik.kmath.functions.PiecewisePolynomial
import scientifik.kmath.functions.Polynomial
import scientifik.kmath.operations.Field

/**
 * Reference JVM implementation: https://github.com/apache/commons-math/blob/master/src/main/java/org/apache/commons/math4/analysis/interpolation/LinearInterpolator.java
 */
class LinearInterpolator<T : Comparable<T>>(override val algebra: Field<T>) : PolynomialInterpolator<T> {

    override fun interpolatePolynomials(points: Collection<Pair<T, T>>): PiecewisePolynomial<T> = algebra.run {
        //sorting points
        val sorted = points.sortedBy { it.first }

        val pairs: List<Pair<T, Polynomial<T>>> = (0 until points.size - 1).map { i ->
            val slope = (sorted[i + 1].second - sorted[i].second) / (sorted[i + 1].first - sorted[i].first)
            val const = sorted[i].second - slope * sorted[i].first
            sorted[i + 1].first to Polynomial(const, slope)
        }

        return PiecewisePolynomial(sorted.first().first, pairs)
    }
}