/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v2.v1

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractVertex2D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction2D<Vector>,
    override val position: Vector
) : MutablePolytopicConstruction2D.Vertex<Vector> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._vertices.remove(this)
        construction._edges.removeAll { this == it.start || this == it.end }
        construction._polygons.removeAll { this in it.vertices }
    }
    
    override fun toString(): String = "AbstractVertex2D#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex2D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractEdge2D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction2D<Vector>,
    override val start: AbstractVertex2D<Vector>,
    override val end: AbstractVertex2D<Vector>,
) : MutablePolytopicConstruction2D.Edge<Vector, AbstractVertex2D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._edges.remove(this)
        construction._polygons.removeAll { this in it.edges }
    }
    
    override fun toString(): String = "AbstractEdge2D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge2D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolygon2D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction2D<Vector>,
    override val vertices: Set<AbstractVertex2D<Vector>>,
    override val edges: Set<AbstractEdge2D<Vector>>,
) : MutablePolytopicConstruction2D.Polygon<Vector, AbstractVertex2D<Vector>, AbstractEdge2D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._polygons.remove(this)
    }
    
    override fun toString(): String = "AbstractEdge2D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge2D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolytopicConstruction2D<Vector> : MutablePolytopicConstruction2D<Vector, AbstractVertex2D<Vector>, AbstractEdge2D<Vector>, AbstractPolygon2D<Vector>> {
    internal val _vertices: MutableSet<AbstractVertex2D<Vector>> = mutableSetOf()
    override val vertices: Set<AbstractVertex2D<Vector>> get() = _vertices
    
    internal val _edges: MutableSet<AbstractEdge2D<Vector>> = mutableSetOf()
    override val edges: Set<AbstractEdge2D<Vector>> get() = _edges
    
    internal val _polygons: MutableSet<AbstractPolygon2D<Vector>> = mutableSetOf()
    override val polygons: Set<AbstractPolygon2D<Vector>> get() = _polygons
    
    override fun addVertex(position: Vector): AbstractVertex2D<Vector> {
        val vertex = AbstractVertex2D(
            construction = this,
            position = position,
        )
        _vertices.add(vertex)
        return vertex
    }
    
    override fun addEdge(start: AbstractVertex2D<Vector>, end: AbstractVertex2D<Vector>): AbstractEdge2D<Vector> {
        val edge = AbstractEdge2D(
            construction = this,
            start = start,
            end = end,
        )
        _edges.add(edge)
        return edge
    }
    
    override fun addPolygon(
        vertices: Set<AbstractVertex2D<Vector>>,
        edges: Set<AbstractEdge2D<Vector>>
    ): AbstractPolygon2D<Vector> {
        val polygon = AbstractPolygon2D(
            construction = this,
            vertices = vertices,
            edges = edges,
        )
        _polygons.add(polygon)
        return polygon
    }
}