/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace.vector
import kotlin.test.Test
import kotlin.test.assertEquals

class ObstacleTest {
    @Test
    fun firstPath() {
        val startPoint = vector(-5.0, -1.0)
        val startDirection = vector(1.0, 1.0)
        val startRadius = 0.5
        val finalPoint = vector(20.0, 4.0)
        val finalDirection = vector(1.0, -1.0)
        val finalRadius = 0.5

        val obstacles = listOf(
            Obstacle(
                listOf(
                    Circle2D(vector(7.0, 1.0), 5.0)
                )
            )
        )

        val outputTangents = findAllPaths(
            DubinsPose2D.of(startPoint, startDirection),
            startRadius,
            DubinsPose2D.of(finalPoint, finalDirection),
            finalRadius,
            obstacles
        )
        val length = outputTangents.minOf { it.length }
        assertEquals(27.2113183, length, 1e-6)
    }

    @Test
    fun secondPath() {
        val startPoint = vector(-5.0, -1.0)
        val startDirection = vector(1.0, 1.0)
        val startRadius = 0.5
        val finalPoint = vector(20.0, 4.0)
        val finalDirection = vector(1.0, -1.0)
        val finalRadius = 0.5

        val obstacles = listOf(
            Obstacle(
                listOf(
                    Circle2D(vector(1.0, 6.5), 0.5),
                    Circle2D(vector(2.0, 1.0), 0.5),
                    Circle2D(vector(6.0, 0.0), 0.5),
                    Circle2D(vector(5.0, 5.0), 0.5)
                )
            ), Obstacle(
                listOf(
                    Circle2D(vector(10.0, 1.0), 0.5),
                    Circle2D(vector(16.0, 0.0), 0.5),
                    Circle2D(vector(14.0, 6.0), 0.5),
                    Circle2D(vector(9.0, 4.0), 0.5)
                )
            )
        )
        val paths = findAllPaths(
            DubinsPose2D.of(startPoint, startDirection),
            startRadius,
            DubinsPose2D.of(finalPoint, finalDirection),
            finalRadius,
            obstacles
        )
        val length = paths.minOf { it.length }
        assertEquals(28.9678224, length, 1e-6)
    }

    @Test
    fun equalObstacles() {
        val circle1 = Circle2D(vector(1.0, 6.5), 0.5)
        val circle2 = Circle2D(vector(1.0, 6.5), 0.5)
        assertEquals(circle1, circle2)
        val obstacle1 = Obstacle(listOf(circle1))
        val obstacle2 = Obstacle(listOf(circle2))
        assertEquals(obstacle1, obstacle2)
    }
}