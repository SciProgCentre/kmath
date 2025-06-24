/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v5


public interface PolytopicConstruction3D<Vector, Vertex, Edge, Polygon, Polyhedron> {
    public val vertices: Set<Vertex>
    public val edges: Set<Edge>
    public val polygons: Set<Polygon>
    public val polyhedra: Set<Polyhedron>
    
    public val Vertex.position: Vector
    
    public val Edge.start: Vertex
    public val Edge.end: Vertex
    
    public val Polygon.vertices: Set<Vertex>
    public val Polygon.edges: Set<Edge>
    
    public val Polyhedron.vertices: Set<Vertex>
    public val Polyhedron.edges: Set<Edge>
    public val Polyhedron.faces: Set<Polygon>
}

public interface MutablePolytopicConstruction3D<Vector, Vertex, Edge, Polygon, Polyhedron> : PolytopicConstruction3D<Vector, Vertex, Edge, Polygon, Polyhedron> {
    public fun addVertex(position: Vector): Vertex
    public fun addEdge(start: Vertex, end: Vertex): Edge
    public fun addPolygon(vertices: Set<Vertex>, edges: Set<Edge>): Polygon
    public fun addPolyhedron(vertices: Set<Vertex>, edges: Set<Edge>, faces: Set<Polygon>): Polyhedron
    
    public fun Vertex.remove()
    public fun Edge.remove()
    public fun Polygon.remove()
    public fun Polyhedron.remove()
}