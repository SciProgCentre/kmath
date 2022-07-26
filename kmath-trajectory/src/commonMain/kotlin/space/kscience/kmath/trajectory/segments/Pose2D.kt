/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Vector2D

/**
 * A [Vector2D] with view direction
 */
public data class Pose2D(
    override val x: Double,
    override val y: Double,
    public val theta: Double
) : Vector2D {
    public companion object {
        public fun of(vector: Vector2D, theta: Double): Pose2D = Pose2D(vector.x, vector.y, theta)
    }
}
