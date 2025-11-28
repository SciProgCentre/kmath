/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v2

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public fun <V: Any> GeometrySpace<V, *>.projectAlong(
    projectedPolytopicConstruction: PolytopicConstruction<V>,
    targetPolytopicConstructor: PolytopicConstructor<V>,
    normal: V,
    base: V,
) : PolytopicConstruction<V> {
    val newVertices: Map<Vertex<V>, Vertex<V>> =
        projectedPolytopicConstruction.vertices.associateWith { vertex ->
            targetPolytopicConstructor.newVertex(projectAlong(vertex.position, normal, base))
        }
    val newPolytopes: MutableList<Map<Polytope<V>, Polytope<V>>> =
        mutableListOf(
            buildMap {
                for ((pVertex, tVertex) in newVertices) put(pVertex.asPolytope(), tVertex.asPolytope())
            }
        )
    for (dim in 1 ..< projectedPolytopicConstruction.dimension)
        newPolytopes += buildMap {
            for (pPolytope in projectedPolytopicConstruction.polytopes[dim]) {
                val tVertices = pPolytope.vertices.mapTo(mutableSetOf()) { newVertices[it]!! }
                val tFaces: MutableList<Set<Polytope<V>>> = mutableListOf()
                for (subdim in 0 ..< dim)
                    tFaces += pPolytope.faces[subdim].mapTo(mutableSetOf()) { newPolytopes[subdim][it]!! }
                put(
                    pPolytope,
                    targetPolytopicConstructor.newPolytope(
                        dimension = dim,
                        vertices = tVertices,
                        faces = tFaces,
                    )
                )
            }
        }
    
    return PolytopicConstruction(
        dimension = projectedPolytopicConstruction.dimension - 1,
        vertices = newVertices.values.toSet(),
        polytopes = newPolytopes.map { it.values.toSet() },
    )
}

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