@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.interpolation

import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.functions.PiecewisePolynomial
import space.kscience.kmath.functions.value
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer

public fun interface Interpolator<T, X : T, Y : T> {
    public fun interpolate(points: XYColumnarData<T, X, Y>): (X) -> Y
}

public interface PolynomialInterpolator<T : Comparable<T>> : Interpolator<T, T, T> {
    public val algebra: Ring<T>

    public fun getDefaultValue(): T = error("Out of bounds")

    public fun interpolatePolynomials(points: XYColumnarData<T, T, T>): PiecewisePolynomial<T>

    override fun interpolate(points: XYColumnarData<T, T, T>): (T) -> T = { x ->
        interpolatePolynomials(points).value(algebra, x) ?: getDefaultValue()
    }
}


public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    x: Buffer<T>,
    y: Buffer<T>,
): PiecewisePolynomial<T> {
    val pointSet = XYColumnarData(x, y)
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: Map<T, T>,
): PiecewisePolynomial<T> {
    val pointSet = XYColumnarData(data.keys.toList().asBuffer(), data.values.toList().asBuffer())
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: List<Pair<T, T>>,
): PiecewisePolynomial<T> {
    val pointSet = XYColumnarData(data.map { it.first }.asBuffer(), data.map { it.second }.asBuffer())
    return interpolatePolynomials(pointSet)
}
