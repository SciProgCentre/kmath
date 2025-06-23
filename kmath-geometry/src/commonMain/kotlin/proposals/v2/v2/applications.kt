/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v2

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public fun <V: Any> GeometrySpace<V, *>.projectAlong(
    polytopicConstruction3D: PolytopicConstruction3D<V>,
    normal: V,
    base: V,
): PolytopicConstruction2D<V> = PolytopicConstruction2D {
    val newVertices = polytopicConstruction3D.vertices.associateWith { vertex ->
        addVertex(projectAlong(vertex.position, normal, base))
    }
    val newEdges = polytopicConstruction3D.edges.associateWith { edge ->
        addEdge(newVertices[edge.start]!!, newVertices[edge.end]!!)
    }
    polytopicConstruction3D.polygons.forEach { polygon ->
        addPolygon(
            vertices = polygon.vertices.mapTo(mutableSetOf()) { newVertices[it]!! },
            edges = polygon.edges.mapTo(mutableSetOf()) { newEdges[it]!! }
        )
    }
}