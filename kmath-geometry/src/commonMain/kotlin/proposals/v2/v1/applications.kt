/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v1

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public fun <
    V: Any,
    PVertex : PolytopicConstruction.Vertex<V, PVertex, PPolytope>,
    PPolytope : PolytopicConstruction.Polytope<V, PVertex, PPolytope>,
    TVertex : MutablePolytopicConstruction.Vertex<V, TVertex, TPolytope>,
    TPolytope : MutablePolytopicConstruction.Polytope<V, TVertex, TPolytope>
> GeometrySpace<V, *>.projectAlong(
    projectedPolytopicConstruction: PolytopicConstruction<V, PVertex, PPolytope>,
    targetPolytopicConstruction: MutablePolytopicConstruction<V, TVertex, TPolytope>,
    normal: V,
    base: V,
) {
    require(targetPolytopicConstruction.dimension >= projectedPolytopicConstruction.dimension - 1)
    with(targetPolytopicConstruction) {
        val newVertices: Map<PVertex, TVertex> = projectedPolytopicConstruction.vertices.associateWith { vertex ->
            addVertex(projectAlong(vertex.position, normal, base))
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
                        addPolytope(
                            dimension = dim,
                            vertices = tVertices,
                            faces = tFaces,
                        )
                    )
                }
            }
    }
}

public fun <
    V: Any,
    PVertex : PolytopicConstruction3D.Vertex<V>,
    PEdge : PolytopicConstruction3D.Edge<V, PVertex>,
    PPolygon : PolytopicConstruction3D.Polygon<V, PVertex, PEdge>,
> GeometrySpace<V, *>.projectAlongToAbstractPolytopicConstruction2D(
    polytopicConstruction3D: PolytopicConstruction3D<V, PVertex, PEdge, PPolygon, *>,
    normal: V,
    base: V,
): AbstractPolytopicConstruction2D<V> = AbstractPolytopicConstruction2D<V>().apply {
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
    VertexType: PolytopicConstruction.Vertex<Vector, VertexType, *>
> GeometrySpace<Vector, *>.nearestVertexOfTo(
    polytopicConstruction: PolytopicConstruction<Vector, VertexType, *>,
    point: Vector,
): VertexType =
    polytopicConstruction.vertices.minBy { it.position.distanceTo(point) }

public fun <
    Vector: Any,
    VertexType: MutablePolytopicConstruction.Vertex<Vector, VertexType, *>
> GeometrySpace<Vector, *>.removeNearestVertexOfTo(
    polytopicConstruction: MutablePolytopicConstruction<Vector, VertexType, *>,
    point: Vector,
) {
    nearestVertexOfTo(polytopicConstruction, point).remove()
}