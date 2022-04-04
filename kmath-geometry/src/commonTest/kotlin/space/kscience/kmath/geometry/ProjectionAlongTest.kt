/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.minus
import kotlin.test.Test
import kotlin.test.assertTrue

internal class ProjectionAlongTest {
    @Test
    fun projectionIntoYEqualsX() {
        with(Euclidean2DSpace) {
            val normal = Vector2D(-2.0, 2.0)
            val base = Vector2D(2.3, 2.3)

            assertVectorEquals(zero, projectAlong(zero, normal, base))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val d = (y - x) / 2.0
                assertVectorEquals(Vector2D(x + d, y - d), projectAlong(Vector2D(x, y), normal, base))
            }
        }
    }

    @Test
    fun projectionOntoLine() {
        with(Euclidean2DSpace) {
            val a = 5.0
            val b = -3.0
            val c = -15.0
            val normal = Vector2D(-5.0, 3.0)
            val base = Vector2D(3.0, 0.0)

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val xProj = (b * (b * x - a * y) - a * c) / (a * a + b * b)
                val yProj = (a * (-b * x + a * y) - b * c) / (a * a + b * b)
                assertVectorEquals(Vector2D(xProj, yProj), projectAlong(Vector2D(x, y), normal, base))
            }
        }
    }

    @Test
    fun projectOntoPlane() {
        val normal = Vector3D(1.0, 3.5, 0.07)
        val base = Vector3D(2.0, -0.0037, 11.1111)

        with(Euclidean3DSpace) {
            val testDomain = (-10.0..10.0).generateList(0.43)
            for (x in testDomain) {
                for (y in testDomain) {
                    for (z in testDomain) {
                        val v = Vector3D(x, y, z)
                        val result = projectAlong(v, normal, base)

                        // assert that result is on plane
                        assertTrue(isOrthogonal(result - base, normal))
                        // assert that PV vector is collinear to normal vector
                        assertTrue(isCollinear(v - result, normal))
                    }
                }
            }
        }
    }
}
