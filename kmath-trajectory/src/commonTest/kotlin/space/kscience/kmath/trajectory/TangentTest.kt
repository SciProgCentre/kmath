/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import space.kscience.kmath.geometry.LineSegment
import space.kscience.kmath.geometry.equalsLine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class TangentTest {
    @Test
    fun tangents() {
        val c1 = Circle2D(vector(0.0, 0.0), 1.0)
        val c2 = Circle2D(vector(4.0, 0.0), 1.0)
        val routes = listOf(
            DubinsPath.Type.RSR,
            DubinsPath.Type.RSL,
            DubinsPath.Type.LSR,
            DubinsPath.Type.LSL
        )
        val segments = listOf(
            LineSegment(
                begin = vector(0.0, 1.0),
                end = vector(4.0, 1.0)
            ),
            LineSegment(
                begin = vector(0.5, 0.8660254),
                end = vector(3.5, -0.8660254)
            ),
            LineSegment(
                begin = vector(0.5, -0.8660254),
                end = vector(3.5, 0.8660254)
            ),
            LineSegment(
                begin = vector(0.0, -1.0),
                end = vector(4.0, -1.0)
            )
        )

        val tangentMap = c1.tangentsToCircle(c2)
        val tangentMapKeys = tangentMap.keys.toList()
        val tangentMapValues = tangentMap.values.toList()

        assertEquals(routes, tangentMapKeys)
        for (i in segments.indices) {
            assertTrue(segments[i].equalsLine(Euclidean2DSpace, tangentMapValues[i]))
        }
    }

    @Test
    fun nonExistingTangents() {
        assertFailsWith<Exception> {
            val c1 = Circle2D(vector(0.0, 0.0), 10.0)
            val c2 = Circle2D(vector(0.0, 0.0), 1.0)
            val segments = c1.tangentsToCircle(c2)
        }
        assertFailsWith<Exception> {
            val c1 = Circle2D(vector(0.0, 0.0), 1.0)
            val c2 = Circle2D(vector(0.0, 0.0), 10.0)
            val segments = c1.tangentsToCircle(c2)
        }
        assertFailsWith<Exception> {
            val c1 = Circle2D(vector(0.0, 0.0), 1.0)
            val c2 = Circle2D(vector(2.0, 0.0), 1.0)
            val segments = c1.tangentsToCircle(c2)
        }
        assertFailsWith<Exception> {
            val c1 = Circle2D(vector(0.0, 0.0), 1.0)
            val c2 = Circle2D(vector(0.5, 0.0), 1.0)
            val segments = c1.tangentsToCircle(c2)
        }
    }
}