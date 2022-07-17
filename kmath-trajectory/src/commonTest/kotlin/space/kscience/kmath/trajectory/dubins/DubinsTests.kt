/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.dubins

import space.kscience.kmath.geometry.Euclidean2DSpace.distanceTo
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.equalFloat
import space.kscience.kmath.trajectory.equalsFloat
import space.kscience.kmath.trajectory.inverse
import space.kscience.kmath.trajectory.segments.Arc
import space.kscience.kmath.trajectory.segments.Straight
import space.kscience.kmath.trajectory.segments.components.Pose2D
import space.kscience.kmath.trajectory.shift
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class DubinsTests {

    @Test
    fun dubinsTest() {
        val straight = Straight(Vector2D(0.0, 0.0), Vector2D(100.0, 100.0))
        val lineP1 = straight.shift(1, 10.0).inverse()

        val start = Pose2D.of(straight.end, straight.theta)
        val end = Pose2D.of(lineP1.start, lineP1.theta)
        val radius = 2.0
        val dubins = DubinsPath.all(start, end, radius)

        val absoluteDistance = start.distanceTo(end)
        println("Absolute distance: $absoluteDistance")

        val expectedLengths = mapOf(
            DubinsPath.TYPE.RLR to 13.067681939031397,
            DubinsPath.TYPE.RSR to 12.28318530717957,
            DubinsPath.TYPE.LSL to 32.84955592153878,
            DubinsPath.TYPE.RSL to 23.37758938854081,
            DubinsPath.TYPE.LSR to 23.37758938854081
        )

        expectedLengths.forEach {
            val path = dubins.find { p -> p.type === it.key }
            assertNotNull(path, "Path ${it.key} not found")
            println("${it.key}: ${path.length}")
            assertTrue(it.value.equalFloat(path.length))

            assertTrue(start.equalsFloat(path.a.start))
            assertTrue(end.equalsFloat(path.c.end))

            // Not working, theta double precision inaccuracy
            if (path.b is Arc) {
                val b = path.b as Arc
                assertTrue(path.a.end.equalsFloat(b.start))
                assertTrue(path.c.start.equalsFloat(b.end))
            } else if (path.b is Straight) {
                val b = path.b as Straight
                assertTrue(path.a.end.equalsFloat(Pose2D.of(b.start, b.theta)))
                assertTrue(path.c.start.equalsFloat(Pose2D.of(b.end, b.theta)))
            }
        }
    }
}
