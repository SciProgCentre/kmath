package scientifik.kmath.interpolation

import scientifik.kmath.functions.PiecewisePolynomial
import scientifik.kmath.functions.value
import scientifik.kmath.operations.Ring

interface Interpolator<X, Y> {
    fun interpolate(points: Collection<Pair<X, Y>>): (X) -> Y
}

interface PolynomialInterpolator<T : Comparable<T>> : Interpolator<T, T> {
    val algebra: Ring<T>

    fun getDefaultValue(): T = error("Out of bounds")

    fun interpolatePolynomials(points: Collection<Pair<T, T>>): PiecewisePolynomial<T>

    override fun interpolate(points: Collection<Pair<T, T>>): (T) -> T = { x ->
        interpolatePolynomials(points).value(algebra, x) ?: getDefaultValue()
    }
}