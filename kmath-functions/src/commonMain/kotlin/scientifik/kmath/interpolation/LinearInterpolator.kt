package scientifik.kmath.interpolation

import scientifik.kmath.functions.OrderedPiecewisePolynomial
import scientifik.kmath.functions.PiecewisePolynomial
import scientifik.kmath.functions.Polynomial
import scientifik.kmath.operations.Field

/**
 * Reference JVM implementation: https://github.com/apache/commons-math/blob/master/src/main/java/org/apache/commons/math4/analysis/interpolation/LinearInterpolator.java
 */
class LinearInterpolator<T : Comparable<T>>(override val algebra: Field<T>) : PolynomialInterpolator<T> {

    override fun interpolatePolynomials(points: Collection<Pair<T, T>>): PiecewisePolynomial<T> = algebra.run {
        require(points.isNotEmpty()) { "Point array should not be empty" }

        //sorting points
        val sorted = points.sortedBy { it.first }

        return@run OrderedPiecewisePolynomial(points.first().first).apply {
            for (i in 0 until points.size - 1) {
                val slope = (sorted[i + 1].second - sorted[i].second) / (sorted[i + 1].first - sorted[i].first)
                val const = sorted[i].second - slope * sorted[i].first
                val polynomial = Polynomial(const, slope)
                putRight(sorted[i + 1].first, polynomial)
            }
        }
    }
}