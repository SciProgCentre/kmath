/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.invoke

/**
 * Get a line, containing this [LineSegment]
 */
context(group: Group<V>)
public val <V : Any> LineSegment<V>.line: Line<V>
    get() = Line(begin, group { end - begin })

/**
 * Get a length of a line segment
 */
context(space: GeometrySpace<V, D>)
public val <V : Any, D : Comparable<D>> LineSegment<V>.length: D
    get() = space { norm(end - begin) }