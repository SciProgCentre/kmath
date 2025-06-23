/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v2


public interface Vertex3D<out Vector> {
    public val position: Vector
}

public interface Edge3D<out Vector> {
    public val start: Vertex3D<Vector>
    public val end: Vertex3D<Vector>
}

public interface Polygon3D<out Vector> {
    public val vertices: Set<Vertex3D<Vector>>
    public val edges: Set<Edge3D<Vector>>
}

public interface Polyhedron3D<out Vector> {
    public val vertices: Set<Vertex3D<Vector>>
    public val edges: Set<Edge3D<Vector>>
    public val faces: Set<Polygon3D<Vector>>
}