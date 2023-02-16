/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

internal const val defaultPrecision = 1e-6

public fun Double.equalsFloat(other: Double, precision: Double = defaultPrecision): Boolean =
    kotlin.math.abs(this - other) < precision

public fun Double.equalsFloat(other: Float, precision: Double = defaultPrecision): Boolean =
    kotlin.math.abs(this - other) < precision

public fun <V : Vector> V.equalsVector(
    space: GeometrySpace<V>,
    other: V,
    precision: Double = defaultPrecision,
): Boolean = with(space) {
    norm(this@equalsVector - other) < precision
}

public fun Float64Vector2D.equalsVector(
    other: Float64Vector2D,
    precision: Double = defaultPrecision,
): Boolean = equalsVector(Euclidean2DSpace, other, precision)

public fun Float64Vector3D.equalsVector(
    other: Float64Vector3D,
    precision: Double = defaultPrecision,
): Boolean = equalsVector(Euclidean3DSpace, other, precision)

public fun <V : Vector> LineSegment<V>.equalsLine(
    space: GeometrySpace<V>,
    other: LineSegment<V>,
    precision: Double = defaultPrecision,
): Boolean = begin.equalsVector(space, other.begin, precision) && end.equalsVector(space, other.end, precision)