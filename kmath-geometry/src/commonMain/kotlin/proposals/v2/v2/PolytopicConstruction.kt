/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v2.v2

import kotlin.experimental.ExperimentalTypeInference


public interface PolytopicConstruction<out V> {
    public val dimension: Int
    
    public val vertices: Set<Vertex<V>>
    public val polytopes: List<Set<Polytope<V>>>
    
    public interface Vertex<out V> {
        public val position: V
        public fun asPolytope(): Polytope<V>
    }
    
    public interface Polytope<out V> {
        public val dimension: Int
        public val vertices: Set<Vertex<V>>
        public val faces: List<Set<Polytope<V>>>
    }
}

public interface MutablePolytopicConstruction<V> : PolytopicConstruction<V> {
    override val vertices: Set<Vertex<V>>
    override val polytopes: List<Set<Polytope<V>>>
    
    public fun addVertex(position: V): Vertex<V>
    public fun addPolytope(dimension: Int, vertices: Set<Vertex<V>>, faces: List<Set<Polytope<V>>>): Polytope<V>
    
    public interface Vertex<V> : PolytopicConstruction.Vertex<V> {
        override fun asPolytope(): Polytope<V>
        
        public fun remove()
    }
    
    public interface Polytope<V> : PolytopicConstruction.Polytope<V> {
        override val vertices: Set<Vertex<V>>
        override val faces: List<Set<Polytope<V>>>
        
        public fun remove()
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <V> PolytopicConstruction(
    dimension: Int,
    @BuilderInference block: MutablePolytopicConstruction<V>.() -> Unit
): PolytopicConstruction<V> =
    MutablePolytopicConstructionImpl<V>(dimension).apply(block)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal class MutablePolytopicConstructionImpl<V> @PublishedApi internal constructor(
    override val dimension: Int,
): MutablePolytopicConstruction<V> {
    override val vertices: MutableSet<Vertex<V>> = mutableSetOf()
    override val polytopes: List<MutableSet<Polytope<V>>> = List(dimension + 1) { mutableSetOf() }
    
    override fun addVertex(position: V): Vertex<V> {
        val vertex = Vertex(
            construction = this,
            position = position,
        )
        vertices.add(vertex)
        polytopes[0].add(vertex.correspondingPolytope)
        return vertex
    }
    
    override fun addPolytope(
        dimension: Int,
        vertices: Set<MutablePolytopicConstruction.Vertex<V>>,
        faces: List<Set<MutablePolytopicConstruction.Polytope<V>>>
    ): Polytope<V> {
        check(dimension > 0)
        check(vertices.all { it is Vertex<V> } && faces.all { dimFaces -> dimFaces.all { it is Polytope<V> } })
        val polytope = Polytope(
            construction = this,
            correspondingVertex = null,
            dimension = dimension,
            vertices = vertices as Set<Vertex<V>>,
            faces = faces as List<Set<Polytope<V>>>,
        )
        polytopes[dimension].add(polytope)
        return polytope
    }
    
    class Vertex<V>(
        val construction: MutablePolytopicConstructionImpl<V>,
        override val position: V,
    ) : MutablePolytopicConstruction.Vertex<V> {
        val correspondingPolytope: Polytope<V> =
            Polytope(
                construction = construction,
                correspondingVertex = this,
                dimension = 0,
                vertices = setOf(this),
                faces = emptyList(),
            )
        override fun asPolytope(): MutablePolytopicConstruction.Polytope<V> = correspondingPolytope
        override fun remove() {
            construction.polytopes.forEach { dimPolytopes -> dimPolytopes.removeAll { this in it.vertices } }
            construction.vertices.remove(this)
        }
    }
    
    class Polytope<V>(
        val construction: MutablePolytopicConstructionImpl<V>,
        val correspondingVertex: Vertex<V>?,
        override val dimension: Int,
        override val vertices: Set<Vertex<V>>,
        override val faces: List<Set<Polytope<V>>>,
    ) : MutablePolytopicConstruction.Polytope<V> {
        override fun remove() {
            construction.polytopes.drop(dimension + 1).forEach { dimPolytopes -> dimPolytopes.removeAll { this in it.faces[dimension] } }
            construction.polytopes[dimension].remove(this)
            correspondingVertex?.also { construction.vertices.remove(it) }
        }
    }
}