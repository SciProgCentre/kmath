/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean2d

import kotlin.experimental.ExperimentalTypeInference


public interface PolytopicConstruction2D<out V> {
    public val vertices: Set<Vertex<V>>
    public val edges: Set<Edge<V>>
    public val polygons: Set<Polygon<V>>
    
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
    
    public companion object
}

public interface MutablePolytopicConstruction2D<V> : PolytopicConstruction2D<V> {
    override val vertices: Set<Vertex<V>>
    override val edges: Set<Edge<V>>
    override val polygons: Set<Polygon<V>>
    
    public fun addVertex(position: V): Vertex<V>
    public fun addEdge(start: Vertex<V>, end: Vertex<V>): Edge<V>
    public fun addPolygon(vertices: Set<Vertex<V>>, edges: Set<Edge<V>>): Polygon<V>
    
    public interface Vertex<V> : PolytopicConstruction2D.Vertex<V> {
        public fun remove()
    }
    
    public interface Edge<V> : PolytopicConstruction2D.Edge<V> {
        public fun remove()
    }
    
    public interface Polygon<V> : PolytopicConstruction2D.Polygon<V> {
        public fun remove()
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <V> PolytopicConstruction2D.Companion.build(@BuilderInference block: MutablePolytopicConstruction2D<V>.() -> Unit): PolytopicConstruction2D<V> =
    MutablePolytopicConstruction2DImpl<V>().apply(block)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class MutablePolytopicConstruction2DImpl<V> @PublishedApi internal constructor() : MutablePolytopicConstruction2D<V> {
    override val vertices: MutableSet<Vertex<V>> = mutableSetOf()
    override val edges: MutableSet<Edge<V>> = mutableSetOf()
    override val polygons: MutableSet<Polygon<V>> = mutableSetOf()
    
    override fun addVertex(position: V): Vertex<V> =
        Vertex(this, position).also { vertices.add(it) }
    
    override fun addEdge(
        start: MutablePolytopicConstruction2D.Vertex<V>,
        end: MutablePolytopicConstruction2D.Vertex<V>
    ): Edge<V> {
        check(start is Vertex<V> && start.construction === this && end is Vertex<V> && end.construction === this) { "Cannot add edge to polytopic construction with vertices not from the construction" }
        val edge = Edge(this, start, end)
        edges.add(edge)
        return edge
    }
    
    override fun addPolygon(
        vertices: Set<MutablePolytopicConstruction2D.Vertex<V>>,
        edges: Set<MutablePolytopicConstruction2D.Edge<V>>
    ): MutablePolytopicConstruction2D.Polygon<V> {
        check(vertices.all { it is Vertex<V> && it.construction === this } && edges.all { it is Edge<V> && it.construction === this })
        val polygon = Polygon(this, vertices as Set<Vertex<V>>, edges as Set<Edge<V>>)
        polygons.add(polygon)
        return polygon
    }
    
    class Vertex<V>(
        val construction: MutablePolytopicConstruction2DImpl<V>,
        override val position: V,
    ) : MutablePolytopicConstruction2D.Vertex<V> {
        val usedInEdges: MutableSet<Edge<V>> = mutableSetOf()
        val usedInPolygons: MutableSet<Polygon<V>> = mutableSetOf()
        
        override fun remove() {
            construction.vertices.remove(this)
            construction.edges.removeAll { it in usedInEdges }
            construction.polygons.removeAll { it in usedInPolygons }
        }
    }
    
    class Edge<V>(
        val construction: MutablePolytopicConstruction2DImpl<V>,
        override val start: Vertex<V>,
        override val end: Vertex<V>
    ) : MutablePolytopicConstruction2D.Edge<V> {
        val usedInPolygons: MutableSet<Polygon<V>> = mutableSetOf()
        
        override fun remove() {
            construction.edges.remove(this)
            construction.polygons.removeAll { it in usedInPolygons }
        }
    }
    
    class Polygon<V>(
        val construction: MutablePolytopicConstruction2DImpl<V>,
        override val vertices: Set<Vertex<V>>,
        override val edges: Set<Edge<V>>,
    ) : MutablePolytopicConstruction2D.Polygon<V> {
        
        override fun remove() {
            construction.polygons.remove(this)
        }
    }
}