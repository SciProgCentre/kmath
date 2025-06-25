/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v3.v1


public interface Vertex2D<
    out Vector
> {
    public val position: Vector
}

public interface Edge2D<
    out Vector,
    out VertexType: Vertex2D<Vector>,
> {
    public val start: VertexType
    public val end: VertexType
}

public interface Polygon2D<
    out Vector,
    out VertexType: Vertex2D<Vector>,
    out EdgeType: Edge2D<Vector, VertexType>
> {
    public val vertices: Set<VertexType>
    public val edges: Set<EdgeType>
}