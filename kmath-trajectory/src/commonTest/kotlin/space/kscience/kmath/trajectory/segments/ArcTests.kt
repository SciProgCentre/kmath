/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.geometry.circumference
import space.kscience.kmath.trajectory.ArcSegment
import space.kscience.kmath.trajectory.radiansToDegrees
import kotlin.test.Test
import kotlin.test.assertEquals

class ArcTests {

    @Test
    fun arcTest() {
        val circle = Circle2D(Vector2D(0.0, 0.0), 2.0)
        val arc = ArcSegment.of(circle.center, Vector2D(-2.0, 0.0), Vector2D(0.0, 2.0), ArcSegment.Direction.RIGHT)
        assertEquals(circle.circumference / 4, arc.length, 1.0)
        assertEquals(0.0, arc.start.theta.radiansToDegrees())
        assertEquals(90.0, arc.end.theta.radiansToDegrees())
    }
}
