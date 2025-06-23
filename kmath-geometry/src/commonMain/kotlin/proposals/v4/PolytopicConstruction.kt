/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v4


public interface Polytope<Vector> {
    public val dimension: Int
    public val vertices: Set<Vertex<Vector>>
    public val faces: List<Set<Polytope<Vector>>>
}

public interface Vertex<Vector> : Polytope<Vector> {
    public val position: Vector
    
    override val dimension: Int get() = 0
    override val vertices: Set<Vertex<Vector>> get() = setOf(this)
    override val faces: List<Set<Polytope<Vector>>> get() = emptyList()
}

public interface Edge<Vector> : Polytope<Vector> {
    public val start: Vertex<Vector>
    public val end: Vertex<Vector>
    
    override val dimension: Int get() = 1
    override val vertices: Set<Vertex<Vector>> get() = setOf(start, end)
    override val faces: List<Set<Polytope<Vector>>> get() = listOf(vertices)
}

public interface Polygon<Vector> : Polytope<Vector> {
    public val edges: Set<Edge<Vector>>
    
    override val dimension: Int get() = 2
    override val faces: List<Set<Polytope<Vector>>> get() = listOf(vertices, edges)
}

public interface Polyhedron<Vector> : Polytope<Vector> {
    public val edges: Set<Edge<Vector>>
    public val polygons: Set<Polygon<Vector>>
    
    override val dimension: Int get() = 3
    override val faces: List<Set<Polytope<Vector>>> get() = listOf(vertices, edges, polygons)
}