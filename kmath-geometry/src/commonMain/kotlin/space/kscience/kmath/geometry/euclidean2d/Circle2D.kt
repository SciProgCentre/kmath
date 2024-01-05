/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean2d

import kotlinx.serialization.Serializable
import space.kscience.kmath.geometry.Vector2D
import kotlin.math.PI


public interface Circle2D<T>{
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
    override val radius: Double
): Circle2D<Double>

public fun Circle2D(center: Float64Vector2D, radius: Double): Circle2D<Double> = Float64Circle2D(center, radius)


