/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v5


public interface PolytopicConstruction<out Vector, out Vertex, out Polytope> {
    public val dimension: Int
    public val polytopes: List<Set<Polytope>>
    public val vertices: Set<Vertex>
    
    public val @UnsafeVariance Vertex.position: Vector
    public fun @UnsafeVariance Vertex.asPolytope(): Polytope
    
    public val @UnsafeVariance Polytope.dimension: Int
    public val @UnsafeVariance Polytope.faces: List<Set<Polytope>>
    public val @UnsafeVariance Polytope.vertices: Set<Vertex>
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