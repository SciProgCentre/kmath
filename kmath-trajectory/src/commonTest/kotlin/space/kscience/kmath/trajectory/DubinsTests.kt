/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.equalsFloat
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class DubinsTests {

    @Test
    fun dubinsTest() = with(Euclidean2DSpace){
        val straight = StraightTrajectory2D(vector(0.0, 0.0), vector(100.0, 100.0))
        val lineP1 = straight.shift(1, 10.0).inverse()

        val start = DubinsPose2D(straight.end, straight.bearing)
        val end = DubinsPose2D(lineP1.begin, lineP1.bearing)
        val radius = 2.0
        val dubins = DubinsPath.all(start, end, radius)

        val absoluteDistance = start.distanceTo(end)
        println("Absolute distance: $absoluteDistance")

        val expectedLengths = mapOf(
            DubinsPath.Type.RLR to 13.067681939031397,
            DubinsPath.Type.RSR to 12.28318530717957,
            DubinsPath.Type.LSL to 32.84955592153878,
            DubinsPath.Type.RSL to 23.37758938854081,
            DubinsPath.Type.LSR to 23.37758938854081
        )

        expectedLengths.forEach {
            val path = dubins.find { p -> DubinsPath.trajectoryTypeOf(p) == it.key }
            assertNotNull(path, "Path ${it.key} not found")
            println("${it.key}: ${path.length}")
            assertTrue(it.value.equalsFloat(path.length))

            val a = path.segments[0] as CircleTrajectory2D
            val b = path.segments[1]
            val c = path.segments[2] as CircleTrajectory2D

            assertTrue(start.equalsFloat(a.start))
            assertTrue(end.equalsFloat(c.end))

            // Not working, theta double precision inaccuracy
            if (b is CircleTrajectory2D) {
                assertTrue(a.end.equalsFloat(b.start))
                assertTrue(c.start.equalsFloat(b.end))
            } else if (b is StraightTrajectory2D) {
                assertTrue(a.end.equalsFloat(DubinsPose2D(b.begin, b.bearing)))
                assertTrue(c.start.equalsFloat(DubinsPose2D(b.end, b.bearing)))
            }
        }
    }
}
