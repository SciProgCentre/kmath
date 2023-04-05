/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.equalsFloat
import space.kscience.kmath.geometry.radians
import space.kscience.kmath.geometry.sin


fun DubinsPose2D.equalsFloat(other: DubinsPose2D) =
    x.equalsFloat(other.x) && y.equalsFloat(other.y) && bearing.radians.equalsFloat(other.bearing.radians)

fun StraightTrajectory2D.inverse() = StraightTrajectory2D(end, begin)

fun StraightTrajectory2D.shift(shift: Int, width: Double): StraightTrajectory2D = with(Euclidean2DSpace) {
    val dX = width * sin(inverse().bearing)
    val dY = width * sin(bearing)

    return StraightTrajectory2D(
        vector(begin.x - dX * shift, begin.y - dY * shift),
        vector(end.x - dX * shift, end.y - dY * shift)
    )
}
