/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v2


public interface Vertex<out Vector> {
    public val position: Vector
    
    public fun asPolytope(): Polytope<Vector>
}

public fun <Vector> Vertex(
    position: Vector,
): Vertex<Vector> =
    object : Vertex<Vector> {
        override val position: Vector = position
        private val correspondingPolytope = Polytope(dimension = 0, vertices = setOf(this), faces = emptyList())
        override fun asPolytope(): Polytope<Vector> = correspondingPolytope
    }

public interface Polytope<out Vector> {
    public val dimension: Int
    public val vertices: Set<Vertex<Vector>>
    public val faces: List<Set<Polytope<Vector>>>
}

public fun <Vector> Polytope(
    dimension: Int,
    vertices: Set<Vertex<Vector>>,
    faces: List<Set<Polytope<Vector>>>,
): Polytope<Vector> =
    object : Polytope<Vector> {
        override val dimension = dimension
        override val vertices = vertices
        override val faces = faces
    }

public interface PolytopicConstructor<Vector> {
    public fun newVertex(
        position: Vector,
    ): Vertex<Vector>
    public fun newPolytope(
        dimension: Int,
        vertices: Set<Vertex<Vector>>,
        faces: List<Set<Polytope<Vector>>>,
    ): Polytope<Vector>
}

public data class PolytopicConstruction<out Vector>(
    public val dimension: Int,
    public val vertices: Set<Vertex<Vector>>,
    public val polytopes: List<Set<Polytope<Vector>>>,
)