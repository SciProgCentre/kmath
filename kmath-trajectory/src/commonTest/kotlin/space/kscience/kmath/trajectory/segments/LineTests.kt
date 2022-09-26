/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.trajectory.StraightTrajectory
import space.kscience.kmath.trajectory.radiansToDegrees
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LineTests {

    @Test
    fun lineTest() = with(Euclidean2DSpace){
        val straight = StraightTrajectory(vector(0.0, 0.0), vector(100.0, 100.0))
        assertEquals(sqrt(100.0.pow(2) + 100.0.pow(2)), straight.length)
        assertEquals(45.0, straight.theta.radiansToDegrees())
    }

    @Test
    fun lineAngleTest() = with(Euclidean2DSpace){
        //val zero = Vector2D(0.0, 0.0)
        val north = StraightTrajectory(Euclidean2DSpace.zero, vector(0.0, 2.0))
        assertEquals(0.0, north.theta.radiansToDegrees())
        val east = StraightTrajectory(Euclidean2DSpace.zero, vector(2.0, 0.0))
        assertEquals(90.0, east.theta.radiansToDegrees())
        val south = StraightTrajectory(Euclidean2DSpace.zero, vector(0.0, -2.0))
        assertEquals(180.0, south.theta.radiansToDegrees())
        val west = StraightTrajectory(Euclidean2DSpace.zero, vector(-2.0, 0.0))
        assertEquals(270.0, west.theta.radiansToDegrees())
    }
}
