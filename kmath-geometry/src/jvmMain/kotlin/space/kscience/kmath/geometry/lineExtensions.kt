/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.Line
import space.kscience.kmath.geometry.LineSegment
import space.kscience.kmath.geometry.Vector
import space.kscience.kmath.operations.Group

/**
 * Get a line, containing this [LineSegment]
 */
context(Group<V>) public val <V : Vector> LineSegment<V>.line: Line<V> get() = Line(begin, end - begin)

/**
 * Get a length of a line segment
 */
context(GeometrySpace<V>) public val <V : Vector> LineSegment<V>.length: Double get() = norm(end - begin)