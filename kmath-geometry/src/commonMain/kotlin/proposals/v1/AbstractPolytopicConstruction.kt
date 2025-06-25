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
 * Abstract vertex that holds only an identifier.
 * It can be used for accessing properties of some vertex of some polytopic construction.
 */
public class AbstractVertex internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractVertex#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Abstract edge that holds only an identifier.
 * It can be used for accessing properties of some edge of some polytopic construction.
 */
public class AbstractEdge internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractEdge#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Abstract polygon that holds only an identifier.
 * It can be used for accessing properties of some polygon of some polytopic construction.
 */
public class AbstractPolygon internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolygon#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolygon) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Abstract polyhedron that holds only an identifier.
 * It can be used for accessing properties of some polyhedron of some polytopic construction.
 */
public class AbstractPolyhedron internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolyhedron#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolyhedron) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Abstract polytope that holds only an identifier.
 * It can be used for accessing properties of some polytope of some polytopic construction.
 */
public class AbstractPolytope internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolytope#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolytope) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public typealias AbstractPolytopicConstruction2D<Vector> = PolytopicConstruction2D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon>
public typealias MutableAbstractPolytopicConstruction2D<Vector> = MutablePolytopicConstruction2D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon>

public typealias AbstractPolytopicConstruction3D<Vector> = PolytopicConstruction3D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon, AbstractPolyhedron>
public typealias MutableAbstractPolytopicConstruction3D<Vector> = MutablePolytopicConstruction3D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon, AbstractPolyhedron>

public typealias AbstractPolytopicConstruction<Vector> = PolytopicConstruction<Vector, AbstractVertex, AbstractPolytope>
public typealias MutableAbstractPolytopicConstruction<Vector> = MutablePolytopicConstruction<Vector, AbstractVertex, AbstractPolytope>

/**
 * Builder of [AbstractPolytopicConstruction2D].
 */
public inline fun <Vector> AbstractPolytopicConstruction2D(
    block: MutableAbstractPolytopicConstruction2D<Vector>.() -> Unit
): AbstractPolytopicConstruction2D<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return MutableAbstractPolytopicConstruction2DImpl<Vector>().apply(block)
}

