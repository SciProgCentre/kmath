/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.geometry.GeometrySpace.Companion.DEFAULT_PRECISION

/**
 * Float equality within given [precision]
 */
public fun Double.equalsFloat(other: Double, precision: Double = DEFAULT_PRECISION): Boolean =
    kotlin.math.abs(this - other) < precision

/**
 * Float equality within given [precision]
 */
public fun Double.equalsFloat(other: Float, precision: Double = DEFAULT_PRECISION): Boolean =
    kotlin.math.abs(this - other) < precision

/**
 * Vector equality within given [precision] (using [GeometrySpace.norm] provided by the space
 */
public fun <V : Vector> V.equalsVector(
    space: GeometrySpace<V>,
    other: V,
    precision: Double = DEFAULT_PRECISION,
): Boolean = with(space) {
    norm(this@equalsVector - other) < precision
}

/**
 * Vector equality using Euclidian L2 norm and given [precision]
 */
public fun Float64Vector2D.equalsVector(
    other: Float64Vector2D,
    precision: Double = DEFAULT_PRECISION,
): Boolean = equalsVector(Euclidean2DSpace, other, precision)

/**
 * Vector equality using Euclidian L2 norm and given [precision]
 */
public fun Float64Vector3D.equalsVector(
    other: Float64Vector3D,
    precision: Double = DEFAULT_PRECISION,
): Boolean = equalsVector(Euclidean3DSpace, other, precision)

/**
 * Line equality using [GeometrySpace.norm] provided by the [space] and given [precision]
 */
public fun <V : Vector> LineSegment<V>.equalsLine(
    space: GeometrySpace<V>,
    other: LineSegment<V>,
    precision: Double = DEFAULT_PRECISION,
): Boolean = begin.equalsVector(space, other.begin, precision) && end.equalsVector(space, other.end, precision)