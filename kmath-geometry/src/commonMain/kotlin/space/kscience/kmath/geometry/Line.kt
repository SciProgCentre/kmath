/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import space.kscience.kmath.geometry.euclidean2d.Float64Vector2D
import space.kscience.kmath.geometry.euclidean3d.Float64Vector3D

/**
 * A line formed by [start] vector of start and a [direction] vector. Direction vector is not necessarily normalized,
 * but its length does not affect line properties
 */
public interface Line<out V : Any> {
    public val start: V
    public val direction: V
}

@Serializable
@SerialName("Line")
private data class LineImpl<out V : Any>(override val start: V, override val direction: V) : Line<V>

public fun <V : Any> Line(base: V, direction: V): Line<V> = LineImpl(base, direction)

public typealias Line2D = Line<Float64Vector2D>
public typealias Line3D = Line<Float64Vector3D>

/**
 * A directed line segment between [begin] and [end]
 */
public interface LineSegment<out V : Any> {
    public val begin: V
    public val end: V
}

/**
 * Basic implementation for [LineSegment]
 */
@Serializable
@SerialName("LineSegment")
private data class LineSegmentImpl<out V : Any>(override val begin: V, override val end: V) : LineSegment<V>

public fun <V : Any> LineSegment(begin: V, end: V): LineSegment<V> = LineSegmentImpl(begin, end)

public fun <V : Any> LineSegment<V>.line(algebra: GeometrySpace<V, *>): Line<V> = with(algebra) {
    Line(begin, end - begin)
}

public typealias LineSegment2D = LineSegment<Float64Vector2D>
public typealias LineSegment3D = LineSegment<Float64Vector3D>
