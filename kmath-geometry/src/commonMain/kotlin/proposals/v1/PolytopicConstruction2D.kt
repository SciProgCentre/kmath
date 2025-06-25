/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v1


/**
 * The 2D polytopic construction.
 */
public interface PolytopicConstruction2D<Vector, Vertex, Edge, Polygon> {
    /**
     * Contains all the vertices in the construction.
     */
    public val vertices: Set<Vertex>
    /**
     * Contains all the edges in the construction.
     */
    public val edges: Set<Edge>
    /**
     * Contains all the polygons in the construction.
     */
    public val polygons: Set<Polygon>
    
    /**
     * Returns position of the vertex.
     */
    public val Vertex.position: Vector
    
    /**
     * Returns start vertex of the edge. It is just some one vertex of the edge.
     */
    public val Edge.start: Vertex
    /**
     * Returns end vertex of the edge. It is just some one vertex of the edge.
     */
    public val Edge.end: Vertex
    
    /**
     * Returns all vertices of the polygon.
     */
    public val Polygon.vertices: Set<Vertex>
    /**
     * Returns all edges of the polytope.
     */
    public val Polygon.edges: Set<Edge>
}

/**
 * The 2D mutable polytopic construction.
 */
public interface MutablePolytopicConstruction2D<Vector, Vertex, Edge, Polygon> : PolytopicConstruction2D<Vector, Vertex, Edge, Polygon> {
    /**
     * Creates new vertex with provided position.
     */
    public fun addVertex(position: Vector): Vertex
    /**
     * Creates new edge with provided ends.
     */
    public fun addEdge(start: Vertex, end: Vertex): Edge
    /**
     * Creates new polygon with provided vertices and edges.
     */
    public fun addPolygon(vertices: Set<Vertex>, edges: Set<Edge>): Polygon
    
    /**
     * Removes the vertex.
     * Also removes all edges and polygons that contain the vertex.
     */
    public fun Vertex.remove()
    /**
     * Removes the edge.
     * Also removes all polygons that contain the edge.
     */
    public fun Edge.remove()
    /**
     * Removes the polygon.
     */
    public fun Polygon.remove()
}