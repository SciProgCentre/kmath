/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertTrue

internal class ProjectionAlongTest {
    @Test
    fun projectionIntoYEqualsX() {
        with(Euclidean2DSpace) {
            val normal = vector(-2.0, 2.0)
            val base = vector(2.3, 2.3)

            assertVectorEquals(zero, projectAlong(zero, normal, base))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val d = (y - x) / 2.0
                assertVectorEquals(vector(x + d, y - d), projectAlong(vector(x, y), normal, base))
            }
        }
    }

    @Test
    fun projectionOntoLine() {
        with(Euclidean2DSpace) {
            val a = 5.0
            val b = -3.0
            val c = -15.0
            val normal = vector(-5.0, 3.0)
            val base = vector(3.0, 0.0)

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val xProj = (b * (b * x - a * y) - a * c) / (a * a + b * b)
                val yProj = (a * (-b * x + a * y) - b * c) / (a * a + b * b)
                assertVectorEquals(vector(xProj, yProj), projectAlong(vector(x, y), normal, base))
            }
        }
    }

    @Test
    fun projectOntoPlane() = with(Euclidean3DSpace){
        val normal = vector(1.0, 3.5, 0.07)
        val base = vector(2.0, -0.0037, 11.1111)

        with(Euclidean3DSpace) {
            val testDomain = (-10.0..10.0).generateList(0.43)
            for (x in testDomain) {
                for (y in testDomain) {
                    for (z in testDomain) {
                        val v = vector(x, y, z)
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
