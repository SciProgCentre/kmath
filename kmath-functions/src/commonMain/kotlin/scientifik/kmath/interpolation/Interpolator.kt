package scientifik.kmath.interpolation

import scientifik.kmath.functions.PiecewisePolynomial
import scientifik.kmath.functions.value
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.asBuffer

interface Interpolator<X, Y> {
    fun interpolate(points: XYPointSet<X, Y>): (X) -> Y
}

interface PolynomialInterpolator<T : Comparable<T>> : Interpolator<T, T> {
    val algebra: Ring<T>

    fun getDefaultValue(): T = error("Out of bounds")

    fun interpolatePolynomials(points: XYPointSet<T, T>): PiecewisePolynomial<T>

    override fun interpolate(points: XYPointSet<T, T>): (T) -> T = { x ->
        interpolatePolynomials(points).value(algebra, x) ?: getDefaultValue()
    }
}

fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    x: Buffer<T>,
    y: Buffer<T>
): PiecewisePolynomial<T> {
    val pointSet = BufferXYPointSet(x, y)
    return interpolatePolynomials(pointSet)
}

fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: Map<T, T>
): PiecewisePolynomial<T> {
    val pointSet = BufferXYPointSet(data.keys.toList().asBuffer(), data.values.toList().asBuffer())
    return interpolatePolynomials(pointSet)
}

fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: List<Pair<T, T>>
): PiecewisePolynomial<T> {
    val pointSet = BufferXYPointSet(data.map { it.first }.asBuffer(), data.map { it.second }.asBuffer())
    return interpolatePolynomials(pointSet)
}