@Suppress("EqualsOrHashCode")
@PublishedApi
internal class MutableAbstractPolytopicConstruction2DImpl<Vector> : MutableAbstractPolytopicConstruction2D<Vector> {
    private class ActualVertex<Vector>(
        val vertex: AbstractVertex,
        val position: Vector,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private class ActualEdge<Vector>(
        val edge: AbstractEdge,
        val start: ActualVertex<Vector>,
        val end: ActualVertex<Vector>,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private class ActualPolygon<Vector>(
        val polygon: AbstractPolygon,
        val vertices: Set<ActualVertex<Vector>>,
        val edges: Set<ActualEdge<Vector>>,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private val _vertices: MutableMap<AbstractVertex, ActualVertex<Vector>> = mutableMapOf()
    override val vertices: Set<AbstractVertex> get() = _vertices.keys
    private val _edges: MutableMap<AbstractEdge, ActualEdge<Vector>> = mutableMapOf()
    override val edges: Set<AbstractEdge> get() = _edges.keys
    private val _polygons: MutableMap<AbstractPolygon, ActualPolygon<Vector>> = mutableMapOf()
    override val polygons: Set<AbstractPolygon> = _polygons.keys
    
    override val AbstractVertex.position: Vector get() = (_vertices[this] ?: error("No such vertex in the construction")).position
    override fun addVertex(position: Vector): AbstractVertex {
        val abstractVertex = AbstractVertex()
        val actualVertex = ActualVertex(
            vertex = abstractVertex,
            position = position,
        )
        _vertices[abstractVertex] = actualVertex
        return abstractVertex
    }
    override fun AbstractVertex.remove() {
        val actualVertex = _vertices[this] ?: return
        val abstractEdges = _edges.keys
        for (abstractEdge in abstractEdges) {
            val actualEdge = _edges[abstractEdge]!!
            if (actualVertex == actualEdge.start || actualVertex == actualEdge.end) _edges.remove(abstractEdge)
        }
        val abstractPolygons = _polygons.keys
        for (abstractPolygon in abstractPolygons) {
            val actualPolygon = _polygons[abstractPolygon]!!
            if (actualVertex in actualPolygon.vertices) _polygons.remove(abstractPolygon)
        }
        _vertices.remove(this)
    }
    
    override val AbstractEdge.start: AbstractVertex get() = (_edges[this] ?: error("No such edge in the construction")).start.vertex
    override val AbstractEdge.end: AbstractVertex get() = (_edges[this] ?: error("No such edge in the construction")).end.vertex
    override fun addEdge(start: AbstractVertex, end: AbstractVertex): AbstractEdge {
        val abstractEdge = AbstractEdge()
        val actualEdge = ActualEdge(
            edge = abstractEdge,
            start = _vertices[start] ?: error("No such vertex in the construction"),
            end = _vertices[end] ?: error("No such vertex in the construction"),
        )
        _edges[abstractEdge] = actualEdge
        return abstractEdge
    }
    override fun AbstractEdge.remove() {
        val actualEdge = _edges[this] ?: return
        val abstractPolygons = _polygons.keys
        for (abstractPolygon in abstractPolygons) {
            val actualPolygon = _polygons[abstractPolygon]!!
            if (actualEdge in actualPolygon.edges) _polygons.remove(abstractPolygon)
        }
        _edges.remove(this)
    }
    
    override val AbstractPolygon.vertices: Set<AbstractVertex>
        get() = (_polygons[this] ?: error("No such polygon in the construction")).vertices.mapTo(mutableSetOf()) { it.vertex }
    override val AbstractPolygon.edges: Set<AbstractEdge>
        get() = (_polygons[this] ?: error("No such polygon in the construction")).edges.mapTo(mutableSetOf()) { it.edge }
    override fun addPolygon(vertices: Set<AbstractVertex>, edges: Set<AbstractEdge>): AbstractPolygon {
        val abstractPolygon = AbstractPolygon()
        val actualPolygon = ActualPolygon(
            polygon = abstractPolygon,
            vertices = vertices.mapTo(mutableSetOf()) { _vertices[it] ?: error("No such vertex in the construction") },
            edges = edges.mapTo(mutableSetOf()) { _edges[it] ?: error("No such edge in the construction") },
        )
        _polygons[abstractPolygon] = actualPolygon
        return abstractPolygon
    }
    override fun AbstractPolygon.remove() {
        _polygons.remove(this)
    }
}

/**
 * Builder of [AbstractPolytopicConstruction3D].
 */
public inline fun <Vector> AbstractPolytopicConstruction3D(
    block: MutableAbstractPolytopicConstruction3D<Vector>.() -> Unit
): AbstractPolytopicConstruction3D<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return MutableAbstractPolytopicConstruction3DImpl<Vector>().apply(block)
}

@Suppress("EqualsOrHashCode")
@PublishedApi
internal class MutableAbstractPolytopicConstruction3DImpl<Vector> : MutableAbstractPolytopicConstruction3D<Vector> {
    private class ActualVertex<Vector>(
        val vertex: AbstractVertex,
        val position: Vector,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private class ActualEdge<Vector>(
        val edge: AbstractEdge,
        val start: ActualVertex<Vector>,
        val end: ActualVertex<Vector>,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private class ActualPolygon<Vector>(
        val polygon: AbstractPolygon,
        val vertices: Set<ActualVertex<Vector>>,
        val edges: Set<ActualEdge<Vector>>,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private class ActualPolyhedron<Vector>(
        val polyhedron: AbstractPolyhedron,
        val vertices: Set<ActualVertex<Vector>>,
        val edges: Set<ActualEdge<Vector>>,
        val faces: Set<ActualPolygon<Vector>>,
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private val _vertices: MutableMap<AbstractVertex, ActualVertex<Vector>> = mutableMapOf()
    override val vertices: Set<AbstractVertex> get() = _vertices.keys
    private val _edges: MutableMap<AbstractEdge, ActualEdge<Vector>> = mutableMapOf()
    override val edges: Set<AbstractEdge> get() = _edges.keys
    private val _polygons: MutableMap<AbstractPolygon, ActualPolygon<Vector>> = mutableMapOf()
    override val polygons: Set<AbstractPolygon> get() = _polygons.keys
    private val _polyhedra: MutableMap<AbstractPolyhedron, ActualPolyhedron<Vector>> = mutableMapOf()
    override val polyhedra: Set<AbstractPolyhedron> get() = _polyhedra.keys
    
    override val AbstractVertex.position: Vector get() = (_vertices[this] ?: error("No such vertex in the construction")).position
    override fun addVertex(position: Vector): AbstractVertex {
        val abstractVertex = AbstractVertex()
        val actualVertex = ActualVertex(
            vertex = abstractVertex,
            position = position,
        )
        _vertices[abstractVertex] = actualVertex
        return abstractVertex
    }
    override fun AbstractVertex.remove() {
        val actualVertex = _vertices[this] ?: return
        val abstractEdges = _edges.keys
        for (abstractEdge in abstractEdges) {
            val actualEdge = _edges[abstractEdge]!!
            if (actualVertex == actualEdge.start || actualVertex == actualEdge.end) _edges.remove(abstractEdge)
        }
        val abstractPolygons = _polygons.keys
        for (abstractPolygon in abstractPolygons) {
            val actualPolygon = _polygons[abstractPolygon]!!
            if (actualVertex in actualPolygon.vertices) _polygons.remove(abstractPolygon)
        }
        val abstractPolyhedra = _polyhedra.keys
        for (abstractPolyhedron in abstractPolyhedra) {
            val actualPolyhedron = _polyhedra[abstractPolyhedron]!!
            if (actualVertex in actualPolyhedron.vertices) _polyhedra.remove(abstractPolyhedron)
        }
        _vertices.remove(this)
    }
    
    override val AbstractEdge.start: AbstractVertex get() = (_edges[this] ?: error("No such edge in the construction")).start.vertex
    override val AbstractEdge.end: AbstractVertex get() = (_edges[this] ?: error("No such edge in the construction")).end.vertex
    override fun addEdge(start: AbstractVertex, end: AbstractVertex): AbstractEdge {
        val abstractEdge = AbstractEdge()
        val actualEdge = ActualEdge(
            edge = abstractEdge,
            start = _vertices[start] ?: error("No such vertex in the construction"),
            end = _vertices[end] ?: error("No such vertex in the construction"),
        )
        _edges[abstractEdge] = actualEdge
        return abstractEdge
    }
    override fun AbstractEdge.remove() {
        val actualEdge = _edges[this] ?: return
        val abstractPolygons = _polygons.keys
        for (abstractPolygon in abstractPolygons) {
            val actualPolygon = _polygons[abstractPolygon]!!
            if (actualEdge in actualPolygon.edges) _polygons.remove(abstractPolygon)
        }
        val abstractPolyhedra = _polyhedra.keys
        for (abstractPolyhedron in abstractPolyhedra) {
            val actualPolyhedron = _polyhedra[abstractPolyhedron]!!
            if (actualEdge in actualPolyhedron.edges) _polyhedra.remove(abstractPolyhedron)
        }
        _edges.remove(this)
    }
    
    override val AbstractPolygon.vertices: Set<AbstractVertex>
        get() = (_polygons[this] ?: error("No such polygon in the construction")).vertices.mapTo(mutableSetOf()) { it.vertex }
    override val AbstractPolygon.edges: Set<AbstractEdge>
        get() = (_polygons[this] ?: error("No such polygon in the construction")).edges.mapTo(mutableSetOf()) { it.edge }
    override fun addPolygon(vertices: Set<AbstractVertex>, edges: Set<AbstractEdge>): AbstractPolygon {
        val abstractPolygon = AbstractPolygon()
        val actualPolygon = ActualPolygon(
            polygon = abstractPolygon,
            vertices = vertices.mapTo(mutableSetOf()) { _vertices[it] ?: error("No such vertex in the construction") },
            edges = edges.mapTo(mutableSetOf()) { _edges[it] ?: error("No such edge in the construction") },
        )
        _polygons[abstractPolygon] = actualPolygon
        return abstractPolygon
    }
    override fun AbstractPolygon.remove() {
        val actualPolygon = _polygons[this] ?: return
        val abstractPolyhedra = _polyhedra.keys
        for (abstractPolyhedron in abstractPolyhedra) {
            val actualPolyhedron = _polyhedra[abstractPolyhedron]!!
            if (actualPolygon in actualPolyhedron.faces) _polyhedra.remove(abstractPolyhedron)
        }
        _polygons.remove(this)
    }
    
    override val AbstractPolyhedron.vertices: Set<AbstractVertex>
        get() = (_polyhedra[this] ?: error("No such polyhedron in the construction")).vertices.mapTo(mutableSetOf()) { it.vertex }
    override val AbstractPolyhedron.edges: Set<AbstractEdge>
        get() = (_polyhedra[this] ?: error("No such polyhedron in the construction")).edges.mapTo(mutableSetOf()) { it.edge }
    override val AbstractPolyhedron.faces: Set<AbstractPolygon>
        get() = (_polyhedra[this] ?: error("No such polyhedron in the construction")).faces.mapTo(mutableSetOf()) { it.polygon }
    override fun addPolyhedron(
        vertices: Set<AbstractVertex>,
        edges: Set<AbstractEdge>,
        faces: Set<AbstractPolygon>
    ): AbstractPolyhedron {
        val abstractPolyhedron = AbstractPolyhedron()
        val actualPolyhedron = ActualPolyhedron(
            polyhedron = abstractPolyhedron,
            vertices = vertices.mapTo(mutableSetOf()) { _vertices[it] ?: error("No such vertex in the construction") },
            edges = edges.mapTo(mutableSetOf()) { _edges[it] ?: error("No such edge in the construction") },
            faces = faces.mapTo(mutableSetOf()) { _polygons[it] ?: error("No such polygon in the construction") },
        )
        _polyhedra[abstractPolyhedron] = actualPolyhedron
        return abstractPolyhedron
    }
    override fun AbstractPolyhedron.remove() {
        _polyhedra.remove(this)
    }
}

/**
 * Builder for [AbstractPolytopicConstruction].
 */
public inline fun <Vector> AbstractPolytopicConstruction(
    dimension: Int,
    block: MutableAbstractPolytopicConstruction<Vector>.() -> Unit
): AbstractPolytopicConstruction<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return MutableAbstractPolytopicConstructionImpl<Vector>(dimension).apply(block)
}

@Suppress("EqualsOrHashCode")
@PublishedApi
internal class MutableAbstractPolytopicConstructionImpl<Vector>(
    override val dimension: Int,
) : MutableAbstractPolytopicConstruction<Vector> {
    private class ActualVertex<Vector>(
        val vertex: AbstractVertex,
        correspondingAbstractPolytope: AbstractPolytope,
        val position: Vector,
    ) {
        val correspondingPolytope: ActualPolytope<Vector> =
            ActualPolytope(
                polytope = correspondingAbstractPolytope,
                correspondingVertex = this,
                dimension = 0,
                vertices = setOf(this),
                faces = emptyList()
            )
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private class ActualPolytope<Vector>(
        val polytope: AbstractPolytope,
        val correspondingVertex: ActualVertex<Vector>?,
        val dimension: Int,
        val vertices: Set<ActualVertex<Vector>>,
        val faces: List<Set<ActualPolytope<Vector>>>
    ) {
        override fun equals(other: Any?): Boolean = this === other
    }
    
    private val _vertices: MutableMap<AbstractVertex, ActualVertex<Vector>> = mutableMapOf()
    override val vertices: Set<AbstractVertex> get() = _vertices.keys
    private val _polytopes: List<MutableMap<AbstractPolytope, ActualPolytope<Vector>>> = List(dimension + 1) { mutableMapOf() }
    override val polytopes: List<Set<AbstractPolytope>> get() = _polytopes.map { it.keys }
    private val allPolytopes: MutableMap<AbstractPolytope, ActualPolytope<Vector>> = mutableMapOf()
    
    override val AbstractVertex.position: Vector get() = (_vertices[this] ?: error("No such vertex in the construction")).position
    override fun AbstractVertex.asPolytope(): AbstractPolytope =
        (_vertices[this] ?: error("No such vertex in the construction")).correspondingPolytope.polytope
    override fun addVertex(position: Vector): AbstractVertex {
        val abstractVertex = AbstractVertex()
        val actualVertex = ActualVertex(
            vertex = abstractVertex,
            correspondingAbstractPolytope = AbstractPolytope(),
            position = position,
        )
        _vertices[abstractVertex] = actualVertex
        _polytopes[0][actualVertex.correspondingPolytope.polytope] = actualVertex.correspondingPolytope
        allPolytopes[actualVertex.correspondingPolytope.polytope] = actualVertex.correspondingPolytope
        return abstractVertex
    }
    override fun AbstractVertex.remove() {
        val actualVertex = _vertices[this] ?: return
        _polytopes.forEach {
            val abstractPolytopes = it.keys
            for (abstractPolytope in abstractPolytopes) {
                val actualPolytope = it[abstractPolytope]!!
                if (actualVertex in actualPolytope.vertices) it.remove(abstractPolytope)
            }
        }
        _vertices.remove(this)
    }
    
    override val AbstractPolytope.dimension: Int
        get() = (allPolytopes[this] ?: error("No such polytope in the construction")).dimension
    override val AbstractPolytope.vertices: Set<AbstractVertex>
        get() = (allPolytopes[this] ?: error("No such polytope in the construction")).vertices.mapTo(mutableSetOf()) { it.vertex }
    override val AbstractPolytope.faces: List<Set<AbstractPolytope>>
        get() = (allPolytopes[this] ?: error("No such polytope in the construction")).faces.map { facesOfDimension -> facesOfDimension.mapTo(mutableSetOf()) { it.polytope } }
    override fun addPolytope(
        dimension: Int,
        vertices: Set<AbstractVertex>,
        faces: List<Set<AbstractPolytope>>
    ): AbstractPolytope {
        require(dimension > 0) { "0-dimensional polytopes should created via 'addVertex' method" }
        val abstractPolytope = AbstractPolytope()
        val actualPolytope = ActualPolytope(
            polytope = abstractPolytope,
            correspondingVertex = null,
            dimension = dimension,
            vertices = vertices.mapTo(mutableSetOf()) { _vertices[it] ?: error("No such vertex in the construction") },
            faces = faces.mapIndexed { dim, faces -> faces.mapTo(mutableSetOf()) { _polytopes[dim][it] ?: error("No such polytope in the construction") } },
        )
        _polytopes[dimension][abstractPolytope] = actualPolytope
        allPolytopes[abstractPolytope] = actualPolytope
        return abstractPolytope
    }
    override fun AbstractPolytope.remove() {
        val thisActualPolytope = allPolytopes[this] ?: return
        for (dim in thisActualPolytope.dimension + 1 .. this@MutableAbstractPolytopicConstructionImpl.dimension) {
            val abstractPolytopes = _polytopes[dim].keys
            for (abstractPolytope in abstractPolytopes) {
                val actualPolytope = _polytopes[dim][abstractPolytope]!!
                if (thisActualPolytope in actualPolytope.faces[thisActualPolytope.dimension]) {
                    _polytopes[dim].remove(abstractPolytope)
                    allPolytopes.remove(abstractPolytope)
                }
            }
        }
        _polytopes[thisActualPolytope.dimension].remove(this)
        allPolytopes.remove(this)
        thisActualPolytope.correspondingVertex?.also { _vertices.remove(it.vertex) }
    }
}