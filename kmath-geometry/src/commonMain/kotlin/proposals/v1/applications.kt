/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v1

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


/**
 * Projects the [projectedPolytopicConstruction] along [normal] onto hyperplane normal to the [normal]
 * and going through [base] and adds projection results to [targetPolytopicConstruction].
 */
public fun <V: Any, PVertex, PPolytope, TVertex, TPolytope> GeometrySpace<V, *>.projectAlong(
    projectedPolytopicConstruction: PolytopicConstruction<V, PVertex, PPolytope>,
    targetPolytopicConstruction: MutablePolytopicConstruction<V, TVertex, TPolytope>,
    normal: V,
    base: V,
) {
    require(targetPolytopicConstruction.dimension >= projectedPolytopicConstruction.dimension - 1)
    with(projectedPolytopicConstruction) {
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
                        @Suppress("UNCHECKED_CAST")
                        val tVertices = pPolytope.vertices.mapTo(mutableSetOf()) { newVertices[it] as TVertex }
                        val tFaces: MutableList<Set<TPolytope>> = mutableListOf()
                        for (subdim in 0 ..< dim)
                            @Suppress("UNCHECKED_CAST")
                            tFaces += pPolytope.faces[subdim].mapTo(mutableSetOf()) { newPolytopes[subdim][it] as TPolytope }
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
}

/**
 * Projects the [polytopicConstruction3D] along [normal] onto hyperplane normal to the [normal] and going through [base].
 * Result is a new [AbstractPolytopicConstruction2D].
 */
public fun <
    V: Any,
    PVertex,
    PEdge,
    PPolygon,
> GeometrySpace<V, *>.projectAlongToAbstractPolytopicConstruction2D(
    polytopicConstruction3D: PolytopicConstruction3D<V, PVertex, PEdge, PPolygon, *>,
    normal: V,
    base: V,
): AbstractPolytopicConstruction2D<V> = with(polytopicConstruction3D) {
    AbstractPolytopicConstruction2D {
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
}

/**
 * Returns vertex of [polytopicConstruction] nearest to the [point].
 */
public fun <
    Vector: Any,
    Vertex
> GeometrySpace<Vector, *>.nearestVertexOfTo(
    polytopicConstruction: PolytopicConstruction<Vector, Vertex, *>,
    point: Vector,
): Vertex =
    with(polytopicConstruction) { vertices.minBy { it.position.distanceTo(point) } }

/**
 * Removes vertex of [polytopicConstruction] nearest to the [point].
 */
public fun <
    Vector: Any,
    Vertex
> GeometrySpace<Vector, *>.removeNearestVertexOfTo(
    polytopicConstruction: MutablePolytopicConstruction<Vector, Vertex, *>,
    point: Vector,
) {
    val vertexToRemove = nearestVertexOfTo(polytopicConstruction, point)
    with(polytopicConstruction) { vertexToRemove.remove() }
}