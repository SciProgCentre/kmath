/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v3.v1

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractVertex2D<Vector>(
    override val position: Vector
) : Vertex2D<Vector> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractVertex2D#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex2D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractEdge2D<Vector>(
    override val start: AbstractVertex2D<Vector>,
    override val end: AbstractVertex2D<Vector>,
) : Edge2D<Vector, AbstractVertex2D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractEdge2D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge2D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolygon2D<Vector>(
    override val vertices: Set<AbstractVertex2D<Vector>>,
    override val edges: Set<AbstractEdge2D<Vector>>,
) : Polygon2D<Vector, AbstractVertex2D<Vector>, AbstractEdge2D<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractPolygon2D#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge2D<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public typealias AbstractPolytopicConstructor2D<Vector> = PolytopicConstructor2D<Vector, AbstractVertex2D<Vector>, AbstractEdge2D<Vector>, AbstractPolygon2D<Vector>>

@Suppress("UNCHECKED_CAST")
public fun <Vector> AbstractPolytopicConstructor2D(): AbstractPolytopicConstructor2D<Vector> =
    AbstractPolytopicConstructor2DImpl as AbstractPolytopicConstructor2D<Vector>

private object AbstractPolytopicConstructor2DImpl : PolytopicConstructor2D<Any?, AbstractVertex2D<Any?>, AbstractEdge2D<Any?>, AbstractPolygon2D<Any?>> {
    override fun newVertex(position: Any?): AbstractVertex2D<Any?> =
        AbstractVertex2D(
            position = position,
        )
    override fun newEdge(start: AbstractVertex2D<Any?>, end: AbstractVertex2D<Any?>): AbstractEdge2D<Any?> =
        AbstractEdge2D(
            start = start,
            end = end,
        )
    override fun newPolygon(
        vertices: Set<AbstractVertex2D<Any?>>,
        edges: Set<AbstractEdge2D<Any?>>
    ): AbstractPolygon2D<Any?> =
        AbstractPolygon2D(
            vertices = vertices,
            edges = edges,
        )
}

public typealias AbstractPolytopicConstruction2D<Vector> = PolytopicConstruction2D<Vector, AbstractVertex2D<Vector>, AbstractEdge2D<Vector>, AbstractPolygon2D<Vector>>