/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v5

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public fun <V: Any, Vertex, Polytope> GeometrySpace<V, *>.projectAlong(
    projectedPolytopicConstruction: PolytopicConstruction<V, Vertex, Polytope>,
    targetPolytopicConstruction: MutablePolytopicConstruction<V, Vertex, Polytope>,
    normal: V,
    base: V,
) {
    require(targetPolytopicConstruction.dimension >= projectedPolytopicConstruction.dimension - 1)
    projectedPolytopicConstruction.vertices.forEach { vertex ->
        val correspondingPolytope = with(projectedPolytopicConstruction) { vertex.asPolytope() }
        with(targetPolytopicConstruction) { vertex.bind(projectAlong(vertex.position, normal, base), correspondingPolytope) }
    }
    for (dim in 1 ..< projectedPolytopicConstruction.dimension)
        for (polytope in projectedPolytopicConstruction.polytopes[dim]) {
            @Suppress("UNCHECKED_CAST")
            val vertices = with(projectedPolytopicConstruction) { polytope.vertices }
            val faces = with(projectedPolytopicConstruction) { polytope.faces }
            with(targetPolytopicConstruction) { polytope.bind(dim, vertices, faces) }
        }
}

public fun <
    Vector: Any,
    Vertex
> GeometrySpace<Vector, *>.nearestVertexOfTo(
    polytopicConstruction: PolytopicConstruction<Vector, Vertex, *>,
    point: Vector,
): Vertex =
    with(polytopicConstruction) { vertices.minBy { it.position.distanceTo(point) } }

public fun <
    Vector: Any,
    Vertex,
> GeometrySpace<Vector, *>.removeNearestVertexOfTo(
    polytopicConstruction: MutablePolytopicConstruction<Vector, Vertex, *>,
    point: Vector,
) {
    val vertexToRemove = nearestVertexOfTo(polytopicConstruction, point)
    with(polytopicConstruction) { vertexToRemove.unbind() }
}