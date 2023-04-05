/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertTrue

internal class ProjectionOntoLineTest {
    @Test
    fun projectionIntoOx() {
        with(Euclidean2DSpace) {
            val ox = Line(zero, vector(1.0, 0.0))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                assertVectorEquals(vector(x, 0.0), projectToLine(vector(x, y), ox))
            }
        }
    }

    @Test
    fun projectionIntoOy() {
        with(Euclidean2DSpace) {
            val line = Line(zero, vector(0.0, 1.0))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                assertVectorEquals(vector(0.0, y), projectToLine(vector(x, y), line))
            }
        }
    }

    @Test
    fun projectionIntoYEqualsX() {
        with(Euclidean2DSpace) {
            val line = Line(zero, vector(1.0, 1.0))

            assertVectorEquals(zero, projectToLine(zero, line))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val d = (y - x) / 2.0
                assertVectorEquals(vector(x + d, y - d), projectToLine(vector(x, y), line))
            }
        }
    }

    @Test
    fun projectionOntoLine2d() {
        with(Euclidean2DSpace) {
            val a = 5.0
            val b = -3.0
            val c = -15.0
            val line = Line(vector(3.0, 0.0), vector(3.0, 5.0))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val xProj = (b * (b * x - a * y) - a * c) / (a * a + b * b)
                val yProj = (a * (-b * x + a * y) - b * c) / (a * a + b * b)
                assertVectorEquals(vector(xProj, yProj), projectToLine(vector(x, y), line))
            }
        }
    }

    @Test
    fun projectionOntoLine3d() = with(Euclidean3DSpace) {
        val line = Line(
            base = vector(1.0, 3.5, 0.07),
            direction = vector(2.0, -0.0037, 11.1111)
        )


        val testDomain = (-10.0..10.0).generateList(0.43)
        for (x in testDomain) {
            for (y in testDomain) {
                for (z in testDomain) {
                    val v = vector(x, y, z)
                    val result = projectToLine(v, line)

                    // assert that result is on the line
                    assertTrue(isCollinear(result - line.start, line.direction))
                    // assert that PV vector is orthogonal to direction vector
                    assertTrue(isOrthogonal(v - result, line.direction))
                }
            }
        }

    }
}
