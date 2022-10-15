/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Euclidean2DSpace
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

const val maxFloatDelta = 0.000001

fun Double.radiansToDegrees() = this * 180 / PI

fun Double.equalFloat(other: Double) = abs(this - other) < maxFloatDelta
fun DubinsPose2D.equalsFloat(other: DubinsPose2D) = x.equalFloat(other.x) && y.equalFloat(other.y) && bearing.equalFloat(other.bearing)

fun StraightTrajectory2D.inverse() = StraightTrajectory2D(end, start)
fun StraightTrajectory2D.shift(shift: Int, width: Double): StraightTrajectory2D = with(Euclidean2DSpace){
    val dX = width * sin(inverse().bearing)
    val dY = width * sin(bearing)

    return StraightTrajectory2D(
        vector(start.x - dX * shift, start.y - dY * shift),
        vector(end.x - dX * shift, end.y - dY * shift)
    )
}
