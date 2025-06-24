/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v2.v1

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractVertex3D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction3D<Vector>,
    override val position: Vector
) : MutablePolytopicConstruction3D.Vertex<Vector> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._vertices.remove(this)
        construction._edges.removeAll { this == it.start || this == it.end }
        construction._polygons.removeAll { this in it.vertices }
        construction._polyhedra.removeAll { this in it.vertices }
    }
    
    override fun toString(): String = "AbstractVertex3D#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractEdge3D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction3D<Vector>,
    override val start: AbstractVertex3D<Vector>,
    override val end: AbstractVertex3D<Vector>,
) : MutablePolytopicConstruction3D.Edge<Vector, AbstractVertex3D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._edges.remove(this)
        construction._polygons.removeAll { this in it.edges }
        construction._polyhedra.removeAll { this in it.edges }
    }
    
    override fun toString(): String = "AbstractEdge3D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolygon3D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction3D<Vector>,
    override val vertices: Set<AbstractVertex3D<Vector>>,
    override val edges: Set<AbstractEdge3D<Vector>>,
) : MutablePolytopicConstruction3D.Polygon<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._polygons.remove(this)
        construction._polyhedra.removeAll { this in it.faces }
    }
    
    override fun toString(): String = "AbstractEdge3D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolyhedron3D<Vector> internal constructor(
    private val construction: AbstractPolytopicConstruction3D<Vector>,
    override val vertices: Set<AbstractVertex3D<Vector>>,
    override val edges: Set<AbstractEdge3D<Vector>>,
    override val faces: Set<AbstractPolygon3D<Vector>>,
) : MutablePolytopicConstruction3D.Polyhedron<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>, AbstractPolygon3D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun remove() {
        construction._polyhedra.remove(this)
    }
    
    override fun toString(): String = "AbstractEdge3D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolytopicConstruction3D<Vector> : MutablePolytopicConstruction3D<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>, AbstractPolygon3D<Vector>, AbstractPolyhedron3D<Vector>> {
    internal val _vertices: MutableSet<AbstractVertex3D<Vector>> = mutableSetOf()
    override val vertices: Set<AbstractVertex3D<Vector>> get() = _vertices
    
    internal val _edges: MutableSet<AbstractEdge3D<Vector>> = mutableSetOf()
    override val edges: Set<AbstractEdge3D<Vector>> get() = _edges
    
    internal val _polygons: MutableSet<AbstractPolygon3D<Vector>> = mutableSetOf()
    override val polygons: Set<AbstractPolygon3D<Vector>> get() = _polygons
    
    internal val _polyhedra: MutableSet<AbstractPolyhedron3D<Vector>> = mutableSetOf()
    override val polyhedra: Set<AbstractPolyhedron3D<Vector>> get() = _polyhedra
    
    override fun addVertex(position: Vector): AbstractVertex3D<Vector> {
        val vertex = AbstractVertex3D(
            construction = this,
            position = position,
        )
        _vertices.add(vertex)
        return vertex
    }
    
    override fun addEdge(start: AbstractVertex3D<Vector>, end: AbstractVertex3D<Vector>): AbstractEdge3D<Vector> {
        val edge = AbstractEdge3D(
            construction = this,
            start = start,
            end = end,
        )
        _edges.add(edge)
        return edge
    }
    
    override fun addPolygon(
        vertices: Set<AbstractVertex3D<Vector>>,
        edges: Set<AbstractEdge3D<Vector>>
    ): AbstractPolygon3D<Vector> {
        val polygon = AbstractPolygon3D(
            construction = this,
            vertices = vertices,
            edges = edges,
        )
        _polygons.add(polygon)
        return polygon
    }
    
    override fun addPolyhedron(
        vertices: Set<AbstractVertex3D<Vector>>,
        edges: Set<AbstractEdge3D<Vector>>,
        faces: Set<AbstractPolygon3D<Vector>>
    ): AbstractPolyhedron3D<Vector> {
        val polyhedron = AbstractPolyhedron3D(
            construction = this,
            vertices = vertices,
            edges = edges,
            faces = faces,
        )
        _polyhedra.add(polyhedron)
        return polyhedron
    }
}