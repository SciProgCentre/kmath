/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v2.v1

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractVertex<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction<Vector>,
    override val position: Vector
) : MutablePolytopicConstruction.Vertex<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>> {
    public val id: Uuid = Uuid.random()
    
    private val correspondingPolytope = AbstractPolytope(
        construction = construction,
        correspondingVertex = this,
        dimension = 0,
        vertices = setOf(this),
        faces = emptyList(),
    )
    override fun asPolytope(): AbstractPolytope<Vector> = correspondingPolytope
    override fun remove() {
        construction._polytopes.forEach { polytopes -> polytopes.removeAll { this in it.vertices } }
        construction._vertices.remove(this)
    }
    
    override fun toString(): String = "AbstractVertex#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolytope<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction<Vector>,
    private val correspondingVertex: AbstractVertex<Vector>?,
    override val dimension: Int,
    override val vertices: Set<AbstractVertex<Vector>>,
    override val faces: List<Set<AbstractPolytope<Vector>>>,
) : MutablePolytopicConstruction.Polytope<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._polytopes[dimension].remove(this)
        for (i in dimension + 1 .. construction.dimension)
            construction._polytopes[i].removeAll { this in it.faces[dimension] }
        correspondingVertex?.also { construction._vertices.remove(it) }
    }
    
    override fun toString(): String = "AbstractPolytope#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolytope<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolytopicConstruction<Vector>(
    override val dimension: Int,
) : MutablePolytopicConstruction<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>> {
    internal val _vertices: MutableSet<AbstractVertex<Vector>> = mutableSetOf()
    override val vertices: Set<AbstractVertex<Vector>> get() = _vertices
    
    internal val _polytopes: List<MutableSet<AbstractPolytope<Vector>>> = List(dimension + 1) { mutableSetOf() }
    override val polytopes: List<Set<AbstractPolytope<Vector>>> get() = _polytopes
    
    override fun addVertex(position: Vector): AbstractVertex<Vector> {
        val vertex = AbstractVertex(
            construction = this,
            position = position,
        )
        _vertices.add(vertex)
        _polytopes[0].add(vertex.asPolytope())
        return vertex
    }
    override fun addPolytope(
        dimension: Int,
        vertices: Set<AbstractVertex<Vector>>,
        faces: List<Set<AbstractPolytope<Vector>>>
    ): AbstractPolytope<Vector> {
        require(dimension > 0)
        val polytope = AbstractPolytope(
            construction = this,
            correspondingVertex = null,
            dimension = dimension,
            vertices = vertices,
            faces = faces,
        )
        _polytopes[dimension].add(polytope)
        return polytope
    }
}