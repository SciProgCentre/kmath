/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v2

import space.kscience.kmath.geometry.GeometrySpace


public fun <
    Vector: Any,
    VertexType: Vertex<Vector>
> GeometrySpace<Vector, *>.nearestVertexOfTo(
    polytopeVertices: Collection<VertexType>,
    point: Vector,
): VertexType =
    polytopeVertices.minBy { it.position.distanceTo(point) }

//public fun <
//    Vector: Any,
//    VertexType: Vertex<Vector, VertexType, *>
//> GeometrySpace<Vector, *>.removeNearestVertexOfTo(
//    polytopeVertices: Collection<VertexType>,
//    point: Vector,
//) {
//    nearestVertexOfTo(polytopeVertices, point).remove()
//}