/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.Serializable
import space.kscience.kmath.operations.DoubleField.pow

/**
 * A line formed by [base] vector of start and a [direction] vector. Direction vector is not necessarily normalized,
 * but its length does not affect line properties
 */
@Serializable
public data class Line<out V : Vector>(val base: V, val direction: V)

public typealias Line2D = Line<DoubleVector2D>
public typealias Line3D = Line<DoubleVector3D>

/**
 * A directed line segment between [begin] and [end]
 */
@Serializable
public data class LineSegment<out V : Vector>(val begin: V, val end: V)

public fun <V : Vector> LineSegment<V>.line(algebra: GeometrySpace<V>): Line<V> = with(algebra) {
    Line(begin, end - begin)
}

public typealias LineSegment2D = LineSegment<DoubleVector2D>
public typealias LineSegment3D = LineSegment<DoubleVector3D>
