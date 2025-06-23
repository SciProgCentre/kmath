/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v1

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * The polytope construction.
 */
public interface PolytopicConstruction<Vector, Vertex, Polytope> {
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
     * Actually, it is the first set in the [polytopes] list but with right type.
     */
    public val vertices: Set<Vertex>
    /**
     * Dimension of the polytope in the construction.
     */
    public val Polytope.dimension: Int
    /**
     * Contains all the faces of [this] polytope.
     * Each index `i` corresponds to set of all faces of dimension `i`.
     * Its size is `dimension` (where `dimension` is the polytope's dimension).
     */
    public val Polytope.faces: List<Set<Polytope>>
    /**
     * Contains all the vertices in [this] polytope.
     * Actually, it is the first set in the [faces] list but with right type.
     */
    public val Polytope.vertices: Set<Vertex>
    /**
     * Position of [this] vertex in Euclidean space.
     */
    public val Vertex.position: Vector
    /**
     * Returns 0-dimensional polytope associated with the vertex.
     */
    public fun Vertex.asPolytope(): Polytope
}

/**
 * The mutable polytope construction.
 */
public interface MutablePolytopicConstruction<Vector, Vertex, Polytope> : PolytopicConstruction<Vector, Vertex, Polytope> {
    /**
     * Creates new vertex with provided position.
     */
    public fun addVertex(position: Vector): Vertex
    /**
     * Creates new polytope by its dimension, its faces, and its vertices.
     */
    public fun addPolytope(
        dimension: Int,
        vertices: Set<Vertex>,
        faces: List<Set<Polytope>>,
    ): Polytope
    /**
     * Removes the vertex.
     */
    public fun Vertex.remove()
    /**
     * Removes the polytope.
     */
    public fun Polytope.remove()
}

/**
 * Abstract vertex that holds only an identifier and its position.
 */
public class AbstractVertex<Vector>(
    public val id: Uuid = Uuid.random(),
    public val position: Vector
) {
    override fun toString(): String = "AbstractVertex#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Abstract polytope that holds an identifier.
 */
public open class AbstractPolytope(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolytope#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolytope) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public typealias AbstractPolytopicConstruction<Vector> = PolytopicConstruction<Vector, AbstractVertex<Vector>, AbstractPolytope>
public typealias MutableAbstractPolytopicConstruction<Vector> = MutablePolytopicConstruction<Vector, AbstractVertex<Vector>, AbstractPolytope>

/**
 * Builder for [AbstractPolytopicConstruction] via [MutableAbstractPolytopicConstruction] API.
 */
public inline fun <Vector> AbstractPolytopicConstruction(
    dimension: Int,
    block: MutableAbstractPolytopicConstruction<Vector>.() -> Unit
): AbstractPolytopicConstruction<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    TODO()
}