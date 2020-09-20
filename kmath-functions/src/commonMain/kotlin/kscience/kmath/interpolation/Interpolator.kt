package kscience.kmath.interpolation

import kscience.kmath.functions.PiecewisePolynomial
import kscience.kmath.functions.value
import kscience.kmath.operations.Ring
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asBuffer

public fun interface Interpolator<X, Y> {
    public fun interpolate(points: XYPointSet<X, Y>): (X) -> Y
}

public interface PolynomialInterpolator<T : Comparable<T>> : Interpolator<T, T> {
    public val algebra: Ring<T>

    public fun getDefaultValue(): T = error("Out of bounds")

    public fun interpolatePolynomials(points: XYPointSet<T, T>): PiecewisePolynomial<T>

    override fun interpolate(points: XYPointSet<T, T>): (T) -> T = { x ->
        interpolatePolynomials(points).value(algebra, x) ?: getDefaultValue()
    }
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    x: Buffer<T>,
    y: Buffer<T>
): PiecewisePolynomial<T> {
    val pointSet = BufferXYPointSet(x, y)
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: Map<T, T>
): PiecewisePolynomial<T> {
    val pointSet = BufferXYPointSet(data.keys.toList().asBuffer(), data.values.toList().asBuffer())
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: List<Pair<T, T>>
): PiecewisePolynomial<T> {
    val pointSet = BufferXYPointSet(data.map { it.first }.asBuffer(), data.map { it.second }.asBuffer())
    return interpolatePolynomials(pointSet)
}
