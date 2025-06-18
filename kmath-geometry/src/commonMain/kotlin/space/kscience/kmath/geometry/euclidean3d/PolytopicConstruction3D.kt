/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean3d

import kotlin.experimental.ExperimentalTypeInference


public interface PolytopicConstruction3D<out V> {
    public val vertices: Set<Vertex<V>>
    public val edges: Set<Edge<V>>
    public val polygons: Set<Polygon<V>>
    public val polyhedra: Set<Polyhedron<V>>
    
    public interface Vertex<out V> {
        public val position: V
    }
    
    public interface Edge<out V> {
        public val start: Vertex<V>
        public val end: Vertex<V>
    }
    
    public interface Polygon<out V> {
        public val vertices: Set<Vertex<V>>
        public val edges: Set<Edge<V>>
    }
    
    public interface Polyhedron<out V> {
        public val vertices: Set<Vertex<V>>
        public val edges: Set<Edge<V>>
        public val faces: Set<Polygon<V>>
    }
    
    public companion object
}

public interface MutablePolytopicConstruction3D<V> : PolytopicConstruction3D<V> {
    override val vertices: Set<Vertex<V>>
    override val edges: Set<Edge<V>>
    override val polygons: Set<Polygon<V>>
    override val polyhedra: Set<Polyhedron<V>>
    
    public fun addVertex(position: V): Vertex<V>
    public fun addEdge(start: Vertex<V>, end: Vertex<V>): Edge<V>
    public fun addPolygon(vertices: Set<Vertex<V>>, edges: Set<Edge<V>>): Polygon<V>
    public fun addPolyhedron(vertices: Set<Vertex<V>>, edges: Set<Edge<V>>, faces: Set<Polygon<V>>): Polyhedron<V>
    
    public interface Vertex<V> : PolytopicConstruction3D.Vertex<V> {
        public fun remove()
    }
    
    public interface Edge<V> : PolytopicConstruction3D.Edge<V> {
        public fun remove()
    }
    
    public interface Polygon<V> : PolytopicConstruction3D.Polygon<V> {
        public fun remove()
    }
    
    public interface Polyhedron<V> : PolytopicConstruction3D.Polyhedron<V> {
        public fun remove()
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <V> PolytopicConstruction3D.Companion.build(@BuilderInference block: MutablePolytopicConstruction3D<V>.() -> Unit): PolytopicConstruction3D<V> =
    MutablePolytopicConstruction3DImpl<V>().apply(block)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class MutablePolytopicConstruction3DImpl<V> @PublishedApi internal constructor(): MutablePolytopicConstruction3D<V> {
    override val vertices: MutableSet<Vertex<V>> = mutableSetOf()
    override val edges: MutableSet<Edge<V>> = mutableSetOf()
    override val polygons: MutableSet<Polygon<V>> = mutableSetOf()
    override val polyhedra: MutableSet<Polyhedron<V>> = mutableSetOf()
    
    override fun addVertex(position: V): Vertex<V> =
        Vertex(this, position).also { vertices.add(it) }
    
    override fun addEdge(
        start: MutablePolytopicConstruction3D.Vertex<V>,
        end: MutablePolytopicConstruction3D.Vertex<V>
    ): Edge<V> {
        check(start is Vertex<V> && start.construction === this && end is Vertex<V> && end.construction === this) { "Cannot add edge to polytopic construction with vertices not from the construction" }
        val edge = Edge(this, start, end)
        edges.add(edge)
        return edge
    }
    
    override fun addPolygon(
        vertices: Set<MutablePolytopicConstruction3D.Vertex<V>>,
        edges: Set<MutablePolytopicConstruction3D.Edge<V>>
    ): MutablePolytopicConstruction3D.Polygon<V> {
        check(vertices.all { it is Vertex<V> && it.construction === this } && edges.all { it is Edge<V> && it.construction === this })
        val polygon = Polygon(this, vertices as Set<Vertex<V>>, edges as Set<Edge<V>>)
        polygons.add(polygon)
        return polygon
    }
    
    override fun addPolyhedron(
        vertices: Set<MutablePolytopicConstruction3D.Vertex<V>>,
        edges: Set<MutablePolytopicConstruction3D.Edge<V>>,
        faces: Set<MutablePolytopicConstruction3D.Polygon<V>>
    ): MutablePolytopicConstruction3D.Polyhedron<V> {
        check(vertices.all { it is Vertex<V> && it.construction === this } && edges.all { it is Edge<V> && it.construction === this } && faces.all { it is Polygon<V> && it.construction === this })
        val polyhedron = Polyhedron(this, vertices as Set<Vertex<V>>, edges as Set<Edge<V>>, faces as Set<Polygon<V>>)
        polyhedra.add(polyhedron)
        return polyhedron
    }
    
    class Vertex<V>(
        val construction: MutablePolytopicConstruction3DImpl<V>,
        override val position: V,
    ) : MutablePolytopicConstruction3D.Vertex<V> {
        val usedInEdges: MutableSet<Edge<V>> = mutableSetOf()
        val usedInPolygons: MutableSet<Polygon<V>> = mutableSetOf()
        val usedInPolyhedra: MutableSet<Polyhedron<V>> = mutableSetOf()
        
        override fun remove() {
            construction.vertices.remove(this)
            construction.edges.removeAll { it in usedInEdges }
            construction.polygons.removeAll { it in usedInPolygons }
            construction.polyhedra.removeAll { it in usedInPolyhedra }
        }
    }
    
    class Edge<V>(
        val construction: MutablePolytopicConstruction3DImpl<V>,
        override val start: Vertex<V>,
        override val end: Vertex<V>
    ) : MutablePolytopicConstruction3D.Edge<V> {
        val usedInPolygons: MutableSet<Polygon<V>> = mutableSetOf()
        val usedInPolyhedra: MutableSet<Polyhedron<V>> = mutableSetOf()
        
        override fun remove() {
            construction.edges.remove(this)
            construction.polygons.removeAll { it in usedInPolygons }
            construction.polyhedra.removeAll { it in usedInPolyhedra }
        }
    }
    
    class Polygon<V>(
        val construction: MutablePolytopicConstruction3DImpl<V>,
        override val vertices: Set<Vertex<V>>,
        override val edges: Set<Edge<V>>,
    ) : MutablePolytopicConstruction3D.Polygon<V> {
        val usedInPolyhedra: MutableSet<Polyhedron<V>> = mutableSetOf()
        
        override fun remove() {
            construction.polygons.remove(this)
            construction.polyhedra.removeAll { it in usedInPolyhedra }
        }
    }
    
    class Polyhedron<V>(
        val construction: MutablePolytopicConstruction3DImpl<V>,
        override val vertices: Set<Vertex<V>>,
        override val edges: Set<Edge<V>>,
        override val faces: Set<Polygon<V>>,
    ) : MutablePolytopicConstruction3D.Polyhedron<V> {
        override fun remove() {
            construction.polyhedra.remove(this)
        }
    }
}