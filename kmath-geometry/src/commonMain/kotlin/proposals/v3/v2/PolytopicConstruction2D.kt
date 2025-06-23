/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v2


public interface Vertex2D<out Vector> {
    public val position: Vector
}

public interface Edge2D<out Vector> {
    public val start: Vertex2D<Vector>
    public val end: Vertex2D<Vector>
}

public interface Polygon2D<out Vector> {
    public val vertices: Set<Vertex2D<Vector>>
    public val edges: Set<Vertex2D<Vector>>
}