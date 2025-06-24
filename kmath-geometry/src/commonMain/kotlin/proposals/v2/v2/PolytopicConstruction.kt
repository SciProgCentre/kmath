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
public inline fun <V> PolytopicConstruction(@BuilderInference block: MutablePolytopicConstruction<V>.() -> Unit): PolytopicConstruction<V> =
    TODO("Not yet implemented")