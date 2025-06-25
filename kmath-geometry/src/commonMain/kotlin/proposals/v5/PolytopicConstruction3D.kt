/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v5


public interface PolytopicConstruction3D<out Vector, out Vertex, out Edge, out Polygon, out Polyhedron> {
    public val vertices: Set<Vertex>
    public val edges: Set<Edge>
    public val polygons: Set<Polygon>
    public val polyhedra: Set<Polyhedron>
    
    public val @UnsafeVariance Vertex.position: Vector
    
    public val @UnsafeVariance Edge.start: Vertex
    public val @UnsafeVariance Edge.end: Vertex
    
    public val @UnsafeVariance Polygon.vertices: Set<Vertex>
    public val @UnsafeVariance Polygon.edges: Set<Edge>
    
    public val @UnsafeVariance Polyhedron.vertices: Set<Vertex>
    public val @UnsafeVariance Polyhedron.edges: Set<Edge>
    public val @UnsafeVariance Polyhedron.faces: Set<Polygon>
}

public interface MutablePolytopicConstruction3D<Vector, Vertex, Edge, Polygon, Polyhedron> : PolytopicConstruction3D<Vector, Vertex, Edge, Polygon, Polyhedron> {
    public fun Vertex.bind(position: Vector)
    public fun Edge.bind(start: Vertex, end: Vertex)
    public fun Polygon.bind(vertices: Set<Vertex>, edges: Set<Edge>)
    public fun Polyhedron.bind(vertices: Set<Vertex>, edges: Set<Edge>, faces: Set<Polygon>)
    
    public fun Vertex.unbind()
    public fun Edge.unbind()
    public fun Polygon.unbind()
    public fun Polyhedron.unbind()
}