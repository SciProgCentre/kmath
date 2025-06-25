/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v1


/**
 * Represents the polytopic construction.
 */
public interface PolytopicConstruction<
    out Vector,
    out VertexType: PolytopicConstruction.Vertex<Vector, VertexType, PolytopeType>,
    out PolytopeType: PolytopicConstruction.Polytope<Vector, VertexType, PolytopeType>,
> {
    /**
     * Dimension of the polytopic construction.
     */
    public val dimension: Int
    /**
     * Contains all the vertices in the construction.
     * Actually, it corresponds to the first set in the [polytopes] list via [Vertex.asPolytope] method.
     */
    public val vertices: Set<VertexType>
    /**
     * Contains all the polytopes in the construction.
     * Each index `i` corresponds to set of all polytopes of dimension `i`.
     * Its size is [dimension]` + 1`.
     */
    public val polytopes: List<Set<PolytopeType>>
    
    /**
     * Represents vertex of [PolytopicConstruction].
     */
    public interface Vertex<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > {
        /**
         * Returns position of the vertex.
         */
        public val position: Vector
        /**
         * Returns polytope corresponding to the vertex.
         */
        public fun asPolytope(): PolytopeType
    }
    
    /**
     * Represents polytope of [PolytopicConstruction].
     */
    public interface Polytope<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > {
        /**
         * Contains all the vertices in the polytope.
         * Actually, it corresponds to the first set in the [faces] list via [Vertex.asPolytope] method.
         */
        public val dimension: Int
        /**
         * Contains all vertices of the polytope.
         */
        public val vertices: Set<VertexType>
        /**
         * Contains all the faces in the polytope.
         * Each index `i` corresponds to set of all faces of dimension `i`.
         * Its size is [dimension]` + 1`.
         */
        public val faces: List<Set<PolytopeType>>
    }
}

/**
 * Represents the mutable polytopic construction.
 */
public interface MutablePolytopicConstruction<
    Vector,
    VertexType: MutablePolytopicConstruction.Vertex<Vector, VertexType, PolytopeType>,
    PolytopeType: MutablePolytopicConstruction.Polytope<Vector, VertexType, PolytopeType>,
> : PolytopicConstruction<Vector, VertexType, PolytopeType> {
    /**
     * Creates new vertex with provided position.
     */
    public fun addVertex(position: Vector): VertexType
    /**
     * Creates new polytope by its dimension, its faces, and its vertices.
     */
    public fun addPolytope(
        dimension: Int,
        vertices: Set<VertexType>,
        faces: List<Set<PolytopeType>>,
    ): PolytopeType
    
    /**
     * Represents mutable vertex of [MutablePolytopicConstruction].
     */
    public interface Vertex<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > : PolytopicConstruction.Vertex<Vector, VertexType, PolytopeType> {
        /**
         * Removes the vertex.
         * Also removes all polytopes that contain the vertex.
         */
        public fun remove()
    }
    
    /**
     * Represents mutable polytope of [MutablePolytopicConstruction].
     */
    public interface Polytope<
        out Vector,
        out VertexType: Vertex<Vector, VertexType, PolytopeType>,
        out PolytopeType: Polytope<Vector, VertexType, PolytopeType>,
    > : PolytopicConstruction.Polytope<Vector, VertexType, PolytopeType> {
        /**
         * Removes the polytope.
         * Also removes all polytopes that contain the polytope.
         */
        public fun remove()
    }
}