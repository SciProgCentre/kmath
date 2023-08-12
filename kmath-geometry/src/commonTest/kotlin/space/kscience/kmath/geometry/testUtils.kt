/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.geometry.euclidean2d.DoubleVector2D
import space.kscience.kmath.geometry.euclidean3d.DoubleVector3D
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

fun assertVectorEquals(expected: DoubleVector2D, actual: DoubleVector2D, absoluteTolerance: Double = 1e-6) {
    assertEquals(expected.x, actual.x, absoluteTolerance)
    assertEquals(expected.y, actual.y, absoluteTolerance)
}

fun assertVectorEquals(expected: DoubleVector3D, actual: DoubleVector3D, absoluteTolerance: Double = 1e-6) {
    assertEquals(expected.x, actual.x, absoluteTolerance)
    assertEquals(expected.y, actual.y, absoluteTolerance)
    assertEquals(expected.z, actual.z, absoluteTolerance)
}

fun <V : Any> GeometrySpace<V, Double>.isCollinear(a: V, b: V, absoluteTolerance: Double = defaultPrecision): Boolean {
    val aDist = a.distanceTo(zero)
    val bDist = b.distanceTo(zero)
    return aDist < absoluteTolerance || bDist < absoluteTolerance || abs(abs((a dot b) / (aDist * bDist)) - 1) < absoluteTolerance
}

fun <V : Any> GeometrySpace<V,*>.isOrthogonal(a: V, b: V, absoluteTolerance: Double = 1e-6): Boolean =
    abs(a dot b) < absoluteTolerance

fun Double.equalFloat(other: Double, maxFloatDelta: Double = 0.000001):
        Boolean = kotlin.math.abs(this - other) < maxFloatDelta