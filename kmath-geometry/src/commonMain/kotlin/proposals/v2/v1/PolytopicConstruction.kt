/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v1


public interface PolytopicConstruction<
    out Vector,
    out VertexType: PolytopicConstruction.Vertex<Vector, VertexType, PolytopeType>,
    out PolytopeType: PolytopicConstruction.Polytope<Vector, VertexType, PolytopeType>,
> {
    public val dimension: Int
    public val vertices: Set<VertexType>
    public val polytopes: List<Set<PolytopeType>>
    
    public interface Vertex<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > {
        public val position: Vector
        public fun asPolytope(): PolytopeType
    }
    
    public interface Polytope<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > {
        public val dimension: Int
        public val vertices: Set<VertexType>
        public val faces: List<Set<PolytopeType>>
    }
}

public interface MutablePolytopicConstruction<
    Vector,
    VertexType: MutablePolytopicConstruction.Vertex<Vector, VertexType, PolytopeType>,
    PolytopeType: MutablePolytopicConstruction.Polytope<Vector, VertexType, PolytopeType>,
> : PolytopicConstruction<Vector, VertexType, PolytopeType> {
    public fun addVertex(position: Vector): VertexType
    public fun addPolytope(
        dimension: Int,
        vertices: Set<VertexType>,
        faces: List<Set<PolytopeType>>,
    ): PolytopeType
    
    public interface Vertex<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > : PolytopicConstruction.Vertex<Vector, VertexType, PolytopeType> {
        public fun remove()
    }
    
    public interface Polytope<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > : PolytopicConstruction.Polytope<Vector, VertexType, PolytopeType> {
        public fun remove()
    }
}