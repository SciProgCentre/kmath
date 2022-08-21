/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Vector
import kotlin.math.atan2

/**
 * Combination of [Vector] and its view angle
 */
public interface Pose2D: DoubleVector2D{
    public val coordinate: DoubleVector2D
    public val theta: Double
}

public class PhaseVector2D(
    override val coordinate: DoubleVector2D,
    public val velocity: DoubleVector2D
): Pose2D, DoubleVector2D by coordinate{
    override val theta: Double get() = atan2(velocity.y, velocity.x)
}

internal class Pose2DImpl(
    override val coordinate: DoubleVector2D,
    override val theta: Double
) : Pose2D, DoubleVector2D by coordinate


public fun Pose2D(coordinate: DoubleVector2D, theta: Double): Pose2D = Pose2DImpl(coordinate, theta)