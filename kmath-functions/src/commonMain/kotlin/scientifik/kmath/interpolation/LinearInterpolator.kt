package scientifik.kmath.interpolation

import scientifik.kmath.functions.OrderedPiecewisePolynomial
import scientifik.kmath.functions.PiecewisePolynomial
import scientifik.kmath.functions.Polynomial
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.invoke

/**
 * Reference JVM implementation: https://github.com/apache/commons-math/blob/master/src/main/java/org/apache/commons/math4/analysis/interpolation/LinearInterpolator.java
 */
class LinearInterpolator<T : Comparable<T>>(override val algebra: Field<T>) : PolynomialInterpolator<T> {
    override fun interpolatePolynomials(points: XYPointSet<T, T>): PiecewisePolynomial<T> = algebra {
        require(points.size > 0) { "Point array should not be empty" }
        insureSorted(points)

        OrderedPiecewisePolynomial(points.x[0]).apply {
            for (i in 0 until points.size - 1) {
                val slope = (points.y[i + 1] - points.y[i]) / (points.x[i + 1] - points.x[i])
                val const = points.y[i] - slope * points.x[i]
                val polynomial = Polynomial(const, slope)
                putRight(points.x[i + 1], polynomial)
            }
        }
    }
}
