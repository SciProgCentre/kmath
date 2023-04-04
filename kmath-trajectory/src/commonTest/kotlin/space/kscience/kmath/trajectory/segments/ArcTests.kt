/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.circumference
import space.kscience.kmath.geometry.degrees
import space.kscience.kmath.trajectory.CircleTrajectory2D
import space.kscience.kmath.trajectory.Trajectory2D
import kotlin.test.Test
import kotlin.test.assertEquals

class ArcTests {

    @Test
    fun arcTest() = with(Euclidean2DSpace){
        val circle = Circle2D(vector(0.0, 0.0), 2.0)
        val arc = CircleTrajectory2D.of(
            circle.center,
            vector(-2.0, 0.0),
            vector(0.0, 2.0),
            Trajectory2D.R
        )
        assertEquals(circle.circumference / 4, arc.length, 1.0)
        assertEquals(0.0, arc.start.bearing.degrees)
        assertEquals(90.0, arc.end.bearing.degrees)
    }
}
