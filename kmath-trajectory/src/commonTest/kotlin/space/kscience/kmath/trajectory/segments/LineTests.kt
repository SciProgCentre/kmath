/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.degrees
import space.kscience.kmath.trajectory.StraightTrajectory2D
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LineTests {

    @Test
    fun lineTest() = with(Euclidean2DSpace){
        val straight = StraightTrajectory2D(vector(0.0, 0.0), vector(100.0, 100.0))
        assertEquals(sqrt(100.0.pow(2) + 100.0.pow(2)), straight.length)
        assertEquals(45.0, straight.bearing.degrees)
    }

    @Test
    fun lineAngleTest() = with(Euclidean2DSpace){
        //val zero = Vector2D(0.0, 0.0)
        val north = StraightTrajectory2D(zero, vector(0.0, 2.0))
        assertEquals(0.0, north.bearing.degrees)
        val east = StraightTrajectory2D(zero, vector(2.0, 0.0))
        assertEquals(90.0, east.bearing.degrees)
        val south = StraightTrajectory2D(zero, vector(0.0, -2.0))
        assertEquals(180.0, south.bearing.degrees)
        val west = StraightTrajectory2D(zero, vector(-2.0, 0.0))
        assertEquals(270.0, west.bearing.degrees)
    }
}
