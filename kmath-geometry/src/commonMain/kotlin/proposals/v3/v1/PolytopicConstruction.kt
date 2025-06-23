/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v1


public interface Vertex<
    out Vector,
    out VertexType: Vertex<Vector, VertexType, PolytopeType>,
    out PolytopeType: Polytope<Vector, VertexType, PolytopeType>
> {
    public val position: Vector
    
    public fun asPolytope(): PolytopeType
}

public interface Polytope<
    out Vector,
    out VertexType: Vertex<Vector, VertexType, PolytopeType>,
    out PolytopeType: Polytope<Vector, VertexType, PolytopeType>
> {
    public val dimension: Int
    public val vertices: Set<VertexType>
    public val faces: List<Set<PolytopeType>>
}