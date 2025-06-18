/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.geometry.euclidean2d.PolytopicConstruction2D
import space.kscience.kmath.geometry.euclidean2d.build
import space.kscience.kmath.geometry.euclidean3d.PolytopicConstruction3D

//TODO move vector to receiver

/**
 * Project vector onto a line.
 * @param vector to project
 * @param line line to which vector should be projected
 */
public fun <V : Any> GeometrySpace<V, *>.projectToLine(vector: V, line: Line<V>): V = with(line) {
    start + (direction dot (vector - start)) / (direction dot direction) * direction
}

/**
 * Project vector onto a hyperplane, which is defined by a normal and base.
 * In 2D case it is the projection to a line, in 3d case it is the one to a plane.
 * @param vector to project
 * @param normal normal (perpendicular) vector to a hyper-plane to which vector should be projected
 * @param base point belonging to a hyper-plane to which vector should be projected
 */
public fun <V : Any> GeometrySpace<V, *>.projectAlong(vector: V, normal: V, base: V): V =
    vector + normal * ((base - vector) dot normal) / (normal dot normal)

public fun <V: Any> GeometrySpace<V, *>.projectAlong(
    polytopicConstruction3D: PolytopicConstruction3D<V>,
    normal: V,
    base: V,
): PolytopicConstruction2D<V> = PolytopicConstruction2D.build {
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