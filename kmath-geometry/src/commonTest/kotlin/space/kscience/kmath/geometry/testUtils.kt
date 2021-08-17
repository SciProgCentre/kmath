package space.kscience.kmath.geometry

import kotlin.math.abs
import kotlin.test.assertEquals

fun ClosedRange<Double>.generateList(step: Double): List<Double> = generateSequence(start) { previous ->
    if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
    val next = previous + step
    if (next > endInclusive) null else next
}.toList()

fun grid(
    xRange: ClosedRange<Double>,
    yRange: ClosedRange<Double>,
    step: Double
): List<Pair<Double, Double>> {
    val xs = xRange.generateList(step)
    val ys = yRange.generateList(step)

    return xs.flatMap { x -> ys.map { y -> x to y } }
}

fun assertVectorEquals(expected: Vector2D, actual: Vector2D, eps: Double = 1e-6) {
    assertEquals(expected.x, actual.x, eps)
    assertEquals(expected.y, actual.y, eps)
}

fun assertVectorEquals(expected: Vector3D, actual: Vector3D, eps: Double = 1e-6) {
    assertEquals(expected.x, actual.x, eps)
    assertEquals(expected.y, actual.y, eps)
    assertEquals(expected.z, actual.z, eps)
}

fun <V : Vector> GeometrySpace<V>.isCollinear(a: V, b: V, eps: Double = 1e-6): Boolean {
    val aDist = a.distanceTo(zero)
    val bDist = b.distanceTo(zero)
    return abs(aDist) < eps || abs(bDist) < eps || abs(abs((a dot b) / (aDist * bDist)) - 1) < eps
}

fun <V : Vector> GeometrySpace<V>.isOrthogonal(a: V, b: V, eps: Double = 1e-6): Boolean =
    abs(a dot b) < eps
