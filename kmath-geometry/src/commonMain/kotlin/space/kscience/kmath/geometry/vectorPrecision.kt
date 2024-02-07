/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.geometry.euclidean2d.Float64Space2D
import space.kscience.kmath.geometry.euclidean2d.Float64Vector2D
import space.kscience.kmath.geometry.euclidean3d.Float64Space3D
import space.kscience.kmath.geometry.euclidean3d.Float64Vector3D


/**
 * Vector equality within given [precision] (using [GeometrySpace.norm] provided by the space
 */
public fun <V : Any, D : Comparable<D>> V.equalsVector(
    space: GeometrySpace<V, D>,
    other: V,
    precision: D = space.defaultPrecision,
): Boolean = with(space) {
    norm(this@equalsVector - other) < precision
}

/**
 * Vector equality using Euclidian L2 norm and given [precision]
 */
public fun Float64Vector2D.equalsVector(
    other: Float64Vector2D,
    precision: Double = Float64Space2D.defaultPrecision,
): Boolean = equalsVector(Float64Space2D, other, precision)

/**
 * Vector equality using Euclidean L2 norm and given [precision]
 */
public fun Float64Vector3D.equalsVector(
    other: Float64Vector3D,
    precision: Double = Float64Space3D.defaultPrecision,
): Boolean = equalsVector(Float64Space3D, other, precision)

/**
 * Line equality using [GeometrySpace.norm] provided by the [space] and given [precision]
 */
public fun <V : Any, D : Comparable<D>> LineSegment<V>.equalsLine(
    space: GeometrySpace<V, D>,
    other: LineSegment<V>,
    precision: D = space.defaultPrecision,
): Boolean = begin.equalsVector(space, other.begin, precision) && end.equalsVector(space, other.end, precision)