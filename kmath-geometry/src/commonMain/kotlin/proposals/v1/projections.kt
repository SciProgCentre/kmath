/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v1

import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public fun <V: Any, PVertex : PPolytope, PPolytope, TVertex : TPolytope, TPolytope> GeometrySpace<V, *>.projectAlong(
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
            @Suppress("UNCHECKED_CAST")
            val newPolytopes: MutableList<Map<PPolytope, TPolytope>> = mutableListOf(newVertices as Map<PPolytope, TVertex>)
            for (dim in 1 ..< projectedPolytopicConstruction.dimension)
                newPolytopes += buildMap {
                    for (pPolytope in projectedPolytopicConstruction.polytopes[dim]) {
                        @Suppress("UNCHECKED_CAST")
                        val tVertices = pPolytope.vertices.mapTo(mutableSetOf()) { newVertices[it] as TVertex }
                        val tFaces: MutableList<Set<TPolytope>> = mutableListOf(tVertices)
                        for (subdim in 1 ..< dim)
                            @Suppress("UNCHECKED_CAST")
                            tFaces += pPolytope.faces[subdim].mapTo(mutableSetOf()) { newPolytopes[subdim] as TPolytope }
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