/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v1


public interface Vertex3D<
    out Vector
> {
    public val position: Vector
}

public interface Edge3D<
    out Vector,
    out VertexType: Vertex3D<Vector>,
> {
    public val start: VertexType
    public val end: VertexType
}

public interface Polygon3D<
    out Vector,
    out VertexType: Vertex3D<Vector>,
    out EdgeType: Edge3D<Vector, VertexType>
> {
    public val vertices: Set<VertexType>
    public val edges: Set<EdgeType>
}

public interface Polyhedron3D<
    out Vector,
    out VertexType: Vertex3D<Vector>,
    out EdgeType: Edge3D<Vector, VertexType>,
    out PolygonType: Polygon3D<Vector, VertexType, EdgeType>,
> {
    public val vertices: Set<VertexType>
    public val edges: Set<EdgeType>
    public val faces: Set<PolygonType>
}

public interface PolytopicConstructor3D<
    Vector,
    VertexType : Vertex3D<Vector>,
    EdgeType : Edge3D<Vector, VertexType>,
    PolygonType : Polygon3D<Vector, VertexType, EdgeType>,
    PolyhedronType : Polyhedron3D<Vector, VertexType, EdgeType, PolygonType>,
> {
    public fun newVertex(
        position: Vector,
    ): VertexType
    public fun newEdge(
        start: VertexType,
        end: VertexType,
    ): EdgeType
    public fun newPolygon(
        vertices: Set<VertexType>,
        edges: Set<EdgeType>,
    ): PolygonType
    public fun newPolyhedron(
        vertices: Set<VertexType>,
        edges: Set<EdgeType>,
        faces: Set<PolygonType>,
    ): PolyhedronType
}

public data class PolytopicConstruction3D<
    out Vector,
    out VertexType : Vertex3D<Vector>,
    out EdgeType : Edge3D<Vector, VertexType>,
    out PolygonType : Polygon3D<Vector, VertexType, EdgeType>,
    out PolyhedronType : Polyhedron3D<Vector, VertexType, EdgeType, PolygonType>,
>(
    public val vertices: Set<VertexType>,
    public val edges: Set<EdgeType>,
    public val polygons: Set<PolygonType>,
    public val polyhedra: Set<PolyhedronType>,
)