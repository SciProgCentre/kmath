/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v1


public interface PolytopicConstruction2D<
    out Vector,
    out VertexType: PolytopicConstruction2D.Vertex<Vector>,
    out EdgeType: PolytopicConstruction2D.Edge<Vector, VertexType>,
    out PolygonType: PolytopicConstruction2D.Polygon<Vector, VertexType, EdgeType>,
> {
    public val vertices: Set<VertexType>
    public val edges: Set<EdgeType>
    public val polygons: Set<PolygonType>
    
    public interface Vertex<
        out Vector,
    > {
        public val position: Vector
    }
    
    public interface Edge<
        out Vector,
        out VertexType: Vertex<Vector>,
    > {
        public val start: VertexType
        public val end: VertexType
    }
    
    public interface Polygon<
        out Vector,
        out VertexType: Vertex<Vector>,
        out EdgeType: Edge<Vector, VertexType>,
    > {
        public val vertices: Set<VertexType>
        public val edges: Set<EdgeType>
    }
}

public interface MutablePolytopicConstruction2D<
    Vector,
    VertexType: MutablePolytopicConstruction2D.Vertex<Vector>,
    EdgeType: MutablePolytopicConstruction2D.Edge<Vector, VertexType>,
    out PolygonType: MutablePolytopicConstruction2D.Polygon<Vector, VertexType, EdgeType>,
> : PolytopicConstruction2D<Vector, VertexType, EdgeType, PolygonType> {
    public fun addVertex(position: Vector): VertexType
    public fun addEdge(
        start: VertexType,
        end: VertexType,
    ): EdgeType
    public fun addPolygon(
        vertices: Set<VertexType>,
        edges: Set<EdgeType>,
    ): PolygonType
    
    public interface Vertex<
        out Vector,
    > : PolytopicConstruction2D.Vertex<Vector> {
        public fun remove()
    }
    
    public interface Edge<
        out Vector,
        out VertexType: Vertex<Vector>,
    > : PolytopicConstruction2D.Edge<Vector, VertexType> {
        public fun remove()
    }
    
    public interface Polygon<
        out Vector,
        out VertexType: Vertex<Vector>,
        out EdgeType: Edge<Vector, VertexType>,
    > : PolytopicConstruction2D.Polygon<Vector, VertexType, EdgeType> {
        public fun remove()
    }
}