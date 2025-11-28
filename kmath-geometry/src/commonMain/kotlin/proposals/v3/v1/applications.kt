/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v1

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public fun <
    V: Any,
    PVertex : Vertex<V, PVertex, PPolytope>,
    PPolytope : Polytope<V, PVertex, PPolytope>,
    TVertex : Vertex<V, TVertex, TPolytope>,
    TPolytope : Polytope<V, TVertex, TPolytope>
> GeometrySpace<V, *>.projectAlong(
    projectedPolytopicConstruction: PolytopicConstruction<V, PVertex, PPolytope>,
    targetPolytopicConstructor: PolytopicConstructor<V, TVertex, TPolytope>,
    normal: V,
    base: V,
): PolytopicConstruction<V, TVertex, TPolytope> {
    val newVertices: Map<PVertex, TVertex> = projectedPolytopicConstruction.vertices.associateWith { vertex ->
        targetPolytopicConstructor.newVertex(projectAlong(vertex.position, normal, base))
    }
    val newPolytopes: MutableList<Map<PPolytope, TPolytope>> =
        mutableListOf(
            buildMap {
                for ((pVertex, tVertex) in newVertices) put(pVertex.asPolytope(), tVertex.asPolytope())
            }
        )
    for (dim in 1 ..< projectedPolytopicConstruction.dimension)
        newPolytopes += buildMap {
            for (pPolytope in projectedPolytopicConstruction.polytopes[dim]) {
                val tVertices = pPolytope.vertices.mapTo(mutableSetOf()) { newVertices[it]!! }
                val tFaces: MutableList<Set<TPolytope>> = mutableListOf()
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

public fun <
    V: Any,
    PVertex : Vertex3D<V>,
    PEdge : Edge3D<V, PVertex>,
    PPolygon : Polygon3D<V, PVertex, PEdge>,
> GeometrySpace<V, *>.projectAlongToAbstractPolytopicConstruction2D(
    polytopicConstruction3D: PolytopicConstruction3D<V, PVertex, PEdge, PPolygon, *>,
    normal: V,
    base: V,
): AbstractPolytopicConstruction2D<V> {
    val constructor = AbstractPolytopicConstructor2D<V>()
    val newVertices = polytopicConstruction3D.vertices.associateWith { vertex ->
        constructor.newVertex(projectAlong(vertex.position, normal, base))
    }
    val newEdges = polytopicConstruction3D.edges.associateWith { edge ->
        constructor.newEdge(newVertices[edge.start]!!, newVertices[edge.end]!!)
    }
    val newPolygons = polytopicConstruction3D.polygons.associateWith { polygon ->
        constructor.newPolygon(
            vertices = polygon.vertices.mapTo(mutableSetOf()) { newVertices[it]!! },
            edges = polygon.edges.mapTo(mutableSetOf()) { newEdges[it]!! }
        )
    }
    return AbstractPolytopicConstruction2D(
        vertices = newVertices.values.toSet(),
        edges = newEdges.values.toSet(),
        polygons = newPolygons.values.toSet(),
    )
}

public fun <
    Vector: Any,
    VertexType: Vertex<Vector, VertexType, *>
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