/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v1

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * Abstract vertex that holds only an identifier and its position.
 */
public class AbstractVertex internal constructor(
    public val id: Uuid = Uuid.random(),
) {
    override fun toString(): String = "AbstractVertex#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractEdge internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractEdge#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEdge) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolygon internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolygon#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolygon) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolyhedron internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolyhedron#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolyhedron) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Abstract polytope that holds an identifier.
 */
public class AbstractPolytope internal constructor(public val id: Uuid = Uuid.random()) {
    override fun toString(): String = "AbstractPolytope#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolytope) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public typealias AbstractPolytopicConstruction2D<Vector> = PolytopicConstruction2D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon>
public typealias MutableAbstractPolytopicConstruction2D<Vector> = MutablePolytopicConstruction2D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon>

public typealias AbstractPolytopicConstruction3D<Vector> = PolytopicConstruction3D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon, AbstractPolyhedron>
public typealias MutableAbstractPolytopicConstruction3D<Vector> = MutablePolytopicConstruction3D<Vector, AbstractVertex, AbstractEdge, AbstractPolygon, AbstractPolyhedron>

public typealias AbstractPolytopicConstruction<Vector> = PolytopicConstruction<Vector, AbstractVertex, AbstractPolytope>
public typealias MutableAbstractPolytopicConstruction<Vector> = MutablePolytopicConstruction<Vector, AbstractVertex, AbstractPolytope>

public inline fun <Vector> AbstractPolytopicConstruction2D(
    block: MutableAbstractPolytopicConstruction2D<Vector>.() -> Unit
): AbstractPolytopicConstruction2D<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    TODO()
}

public inline fun <Vector> AbstractPolytopicConstruction3D(
    block: MutableAbstractPolytopicConstruction3D<Vector>.() -> Unit
): AbstractPolytopicConstruction3D<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    TODO()
}

/**
 * Builder for [AbstractPolytopicConstruction] via [MutableAbstractPolytopicConstruction] API.
 */
public inline fun <Vector> AbstractPolytopicConstruction(
    dimension: Int,
    block: MutableAbstractPolytopicConstruction<Vector>.() -> Unit
): AbstractPolytopicConstruction<Vector> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    TODO()
}