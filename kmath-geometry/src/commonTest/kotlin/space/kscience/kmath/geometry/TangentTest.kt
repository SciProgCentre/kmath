/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import kotlin.test.Test
import kotlin.test.assertEquals

class TangentTest {
    @Test
    fun tangent() {
        val c1 = Circle2D(vector(0.0, 0.0), 1.0)
        val c2 = Circle2D(vector(4.0, 0.0), 1.0)
        val routes = arrayListOf<String>("RSR", "RSL", "LSR", "LSL")
        val segments = arrayListOf<Segment>(
            Segment(startPoint = vector(0.0, 1.0),
                terminalPoint = vector(4.0, 1.0)),
            Segment(startPoint = vector(0.5, 0.8660254),
                terminalPoint = vector(3.5, -0.8660254)),
            Segment(startPoint = vector(0.5, -0.8660254),
                terminalPoint = vector(3.5, 0.8660254)),
            Segment(startPoint = vector(0.0, -1.0),
                terminalPoint = vector(4.0, -1.0))
        )

        val tangentMap = tangentsToCircles(c1, c2)
        val tangentMapKeys = tangentMap.keys.toList()
        val tangentMapValues = tangentMap.values.toList()

        assertEquals(routes, tangentMapKeys)
        for (i in segments.indices) {
            assertEquals(segments[i], tangentMapValues[i])
        }
//        assertEquals(segments, tangentMapValues)
    }
}