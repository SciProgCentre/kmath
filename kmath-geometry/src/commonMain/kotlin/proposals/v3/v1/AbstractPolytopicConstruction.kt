/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalUuidApi::class)

package proposals.v3.v1

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


public class AbstractVertex<Vector>(
    override val position: Vector
) : Vertex<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>> {
    public val id: Uuid = Uuid.random()
    
    private val correspondingPolytope = AbstractPolytope(
        dimension = 0,
        vertices = setOf(this),
        faces = emptyList(),
    )
    override fun asPolytope(): AbstractPolytope<Vector> = correspondingPolytope
    
    override fun toString(): String = "AbstractVertex#${id.toHexString()} at $position"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractVertex<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public class AbstractPolytope<Vector>(
    override val dimension: Int,
    override val vertices: Set<AbstractVertex<Vector>>,
    override val faces: List<Set<AbstractPolytope<Vector>>>,
) : Polytope<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>> {
    public val id: Uuid = Uuid.random()
    
    override fun toString(): String = "AbstractPolytope#${id.toHexString()}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractPolytope<*>) return false
        
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

public typealias AbstractPolytopicConstructor<Vector> = PolytopicConstructor<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>>

@Suppress("UNCHECKED_CAST")
public fun <Vector> AbstractPolytopicConstructor(): AbstractPolytopicConstructor<Vector> =
    AbstractPolytopicConstructorImpl as AbstractPolytopicConstructor<Vector>

private object AbstractPolytopicConstructorImpl : PolytopicConstructor<Any?, AbstractVertex<Any?>, AbstractPolytope<Any?>> {
    override fun newVertex(position: Any?): AbstractVertex<Any?> =
            AbstractVertex(position = position)
    override fun newPolytope(
        dimension: Int,
        vertices: Set<AbstractVertex<Any?>>,
        faces: List<Set<AbstractPolytope<Any?>>>
    ): AbstractPolytope<Any?> =
        AbstractPolytope(
            dimension = dimension,
            vertices = vertices,
            faces = faces,
        )
}

public typealias AbstractPolytopicConstruction<Vector> = PolytopicConstruction<Vector, AbstractVertex<Vector>, AbstractPolytope<Vector>>