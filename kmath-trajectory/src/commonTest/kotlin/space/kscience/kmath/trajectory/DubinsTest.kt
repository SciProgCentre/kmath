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
        println(length)
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
            DubinsObstacle(listOf(
                Circle2D(vector(1.0, 6.5), 0.5),
                Circle2D(vector(2.0, 1.0), 0.5),
                Circle2D(vector(6.0, 0.0), 0.5),
                Circle2D(vector(5.0, 5.0), 0.5)
            )), DubinsObstacle(listOf(
                Circle2D(vector(10.0, 1.0), 0.5),
                Circle2D(vector(16.0, 0.0), 0.5),
                Circle2D(vector(14.0, 6.0), 0.5),
                Circle2D(vector(9.0, 4.0), 0.5)
            ))
        )
        val outputTangents = findAllPaths(
            startPoint,
            startDirection,
            startRadius,
            finalPoint,
            finalDirection,
            finalRadius,
            obstacles)
        val length = pathLength(shortestPath(outputTangents))
        println(length)
    }
    @Test
    fun outerTangentsTest1() {
        // works incorrectly
        val circles1 = listOf(
            Circle2D(vector(0.0, 0.0), 1.0))
        val circles2 = listOf(
            Circle2D(vector(5.0, 5.0), 1.0)
        )
        println(outerTangents(DubinsObstacle(circles1), DubinsObstacle(circles2)))
        assertTrue(false)
    }
    @Test
    fun outerTangentsTest2() {
        // works incorrectly
        val circles1 = listOf(
            Circle2D(vector(0.0, 0.0), 1.0),
            Circle2D(vector( 2.0, 0.0), 1.0))
        val circles2 = listOf(
            Circle2D(vector(5.0, 5.0), 1.0),
            Circle2D(vector(7.0, 5.0), 1.0)
        )
        println(outerTangents(DubinsObstacle(circles1), DubinsObstacle(circles2)))

        for (circle1 in circles1) {
            for (circle2 in circles2) {
                for (tangent in dubinsTangentsToCircles(circle1, circle2,
                    DubinsObstacle(circles1), DubinsObstacle(circles2))) {
                    println(tangent)
                }
            }
        }
        assertTrue(false)
    }
}