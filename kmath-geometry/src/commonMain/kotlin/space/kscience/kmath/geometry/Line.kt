/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

/**
 * A line formed by [base] vector of start and a [direction] vector. Direction vector is not necessarily normalized,
 * but its length does not affect line properties
 */
public data class Line<out V : Vector>(val base: V, val direction: V)

public typealias Line2D = Line<DoubleVector2D>
public typealias Line3D = Line<DoubleVector3D>

/**
 * A directed line segment between [begin] and [end]
 */
public data class LineSegment<out V : Vector>(val begin: V, val end: V)
