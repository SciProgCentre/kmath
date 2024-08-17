/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean2d

import kotlinx.serialization.Serializable
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.structures.Float64
import kotlin.math.PI


public interface Circle2D<T> {
    public val center: Vector2D<T>
    public val radius: Double
}

public val Circle2D<*>.circumference: Double get() = radius * 2 * PI

/**
 * A circle in 2D space
 */
@Serializable
public data class Float64Circle2D(
    @Serializable(Float64Space2D.VectorSerializer::class) override val center: Float64Vector2D,
    override val radius: Float64,
) : Circle2D<Float64>

public fun Circle2D(center: Vector2D<Float64>, radius: Double): Float64Circle2D = Float64Circle2D(
    center as? Float64Vector2D ?: Float64Vector2D(center.x, center.y),
    radius
)


