/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v1


public interface PolytopicConstruction3D<
    out Vector,
    out VertexType: PolytopicConstruction3D.Vertex<Vector>,
    out EdgeType: PolytopicConstruction3D.Edge<Vector, VertexType>,
    out PolygonType: PolytopicConstruction3D.Polygon<Vector, VertexType, EdgeType>,
    out PolyhedronType: PolytopicConstruction3D.Polyhedron<Vector, VertexType, EdgeType, PolygonType>,
> {
    public val vertices: Set<VertexType>
    public val edges: Set<EdgeType>
    public val polygons: Set<PolygonType>
    public val polyhedra: Set<PolyhedronType>
    
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
    
    public interface Polyhedron<
        out Vector,
        out VertexType: Vertex<Vector>,
        out EdgeType: Edge<Vector, VertexType>,
        out PolygonType: Polygon<Vector, VertexType, EdgeType>,
    > {
        public val vertices: Set<VertexType>
        public val edges: Set<EdgeType>
        public val faces: Set<PolygonType>
    }
}

public interface MutablePolytopicConstruction3D<
    Vector,
    VertexType: MutablePolytopicConstruction3D.Vertex<Vector>,
    EdgeType: MutablePolytopicConstruction3D.Edge<Vector, VertexType>,
    PolygonType: MutablePolytopicConstruction3D.Polygon<Vector, VertexType, EdgeType>,
    out PolyhedronType: MutablePolytopicConstruction3D.Polyhedron<Vector, VertexType, EdgeType, PolygonType>,
> : PolytopicConstruction3D<Vector, VertexType, EdgeType, PolygonType, PolyhedronType> {
    public fun addVertex(position: Vector): VertexType
    public fun addEdge(
        start: VertexType,
        end: VertexType,
    ): EdgeType
    public fun addPolygon(
        vertices: Set<VertexType>,
        edges: Set<EdgeType>,
    ): PolygonType
    public fun addPolyhedron(
        vertices: Set<VertexType>,
        edges: Set<EdgeType>,
        faces: Set<PolygonType>,
    ): PolyhedronType
    
    public interface Vertex<
        out Vector,
    > : PolytopicConstruction3D.Vertex<Vector> {
        public fun remove()
    }
    
    public interface Edge<
        out Vector,
        out VertexType: Vertex<Vector>,
    > : PolytopicConstruction3D.Edge<Vector, VertexType> {
        public fun remove()
    }
    
    public interface Polygon<
        out Vector,
        out VertexType: Vertex<Vector>,
        out EdgeType: Edge<Vector, VertexType>,
    > : PolytopicConstruction3D.Polygon<Vector, VertexType, EdgeType> {
        public fun remove()
    }
    
    public interface Polyhedron<
        out Vector,
        out VertexType: Vertex<Vector>,
        out EdgeType: Edge<Vector, VertexType>,
        out PolygonType: Polygon<Vector, VertexType, EdgeType>,
    > : PolytopicConstruction3D.Polyhedron<Vector, VertexType, EdgeType, PolygonType> {
        public fun remove()
    }
}