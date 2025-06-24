/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v5


public interface PolytopicConstruction<Vector, Vertex, Polytope> {
    public val dimension: Int
    public val polytopes: List<Set<Polytope>>
    public val vertices: Set<Vertex>
    
    public val Vertex.position: Vector
    public fun Vertex.asPolytope(): Polytope
    
    public val Polytope.dimension: Int
    public val Polytope.faces: List<Set<Polytope>>
    public val Polytope.vertices: Set<Vertex>
}

public interface MutablePolytopicConstruction<Vector, Vertex, Polytope> : PolytopicConstruction<Vector, Vertex, Polytope> {
    public fun Vertex.bind(position: Vector, correspondingPolytope: Polytope)
    public fun Polytope.bind(
        dimension: Int,
        vertices: Set<Vertex>,
        faces: List<Set<Polytope>>,
    )
    public fun Vertex.unbind()
    public fun Polytope.unbind()
}