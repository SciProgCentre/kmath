/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.Serializable

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

public fun equalLineSegments(line1: LineSegment<DoubleVector2D>, line2: LineSegment<DoubleVector2D>): Boolean {
    val maxFloatDelta = 0.000001
    return line1.begin.x.equalFloat(line2.begin.x) && line1.begin.y.equalFloat(line2.begin.y) &&
            line1.end.x.equalFloat(line2.end.x) && line1.end.y.equalFloat(line2.end.y)
//    return line1.begin == line2.begin && line1.end == line2.end
}

public fun Double.equalFloat(other: Double, maxFloatDelta: Double = 0.000001):
        Boolean = kotlin.math.abs(this - other) < maxFloatDelta

public typealias LineSegment2D = LineSegment<DoubleVector2D>
public typealias LineSegment3D = LineSegment<DoubleVector3D>
