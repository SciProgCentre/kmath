/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v1


/**
 * The polytopic construction.
 */
public interface PolytopicConstruction<out Vector, out Vertex, out Polytope> {
    /**
     * Dimension of the polytopic construction.
     */
    public val dimension: Int
    /**
     * Contains all the polytopes in the construction.
     * Each index `i` corresponds to set of all polytopes of dimension `i`.
     * Its size is [dimension]` + 1`.
     */
    public val polytopes: List<Set<Polytope>>
    /**
     * Contains all the vertices in the construction.
     * Actually, it corresponds to the first set in the [polytopes] list via [asPolytope] method.
     */
    public val vertices: Set<Vertex>
    /**
     * Dimension of the polytope in the construction.
     */
    public val @UnsafeVariance Polytope.dimension: Int
    /**
     * Contains all the faces of [this] polytope.
     * Each index `i` corresponds to set of all faces of dimension `i`.
     * Its size is `dimension` (where `dimension` is the polytope's dimension).
     */
    public val @UnsafeVariance Polytope.faces: List<Set<Polytope>>
    /**
     * Contains all the vertices in [this] polytope.
     * Actually, it is the first set in the [faces] list but with right type.
     */
    public val @UnsafeVariance Polytope.vertices: Set<Vertex>
    /**
     * Position of [this] vertex in Euclidean space.
     */
    public val @UnsafeVariance Vertex.position: Vector
    /**
     * Returns 0-dimensional polytope associated with the vertex.
     */
    public fun @UnsafeVariance Vertex.asPolytope(): Polytope
}

/**
 * The mutable polytopic construction.
 */
public interface MutablePolytopicConstruction<Vector, out Vertex, out Polytope> : PolytopicConstruction<Vector, Vertex, Polytope> {
    /**
     * Creates new vertex with provided position.
     */
    public fun addVertex(position: Vector): Vertex
    /**
     * Creates new polytope by its dimension, its faces, and its vertices.
     */
    public fun addPolytope(
        dimension: Int,
        vertices: Set<@UnsafeVariance Vertex>,
        faces: List<Set<@UnsafeVariance Polytope>>,
    ): Polytope
    /**
     * Removes the vertex.
     * Also removes all polytopes that contain the vertex.
     */
    public fun @UnsafeVariance Vertex.remove()
    /**
     * Removes the polytope.
     * Also removes all polytopes that contain the polytope.
     */
    public fun @UnsafeVariance Polytope.remove()
}