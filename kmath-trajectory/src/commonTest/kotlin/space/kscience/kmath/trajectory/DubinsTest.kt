/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import kotlin.test.Test
import kotlin.test.assertTrue

class DubinsTest {
    @Test
    fun firstPath() {
        val startPoint = vector(-5.0, -1.0)
        val startDirection = vector(1.0, 1.0)
        val startRadius = 0.5
        val finalPoint = vector(20.0, 4.0)
        val finalDirection = vector(1.0, -1.0)
        val finalRadius = 0.5

        val obstacles = listOf(DubinsObstacle(listOf(
            Circle2D(vector(7.0, 1.0), 5.0))))

        val outputTangents = findAllPaths(
            startPoint,
            startDirection,
            startRadius,
            finalPoint,
            finalDirection,
            finalRadius,
            obstacles)
        val length = pathLength(shortestPath(outputTangents))
        TODO("fix negative indices in boundaryTangents and accomplish test")
        assertTrue(false)
    }
}