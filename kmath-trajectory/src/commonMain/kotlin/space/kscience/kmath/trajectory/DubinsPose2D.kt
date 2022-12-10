/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Vector
import kotlin.math.atan2

/**
 * Combination of [Vector] and its view angle (clockwise from positive y-axis direction)
 */
public interface DubinsPose2D : DoubleVector2D {
    public val coordinate: DoubleVector2D
    public val bearing: Double
}

public class PhaseVector2D(
    override val coordinate: DoubleVector2D,
    public val velocity: DoubleVector2D,
) : DubinsPose2D, DoubleVector2D by coordinate {
    override val bearing: Double get() = atan2(velocity.x, velocity.y)
}

private class DubinsPose2DImpl(
    override val coordinate: DoubleVector2D,
    override val bearing: Double,
) : DubinsPose2D, DoubleVector2D by coordinate{

    override fun toString(): String = "Pose2D(x=$x, y=$y, bearing=$bearing)"
}


public fun DubinsPose2D(coordinate: DoubleVector2D, theta: Double): DubinsPose2D = DubinsPose2DImpl(coordinate, theta)