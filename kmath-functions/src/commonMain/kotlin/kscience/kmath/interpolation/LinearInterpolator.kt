package kscience.kmath.interpolation

import kscience.kmath.functions.OrderedPiecewisePolynomial
import kscience.kmath.functions.PiecewisePolynomial
import kscience.kmath.functions.Polynomial
import kscience.kmath.operations.Field
import kscience.kmath.operations.invoke

/**
 * Reference JVM implementation: https://github.com/apache/commons-math/blob/master/src/main/java/org/apache/commons/math4/analysis/interpolation/LinearInterpolator.java
 */
public class LinearInterpolator<T : Comparable<T>>(public override val algebra: Field<T>) : PolynomialInterpolator<T> {
    public override fun interpolatePolynomials(points: XYPointSet<T, T>): PiecewisePolynomial<T> = algebra {
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
