/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A line formed by [start] vector of start and a [direction] vector. Direction vector is not necessarily normalized,
 * but its length does not affect line properties
 */
public interface Line<out V : Vector> {
    public val start: V
    public val direction: V
}

@Serializable
@SerialName("Line")
private data class LineImpl<out V : Vector>(override val start: V, override val direction: V): Line<V>

public fun <V : Vector> Line(base: V, direction: V): Line<V> = LineImpl(base, direction)

public typealias Line2D = Line<DoubleVector2D>
public typealias Line3D = Line<DoubleVector3D>

/**
 * A directed line segment between [begin] and [end]
 */
public interface LineSegment<out V : Vector> {
    public val begin: V
    public val end: V
}

/**
 * Basic implementation for [LineSegment]
 */
@Serializable
@SerialName("LineSegment")
private data class LineSegmentImpl<out V : Vector>(override val begin: V, override val end: V) : LineSegment<V>

public fun <V : Vector> LineSegment(begin: V, end: V): LineSegment<V> = LineSegmentImpl(begin, end)

public fun <V : Vector> LineSegment<V>.line(algebra: GeometrySpace<V>): Line<V> = with(algebra) {
    Line(begin, end - begin)
}

public typealias LineSegment2D = LineSegment<DoubleVector2D>
public typealias LineSegment3D = LineSegment<DoubleVector3D>
