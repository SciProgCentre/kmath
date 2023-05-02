/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.Serializable
import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import kotlin.math.*

/**
 * A circle in 2D space
 */
@Serializable
public data class Circle2D(
    @Serializable(Euclidean2DSpace.VectorSerializer::class) public val center: DoubleVector2D,
    public val radius: Double
)


public val Circle2D.circumference: Double get() = radius * 2 * PI
