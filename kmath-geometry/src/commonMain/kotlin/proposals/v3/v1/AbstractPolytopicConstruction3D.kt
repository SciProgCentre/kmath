/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v3.v1

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractVertex3D<Vector>(
    override val position: Vector
) : Vertex3D<Vector> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractVertex3D#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractEdge3D<Vector>(
    override val start: AbstractVertex3D<Vector>,
    override val end: AbstractVertex3D<Vector>,
) : Edge3D<Vector, AbstractVertex3D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractEdge3D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolygon3D<Vector>(
    override val vertices: Set<AbstractVertex3D<Vector>>,
    override val edges: Set<AbstractEdge3D<Vector>>,
) : Polygon3D<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractPolygon3D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolyhedron3D<Vector>(
    override val vertices: Set<AbstractVertex3D<Vector>>,
    override val edges: Set<AbstractEdge3D<Vector>>,
    override val faces: Set<AbstractPolygon3D<Vector>>,
) : Polyhedron3D<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>, AbstractPolygon3D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractPolyhedron3D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge3D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public typealias AbstractPolytopicConstructor3D<Vector> = PolytopicConstructor3D<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>, AbstractPolygon3D<Vector>, AbstractPolyhedron3D<Vector>>

@Suppress("UNCHECKED_CAST")
public fun <Vector> AbstractPolytopicConstructor3D(): AbstractPolytopicConstructor3D<Vector> =
    AbstractPolytopicConstructor3DImpl as AbstractPolytopicConstructor3D<Vector>

public object AbstractPolytopicConstructor3DImpl : PolytopicConstructor3D<Any?, AbstractVertex3D<Any?>, AbstractEdge3D<Any?>, AbstractPolygon3D<Any?>, AbstractPolyhedron3D<Any?>> {
    override fun newVertex(position: Any?): AbstractVertex3D<Any?> =
        AbstractVertex3D(
            position = position,
        )
    override fun newEdge(start: AbstractVertex3D<Any?>, end: AbstractVertex3D<Any?>): AbstractEdge3D<Any?> =
        AbstractEdge3D(
            start = start,
            end = end,
        )
    override fun newPolygon(
        vertices: Set<AbstractVertex3D<Any?>>,
        edges: Set<AbstractEdge3D<Any?>>
    ): AbstractPolygon3D<Any?> =
        AbstractPolygon3D(
            vertices = vertices,
            edges = edges,
        )
    override fun newPolyhedron(
        vertices: Set<AbstractVertex3D<Any?>>,
        edges: Set<AbstractEdge3D<Any?>>,
        faces: Set<AbstractPolygon3D<Any?>>
    ): AbstractPolyhedron3D<Any?> =
        AbstractPolyhedron3D(
            vertices = vertices,
            edges = edges,
            faces = faces,
        )
}

public typealias AbstractPolytopicConstruction3D<Vector> = PolytopicConstruction3D<Vector, AbstractVertex3D<Vector>, AbstractEdge3D<Vector>, AbstractPolygon3D<Vector>, AbstractPolyhedron3D<Vector>>