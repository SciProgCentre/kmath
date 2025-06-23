/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v2


public interface Vertex<Vector> {
    public val position: Vector
    
    public fun asPolytope(): Polytope<Vector>
}

public interface Polytope<Vector> {
    public val dimension: Int
    public val vertices: Set<Vertex<Vector>>
    public val faces: List<Set<Polytope<Vector>>>
}